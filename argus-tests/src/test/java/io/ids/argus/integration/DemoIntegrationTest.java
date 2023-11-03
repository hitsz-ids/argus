package io.ids.argus.integration;

import io.ids.argus.base.BaseHttpTest;
import io.ids.argus.configuration.EnvProperties;
import io.ids.argus.core.base.json.ArgusJson;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.entry.Application;
import io.ids.argus.extension.demo.DemoApplication;
import io.ids.argus.job.server.JobApplication;
import io.ids.argus.store.server.StoreApplication;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author jalr4real[jalrhex@gmail.com]
 * @since 2023-11-02
 */

public class DemoIntegrationTest extends BaseHttpTest {


    private final ArgusLogger log = new ArgusLogger(DemoIntegrationTest.class);

    private static final String DEMO_SERVICE_URL = EnvProperties.get().getDemoHost() + "/argus/execute";

    private static final ExecutorService INTEGRATION_TEST_THREAD_POOL = new ThreadPoolExecutor(8, 64, 0L, java.util.concurrent.TimeUnit.MILLISECONDS, new SynchronousQueue<>(), new ThreadFactory() {
        private int count = 0;

        @Override
        public Thread newThread(@NotNull Runnable r) {
            count++;
            return new Thread(r, "argus-test-" + count);
        }
    }, new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());

    @BeforeAll
    static void init() throws InterruptedException {

        // TODO: 2023/11/2 refactor this code inject configuration and using docker to support db service.

        Semaphore storeRunning = new Semaphore(1);
        Semaphore jobRunning = new Semaphore(0);
        Semaphore httpRunning = new Semaphore(0);
        Semaphore demoRunning = new Semaphore(0);
        Semaphore mainRunning = new Semaphore(0);
        CountDownLatch last = new CountDownLatch(1);

        // store server
        CompletableFuture.runAsync(() -> {
            try {
                storeRunning.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            StoreApplication.register(jobRunning::release);
            StoreApplication.main(new String[0]);

        }, INTEGRATION_TEST_THREAD_POOL);

        // job server
        CompletableFuture.runAsync(() -> {
            try {
                jobRunning.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            JobApplication.register(httpRunning::release);
            JobApplication.main(new String[0]);
        }, INTEGRATION_TEST_THREAD_POOL);

        // http server
        CompletableFuture.runAsync(() -> {
            try {
                httpRunning.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Application.register(demoRunning::release);
            Application.main(new String[0]);
        }, INTEGRATION_TEST_THREAD_POOL);

        // demo service
        CompletableFuture.runAsync(() -> {
            try {
                demoRunning.acquire();
                DemoApplication.register(mainRunning::release);
                last.countDown();
                DemoApplication.main(new String[0]);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, INTEGRATION_TEST_THREAD_POOL);

        // Wait last one boot completed.
        last.await();
        mainRunning.acquire();
    }


    @Test
    void demoJobCase() {
        // create job
        ArgusJson param = new ArgusJson();
        param.add("path", "demo/1.0.0/test/job");
        ArgusJson createParam = new ArgusJson();
        createParam.add("name", "task-" + UUID.randomUUID());
        param.add("params", createParam);
        Response response = post(DEMO_SERVICE_URL, param.toJsonString());
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        AtomicReference<String> seq = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> {
            Map v = Transformer.fromJson(Objects.requireNonNull(response.body()).string(), Map.class);
            seq.set(v.get("seq").toString());
            Assertions.assertNotNull(seq.get());

        });

        log.info("job seq: {}", seq.get());

        // stop job
        ArgusJson param2 = new ArgusJson();
        param2.add("path", "demo/1.0.0/@stop-job@");
        ArgusJson stopParam = new ArgusJson();
        stopParam.add("seq", seq.get());
        param2.add("params", stopParam);

        Response response2 = post(DEMO_SERVICE_URL, param2.toJsonString());
        Assertions.assertNotNull(response2);
        Assertions.assertTrue(response2.isSuccessful());
        Assertions.assertDoesNotThrow(() -> {
            Map v = Transformer.fromJson(Objects.requireNonNull(response2.body()).string(), Map.class);
            Assertions.assertEquals(v.get("code"), 1);
        });
    }

}
