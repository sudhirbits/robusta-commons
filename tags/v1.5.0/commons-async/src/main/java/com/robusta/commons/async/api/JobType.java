package com.robusta.commons.async.api;

import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public class JobType {
    protected String jobTypeAsString;
    private static int JOB_TYPE_MAX_LENGTH = 50;

    private JobType(String jobTypeAsString) {
        checkArgument(!isNullOrEmpty(jobTypeAsString));
        this.jobTypeAsString = StringUtils.substring(jobTypeAsString, 0, JOB_TYPE_MAX_LENGTH);
    }

    public static JobType with(String jobType) {
        return new JobType(jobType);
    }

    public static void setMaxJobTypeLength(int maxLength) {
        checkArgument(maxLength > 10, "Max Job Type Length of less than 10 characters is unsupported");
        JOB_TYPE_MAX_LENGTH = maxLength;
    }

    @Override
    public String toString() {
        return jobTypeAsString;
    }
}
