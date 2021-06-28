package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Mike Safonov
 */
public class RunParameterDefinition extends ParameterDefinition {
    static final String CLASS = "hudson.model.RunParameterDefinition";

    private String projectName;
    private RunParameterFilter filter;

    public String getProjectName() {
        return projectName;
    }

    public RunParameterFilter getFilter() {
        return filter;
    }

    @Override
    public ParameterValue getDefaultParameterValue() {
        return null;
    }

    public enum RunParameterFilter {
        ALL,
        STABLE,
        SUCCESSFUL,
        COMPLETED
    }

    @Override
    public String toString() {
        return getName() + "(" + getDescription() + ")" + "(" +
                Arrays.stream(RunParameterFilter.values()).map(Enum::toString).collect(Collectors.joining(","))
                + ")";
    }
}
