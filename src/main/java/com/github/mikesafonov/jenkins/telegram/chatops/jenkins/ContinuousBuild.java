package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.PendingInput;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mike Safonov
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class ContinuousBuild {
    private final Long chatId;
    private final String jobName;
    private final Build build;
    private final Map<String, PendingInput> requestedInputs = new HashMap<>();

    public BuildWithDetails details() throws IOException {
        return build.details();
    }

    public Integer number() {
        return build.getNumber();
    }

    public void addRequestedInput(PendingInput pendingInput) {
        requestedInputs.put(pendingInput.getId(), pendingInput);
    }

    public List<PendingInput> getNotRequestedInputs(List<PendingInput> pendingInputs) {
        return pendingInputs.stream()
                .filter(pendingInput -> requestedInputs.get(pendingInput.getId()) == null)
                .collect(Collectors.toList());
    }
}
