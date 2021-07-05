package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.inputs;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Mike Safonov
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChoiceInputParameterDefinition extends InputParameterDefinition {
    public static final String TYPE = "ChoiceParameterDefinition";

    private String defaultVal;
    private List<String> choices;
}
