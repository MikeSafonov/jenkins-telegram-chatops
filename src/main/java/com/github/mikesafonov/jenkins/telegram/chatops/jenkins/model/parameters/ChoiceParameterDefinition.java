package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

import java.util.List;

/**
 * @author Mike Safonov
 */
public class ChoiceParameterDefinition extends ParameterDefinition {
    static final String CLASS = "hudson.model.ChoiceParameterDefinition";

    private List<String> choices;
    private StringParameterValue defaultParameterValue;

    public List<String> getChoices() {
        return choices;
    }

    @Override
    public StringParameterValue getDefaultParameterValue() {
        return null;
    }

    @Override
    public String toString() {
        return getName() + "(" + getDescription() + ")" + "(" + String.join(",", choices) + ")";
    }
}
