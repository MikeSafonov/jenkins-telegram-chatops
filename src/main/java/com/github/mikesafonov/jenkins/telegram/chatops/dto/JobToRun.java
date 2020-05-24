package com.github.mikesafonov.jenkins.telegram.chatops.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.Map;

/**
 * @author Mike Safonov
 */
@Value
@AllArgsConstructor
public class JobToRun {

    /**
     * Jenkins job name
     */
    private final String jobName;
    /**
     * ID of user which run job
     */
    private final Long userId;

    /**
     * Optional job parameters
     */
    private final Map<String, String> parameters;

    public JobToRun(String jobName, Long userId) {
        this.jobName = jobName;
        this.userId = userId;
        parameters = Collections.emptyMap();
    }
}
