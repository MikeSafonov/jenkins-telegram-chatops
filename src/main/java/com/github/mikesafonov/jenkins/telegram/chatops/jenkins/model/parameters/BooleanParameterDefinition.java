package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

/**
 * @author Mike Safonov
 */
public class BooleanParameterDefinition extends ParameterDefinition {
    static final String CLASS = "hudson.model.BooleanParameterDefinition";

    private BooleanParameterValue defaultParameterValue;

    public BooleanParameterDefinition(){}

    public BooleanParameterDefinition(BooleanParameterValue defaultParameterValue) {
        this.defaultParameterValue = defaultParameterValue;
    }

    @Override
    public BooleanParameterValue getDefaultParameterValue() {
        return defaultParameterValue;
    }
}
