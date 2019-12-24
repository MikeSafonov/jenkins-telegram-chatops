package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions;

/**
 * @author Mike Safonov
 */
public abstract class JenkinsApiException extends RuntimeException {
    public JenkinsApiException(String message) {
        super(message);
    }

    public JenkinsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
