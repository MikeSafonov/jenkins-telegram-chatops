package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

/**
 * @author Mike Safonov
 */
public class FileParameterDefinition extends ParameterDefinition {
    static final String CLASS = "hudson.model.FileParameterDefinition";

    @Override
    public ParameterValue getDefaultParameterValue() {
        return null;
    }
}
