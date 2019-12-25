package com.github.mikesafonov.jenkins.telegram.chatops.dto;

import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
public class JobToRun {
    /**
     * Jenkins job name
     */
    private final String jobName;
    /**
     * ID of user which run job
     */
    private final Long userId;
}
