package io.ids.argus.store.server.db.job.session;

import io.ids.argus.store.server.db.job.mapper.JobMapper;
import io.ids.argus.store.server.session.ArgusSession;

public class JobSession extends ArgusSession<JobMapper> {
    public JobSession() {
        super(JobMapper.class);
    }
}
