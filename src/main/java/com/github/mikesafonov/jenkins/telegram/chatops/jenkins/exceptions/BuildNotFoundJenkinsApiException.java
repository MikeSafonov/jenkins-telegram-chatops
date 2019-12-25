package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions;

import lombok.Getter;

/**
 * @author Mike Safonov
 */
@Getter
public class BuildNotFoundJenkinsApiException extends JenkinsApiException {

    public BuildNotFoundJenkinsApiException(Long id) {
        super("Build for queue item with id " + id + " not found");
    }

    public BuildNotFoundJenkinsApiException(Long id, Throwable cause) {
        super("Build for queue item with id " + id + " not found", cause);
    }
}
