package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions;

import lombok.Getter;

/**
 * @author Mike Safonov
 */
@Getter
public class QueueItemNotFoundJenkinsApiException extends JenkinsApiException {

    public QueueItemNotFoundJenkinsApiException(String queueItem) {
        super("Queue item " + queueItem + " not found");
    }

    public QueueItemNotFoundJenkinsApiException(String queueItem, Throwable cause) {
        super("Queue item " + queueItem + " not found", cause);
    }
}
