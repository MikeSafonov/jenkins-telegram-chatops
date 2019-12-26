package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

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
}
