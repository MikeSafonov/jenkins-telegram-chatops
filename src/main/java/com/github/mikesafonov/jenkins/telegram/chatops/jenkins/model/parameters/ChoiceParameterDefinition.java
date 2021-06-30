package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Mike Safonov
 */
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceParameterDefinition extends ParameterDefinition {
    static final String CLASS = "hudson.model.ChoiceParameterDefinition";

    @Getter
    private List<String> choices;
    @Getter
    private StringParameterValue defaultParameterValue;

    @Override
    public String toString() {
        return getName() + "(" + getDescription() + ")" + "(" + String.join(",", choices) + ")";
    }
}
