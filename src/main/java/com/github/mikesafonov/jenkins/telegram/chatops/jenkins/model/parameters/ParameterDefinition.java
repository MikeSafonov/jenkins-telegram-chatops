package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Mike Safonov
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "_class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanParameterDefinition.class, name = BooleanParameterDefinition.CLASS),
        @JsonSubTypes.Type(value = StringParameterDefinition.class, name = StringParameterDefinition.CLASS),
        @JsonSubTypes.Type(value = TextParameterDefinition.class, name = TextParameterDefinition.CLASS),
        @JsonSubTypes.Type(value = ChoiceParameterDefinition.class, name = ChoiceParameterDefinition.CLASS),
        @JsonSubTypes.Type(value = FileParameterDefinition.class, name = FileParameterDefinition.CLASS),
        @JsonSubTypes.Type(value = PasswordParameterDefinition.class, name = PasswordParameterDefinition.CLASS),
        @JsonSubTypes.Type(value = RunParameterDefinition.class, name = RunParameterDefinition.CLASS),
        @JsonSubTypes.Type(value = GitParameterDefinition.class, name = GitParameterDefinition.CLASS)
})
public abstract class ParameterDefinition {
    private String name;
    private String description;
    private String type;

    public abstract ParameterValue getDefaultParameterValue();
}
