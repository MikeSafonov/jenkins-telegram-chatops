package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.inputs.InputParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Mike Safonov
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PendingInput {
    private String id;
    private String message;
    private String proceedText;
    private String proceedUrl;
    private String abortUrl;

    private List<InputParameter<?>> inputs;

}
