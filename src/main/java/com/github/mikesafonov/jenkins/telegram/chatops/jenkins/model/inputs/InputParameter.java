package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.inputs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Mike Safonov
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChoiceInputParameter.class, name = ChoiceInputParameterDefinition.TYPE)
})
@EqualsAndHashCode
public abstract class InputParameter<D extends InputParameterDefinition> {
    private String name;
    private D definition;
}
