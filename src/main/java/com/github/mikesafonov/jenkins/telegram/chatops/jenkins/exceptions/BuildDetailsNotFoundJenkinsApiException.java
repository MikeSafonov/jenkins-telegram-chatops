package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions;

/**
 * @author Mike Safonov
 */
public class BuildDetailsNotFoundJenkinsApiException extends JenkinsApiException {
    public BuildDetailsNotFoundJenkinsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
