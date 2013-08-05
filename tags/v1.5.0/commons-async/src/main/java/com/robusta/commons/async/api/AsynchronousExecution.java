package com.robusta.commons.async.api;

import com.robusta.commons.domain.user.User;

public interface AsynchronousExecution<Activity extends AsynchronousActivity> {
    void performAsynchronously(User user, Long jobId);
    JobType jobType();
    void setPerformer(Activity performer);
}
