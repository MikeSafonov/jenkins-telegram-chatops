package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions;

/**
 * @author Mike Safonov
 */
public class RunJobJenkinsApiException extends JenkinsApiException {
    public RunJobJenkinsApiException(String message) {
        super(message);
    }

    public RunJobJenkinsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
