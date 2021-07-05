package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.PendingInput;
import com.offbytwo.jenkins.model.Build;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class ContinuousBuildTest {
    private Build build;

    private ContinuousBuild continuousBuild;


    @BeforeEach
    void setUp() {
        build = mock(Build.class);
        continuousBuild = new ContinuousBuild(10L, "name", build);
    }

    @Nested
    class GetNumber {
        @Test
        void shouldReturnNumber() {
            when(build.getNumber()).thenReturn(10);

            assertEquals(10, continuousBuild.number());
        }
    }

    @Nested
    class GetNotRequestedInputs {
        @BeforeEach
        void setUp() {
            var input = new PendingInput();
            input.setId("1");
            continuousBuild.addRequestedInput(input);
        }

        @Test
        void shouldReturnAll() {
            var second = new PendingInput();
            second.setId("2");
            var pendingInputs = List.of(second);

            assertEquals(pendingInputs, continuousBuild.getNotRequestedInputs(pendingInputs));
        }

        @Test
        void shouldReturnWithout() {
            var input = new PendingInput();
            input.setId("1");
            var second = new PendingInput();
            second.setId("2");
            var pendingInputs = List.of(input, second);

            assertEquals(List.of(second), continuousBuild.getNotRequestedInputs(pendingInputs));
        }
    }

}
