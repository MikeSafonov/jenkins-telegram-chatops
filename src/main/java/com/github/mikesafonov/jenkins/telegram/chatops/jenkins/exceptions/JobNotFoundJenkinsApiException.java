package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions;

import lombok.Getter;

/**
 * @author Mike Safonov
 */
@Getter
public class JobNotFoundJenkinsApiException extends JenkinsApiException {

    public JobNotFoundJenkinsApiException(String jobName) {
        super("Job " + jobName + " not found");
    }

    public JobNotFoundJenkinsApiException(String jobName, Throwable cause) {
        super("Job " + jobName + " not found", cause);
    }
}
