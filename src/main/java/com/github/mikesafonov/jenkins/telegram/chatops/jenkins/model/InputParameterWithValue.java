package com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model;

import com.offbytwo.jenkins.model.Build;
import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
public class InputParameterWithValue {
    private final Build build;
    private final PendingInput pendingInput;
    private final String name;
    private final String value;
}
