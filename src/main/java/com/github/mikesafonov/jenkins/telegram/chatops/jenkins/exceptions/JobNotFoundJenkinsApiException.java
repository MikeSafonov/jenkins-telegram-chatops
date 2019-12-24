package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions;

import lombok.Getter;

/**
 * @author Mike Safonov
 */
@Getter
public class JobNotFoundJenkinsApiException extends JenkinsApiException {
    private String jobName;

    public JobNotFoundJenkinsApiException(String jobName) {
        super("Job " + jobName + " not found");
        this.jobName = jobName;
    }

    public JobNotFoundJenkinsApiException(String jobName, Throwable cause) {
        super("Job " + jobName + " not found", cause);
        this.jobName = jobName;
    }
}
