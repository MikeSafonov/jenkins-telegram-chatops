package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * @author Mike Safonov
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class ContinuousBuild {
    private final String jobName;
    private final Build build;

    public BuildWithDetails details() throws IOException {
        return build.details();
    }

    public Integer number() {
        return build.getNumber();
    }
}
