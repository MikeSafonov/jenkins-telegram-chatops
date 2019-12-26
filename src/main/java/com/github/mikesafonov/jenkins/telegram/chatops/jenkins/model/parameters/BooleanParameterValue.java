package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

/**
 * @author Mike Safonov
 */
public class BooleanParameterValue extends ParameterValue<Boolean> {
    public BooleanParameterValue() {
    }

    public BooleanParameterValue(String _class, String name, Boolean value) {
        super(_class, name, value);
    }
}
