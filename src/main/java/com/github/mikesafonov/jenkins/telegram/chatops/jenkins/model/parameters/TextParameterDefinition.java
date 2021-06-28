package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

/**
 * @author Mike Safonov
 */
public class TextParameterDefinition extends ParameterDefinition {
    static final String CLASS = "hudson.model.TextParameterDefinition";

    private TextParameterValue defaultParameterValue;

    @Override
    public TextParameterValue getDefaultParameterValue() {
        return defaultParameterValue;
    }

    @Override
    public String toString() {
        return getName() + "(" + getDescription() + ")" + "(text)";
    }
}
