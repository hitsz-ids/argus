package io.ids.argus.center.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import io.ids.argus.center.common.ExecuteStatus;
import io.ids.argus.center.startup.Command;
import io.ids.argus.center.exception.ArgusExecuteException;
import io.ids.argus.core.conf.ArgusLogger;
import io.ids.argus.core.json.ArgusJson;
import io.ids.argus.core.json.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RequestManager {
    private final static RequestManager instance = new RequestManager();
    private final ArgusLogger log = new ArgusLogger(RequestManager.class);
    public static class Request {
        private final StringBuffer response = new StringBuffer();
        private final Command command;
        private final CountDownLatch latch = new CountDownLatch(1);

        public Request(Command command) {
            this.command = command;
        }

        public Command getCommand() {
            return command;
        }

        public CountDownLatch getLatch() {
            return latch;
        }

        public void append(String data) {
            response.append(data);
        }

        public ArgusJson getResponse() {
            return Transformer.parseObject(response.toString());
        }

        public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        public void countDown() {
            latch.countDown();
        }
    }

    private final Cache<String, Request> pool;

    private RequestManager() {
        pool = CacheBuilder.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .expireAfterAccess(60, TimeUnit.SECONDS)
                .removalListener(notification -> {
                    RemovalCause cause = notification.getCause();
                    if (cause == RemovalCause.EXPIRED) {
                        log.error("消息：{}，请求超时", notification.getKey());
                    }
                })
                .build();
    }

    public static RequestManager get() {
        return instance;
    }

    public ArgusJson execute(Connection connection, Command command) {
        try {
            var request = new Request(command);
            pool.put(command.getUuid(), request);
            var pass = connection.produce(command);
            if (!pass) {
                throw new ArgusExecuteException(ExecuteStatus.EXECUTE_COMMAND_TIME_OUT);
            }
            pass = request.await(60, TimeUnit.SECONDS);
            if (!pass) {
                throw new ArgusExecuteException(ExecuteStatus.EXECUTE_COMMAND_TIME_OUT);
            }
            return request.getResponse();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ArgusExecuteException(ExecuteStatus.EXECUTE_INTERRUPTED);
        }
    }

    public Request pop(String requestId) {
        if (StringUtils.isEmpty(requestId)) {
            return null;
        }
        return pool.asMap().remove(requestId);
    }
}
