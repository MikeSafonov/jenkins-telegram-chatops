package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters.ParameterDefinition;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * @author Mike Safonov
 */
public class ParametersDefinitionProperty extends JobProperty {
    static final String CLASS = "hudson.model.ParametersDefinitionProperty";

    private List<ParameterDefinition> parameterDefinitions;

    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions
                .stream()
                .filter(Objects::nonNull)
                .collect(toList());
    }
}
