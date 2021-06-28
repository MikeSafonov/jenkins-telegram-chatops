package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

/**
 * @author Mike Safonov
 */
public class StringParameterDefinition extends ParameterDefinition {
    static final String CLASS = "hudson.model.StringParameterDefinition";

    private StringParameterValue defaultParameterValue;

    @Override
    public StringParameterValue getDefaultParameterValue() {
        return defaultParameterValue;
    }

    @Override
    public String toString() {
        return getName() + "(" + getDescription() + ")" + "(string)";
    }
}
