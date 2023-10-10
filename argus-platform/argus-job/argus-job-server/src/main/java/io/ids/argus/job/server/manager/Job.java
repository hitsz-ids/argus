package io.ids.argus.job.server.manager;

import io.ids.argus.core.conf.exception.ArgusCreateClassException;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.job.base.ConnectorId;
import io.ids.argus.job.grpc.*;
import io.ids.argus.job.server.exception.ArgusJobServerCallbackException;
import io.ids.argus.store.client.ArgusStore;
import io.ids.argus.store.client.session.JobSession;
import io.ids.argus.store.grpc.job.JobStoreStatusEnum;
import io.ids.argus.store.grpc.job.UpdateStatusRequest;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Job implements Runnable {
    private final BlockingQueue<Command> queue = new LinkedBlockingQueue<>();
    private final JobData data;
    private final ArgusLogger log = new ArgusLogger(Job.class);

    public Job(JobData data) {
        this.data = data;
        try {
            queue.put(Command.builder()
                    .operation(Operation.EXECUTE)
                    .build());
        } catch (InterruptedException e) {
            throw new ArgusCreateClassException("任务创建失败");
        }
    }

    public Job(String seq) {
        data = new JobData(seq, null, null, null, null);
    }

    @Override
    public void run() {
        JobManager.get().bind(this);
        while (true) {
            Command command = null;
            try {
                command = queue.take();
                TimeUnit.SECONDS.sleep(command.getDelay());
                var connector = ConnectorManager.get().get(data.connectorId());
                if (Objects.isNull(connector)) {
                    command.setDelay(10);
                    queue.add(command);
                    continue;
                }
                boolean success = false;
                if (command.getOperation() == Operation.EXECUTE) {
                    execute(connector, command);
                    continue;
                }
                if (command.getOperation() == Operation.FAILED) {
                    success = fail(connector, command);
                } else if (command.getOperation() == Operation.COMPLETED) {
                    success = complete(connector, command);
                } else if (command.getOperation() == Operation.STOP) {
                    success = stop(connector, command);
                }
                if (success) {
                    break;
                }
                retry(command);
            } catch (Exception e) {
                if (Objects.isNull(command)) {
                    break;
                }
                retry(command);
            }
        }
        JobManager.get().unBind(this);
    }

    private void retry(Command command) {
        command.setDelay(10);
        queue.add(command);
    }

    private void execute(Connector connector, Command command) {
        callback(connector,
                ExecuteState.newBuilder()
                        .setJob(data.job())
                        .setParams(data.params())
                        .build(),
                command);
        try (var session = ArgusStore.get().open(JobSession.class)) {
            session.updateStatus(UpdateStatusRequest.newBuilder()
                    .setSeq(data.seq())
                    .setStatus(JobStoreStatusEnum.EXECUTING)
                    .build());
            session.commit();
        }
        log.debug("任务：{}, 正在执行", data.seq);
    }

    private boolean fail(Connector connector, Command command) {
        try (var session = ArgusStore.get().open(JobSession.class)) {
            session.updateStatus(UpdateStatusRequest.newBuilder()
                    .setSeq(data.seq())
                    .setStatus(JobStoreStatusEnum.FAILING)
                    .build());
            session.commit();
        }
        callback(connector,
                FailedState.newBuilder().build(),
                command);
        try (var session = ArgusStore.get().open(JobSession.class)) {
            session.updateStatus(UpdateStatusRequest.newBuilder()
                    .setSeq(data.seq())
                    .setStatus(JobStoreStatusEnum.FAILED)
                    .build());
            session.commit();
        }
        log.debug("任务：{}, 执行失败", data.seq);
        return true;
    }

    private boolean complete(Connector connector, Command command) {
        try (var session = ArgusStore.get().open(JobSession.class)) {
            var status = session.readState(data.seq);
            if (!Objects.equals(status, JobStoreStatusEnum.EXECUTING)) {
                return false;
            }
            session.updateStatus(UpdateStatusRequest.newBuilder()
                    .setSeq(data.seq)
                    .setStatus(JobStoreStatusEnum.COMPLETING)
                    .build());
            session.commit();
        }
        callback(connector,
                CompleteState.newBuilder().build(),
                command);
        try (var session = ArgusStore.get().open(JobSession.class)) {
            session.updateStatus(UpdateStatusRequest.newBuilder()
                    .setSeq(data.seq())
                    .setStatus(JobStoreStatusEnum.COMPLETED)
                    .build());
            session.commit();
        }
        log.debug("任务：{}, 执行完成", data.seq);
        return true;
    }

    private boolean stop(Connector connector, Command command) {
        try (var session = ArgusStore.get().open(JobSession.class)) {
            var status = session.readState(data.seq);
            if (!Objects.equals(status, JobStoreStatusEnum.EXECUTING)) {
                return false;
            }
            session.updateStatus(UpdateStatusRequest.newBuilder()
                    .setSeq(data.seq())
                    .setStatus(JobStoreStatusEnum.STOPPING)
                    .build());
            session.commit();
        }
        callback(connector,
                StopState.newBuilder().build(),
                command);
        try (var session = ArgusStore.get().open(JobSession.class)) {
            session.updateStatus(UpdateStatusRequest.newBuilder()
                    .setSeq(data.seq())
                    .setStatus(JobStoreStatusEnum.STOPPED)
                    .build());
            session.commit();
        }
        log.debug("任务：{}, 已经停止", data.seq);
        return true;
    }

    private void callback(Connector connector,
                          com.google.protobuf.Message message,
                          Command command)
            throws ArgusJobServerCallbackException {
        try {
            connector.callback(
                    data.seq(),
                    message);
        } catch (ArgusJobServerCallbackException e) {
            log.error(e.getMessage(), e);
            command.setDelay(10);
            queue.add(command);
            throw e;
        }
    }

    public boolean publishCommand(Command command) {
        try {
            return queue.offer(command, 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public String seq() {
        return data.seq();
    }

    public String params() {
        return data.params();
    }

    public String name() {
        return data.name();
    }

    public String job() {
        return data.job();
    }

    public record JobData(String seq, ConnectorId connectorId, String params, String name, String job) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JobData jobData = (JobData) o;
            return Objects.equals(seq, jobData.seq);
        }

        @Override
        public int hashCode() {
            return Objects.hash(seq);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(data, job.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
