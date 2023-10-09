package io.ids.argus.store.server.session;

import io.ids.argus.store.grpc.SessionType;
import io.ids.argus.store.server.db.job.session.JobSqlSession;

public class SessionFactory {
    public static ArgusSqlSession<?> create(SessionType sessionType) {
        ArgusSqlSession<?> session;
        switch (sessionType) {
            case JOB -> session = new JobSqlSession();
            default -> session = null;
        }
        return session;
    }
}
