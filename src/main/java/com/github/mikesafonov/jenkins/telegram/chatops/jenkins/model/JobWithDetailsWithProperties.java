package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model;

import com.offbytwo.jenkins.model.JobWithDetails;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * @author Mike Safonov
 */
public class JobWithDetailsWithProperties extends JobWithDetails {
    private List<JobProperty> property;

    public List<JobProperty> getProperty() {
        return property.stream()
                .filter(Objects::nonNull)
                .collect(toList());
    }
}
