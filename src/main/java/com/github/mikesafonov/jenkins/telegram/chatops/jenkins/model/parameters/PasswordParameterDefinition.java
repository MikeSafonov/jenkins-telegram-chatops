package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

/**
 * @author Mike Safonov
 */
public class PasswordParameterDefinition extends ParameterDefinition {
    static final String CLASS = "hudson.model.PasswordParameterDefinition";

    private PasswordParameterValue defaultParameterValue;

    @Override
    public PasswordParameterValue getDefaultParameterValue() {
        return defaultParameterValue;
    }

    @Override
    public String toString() {
        return getName() + "(" + getDescription() + ")" + "(password)";
    }
}
