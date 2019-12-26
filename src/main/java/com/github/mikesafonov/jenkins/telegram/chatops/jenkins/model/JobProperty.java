package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


/**
 * @author Mike Safonov
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "_class",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ParametersDefinitionProperty.class, name = ParametersDefinitionProperty.CLASS),
        @JsonSubTypes.Type(value = DisableConcurrentBuildsJobProperty.class, name = DisableConcurrentBuildsJobProperty.CLASS)
})
public abstract class JobProperty  {
    private String _class;

    public String get_class() {
        return _class;
    }
}
