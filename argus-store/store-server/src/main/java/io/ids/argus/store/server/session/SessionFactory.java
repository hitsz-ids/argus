package io.ids.argus.store.server.session;

import io.ids.argus.store.server.db.job.session.JobSession;
import io.ids.argus.store.transport.grpc.SessionType;

public class SessionFactory {
    public static ArgusSession<?> create(SessionType sessionType) {
        ArgusSession<?> session;
        switch (sessionType) {
            case JOB -> session = new JobSession();
            default -> session = null;
        }
        return session;
    }
}
