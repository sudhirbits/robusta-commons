package com.robusta.commons.async.defaults.jobops;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class JobId {
    private final Long jobId;
    JobId(Long jobId) {
        this.jobId = jobId;
    }

    static JobId with(Long jobId) {
        return new JobId(jobId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("jobId", jobId).
                toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobId jobId1 = (JobId) o;
        return !(jobId != null ? !jobId.equals(jobId1.jobId) : jobId1.jobId != null);
    }

    @Override
    public int hashCode() {
        return jobId != null ? jobId.hashCode() : 0;
    }
}