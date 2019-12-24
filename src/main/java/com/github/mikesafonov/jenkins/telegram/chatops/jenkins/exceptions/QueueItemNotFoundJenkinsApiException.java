package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions;

import lombok.Getter;

/**
 * @author Mike Safonov
 */
@Getter
public class QueueItemNotFoundJenkinsApiException extends JenkinsApiException {
    private String queueItem;

    public QueueItemNotFoundJenkinsApiException(String queueItem) {
        super("Queue item " + queueItem + " not found");
        this.queueItem = queueItem;
    }

    public QueueItemNotFoundJenkinsApiException(String queueItem, Throwable cause) {
        super("Queue item " + queueItem + " not found", cause);
        this.queueItem = queueItem;
    }
}
