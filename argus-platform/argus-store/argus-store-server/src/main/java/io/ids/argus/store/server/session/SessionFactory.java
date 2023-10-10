package io.ids.argus.store.server.session;

import io.ids.argus.store.grpc.SessionType;
import io.ids.argus.store.server.db.job.session.JobSqlStoreSession;

public class SessionFactory {
    public static ArgusStoreSession create(SessionType sessionType) {
        ArgusStoreSession session;
        switch (sessionType) {
            case JOB -> session = new JobSqlStoreSession();
            default -> session = null;
        }
        return session;
    }
}
