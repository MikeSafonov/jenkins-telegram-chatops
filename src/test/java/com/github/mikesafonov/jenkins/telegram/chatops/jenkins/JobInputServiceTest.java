package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.InputParameterWithValue;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.PendingInput;
import com.offbytwo.jenkins.client.JenkinsHttpConnection;
import com.offbytwo.jenkins.model.Build;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
public class JobInputServiceTest {
    private ObjectMapper objectMapper;
    private JobInputService jobInputService;
    private Build build;
    private JenkinsHttpConnection client;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        jobInputService = new JobInputService(objectMapper);
        build = mock(Build.class);
        client = mock(JenkinsHttpConnection.class);
        when(build.getUrl()).thenReturn("jenkins");
        when(build.getClient()).thenReturn(client);
    }

    @Nested
    class GetPendingInputs {


        @Test
        @SneakyThrows
        void shouldReturnEmptyWhenNoPendingInputs() {
            var json = "json";
            when(client.get("jenkins/wfapi/pendingInputActions"))
                    .thenReturn(json);
            when(objectMapper.readValue(json, PendingInput[].class))
                    .thenReturn(new PendingInput[]{});

            assertThat(jobInputService.getPendingInputs(build)).isEmpty();
        }

        @Test
        @SneakyThrows
        void shouldReturnExpectedPendingInputs() {
            var json = "json";
            when(client.get("jenkins/wfapi/pendingInputActions"))
                    .thenReturn(json);
            when(objectMapper.readValue(json, PendingInput[].class))
                    .thenReturn(new PendingInput[]{new PendingInput()});

            var pendingInputs = jobInputService.getPendingInputs(build);
            assertThat(pendingInputs).hasSize(1);
        }

    }

    @Nested
    class PostInput {
        private PendingInput pendingInput;

        @BeforeEach
        void setUp() {
            pendingInput = mock(PendingInput.class);
            when(pendingInput.getId()).thenReturn("id");
        }

        @Test
        @SneakyThrows
        void shouldCallClient() {
            when(pendingInput.getProceedText()).thenReturn("pr");
            when(objectMapper.writeValueAsString(any(Map.class)))
                    .thenReturn("{\"name\":\"name\",\"value\":\"value\"}");

            jobInputService.postInput(new InputParameterWithValue(build, pendingInput, "name", "value"));

            verify(client).post("jenkinsinput/id/submit?proceed=pr&json=%7B%22name%22%3A%22name%22%2C%22value%22%3A%22value%22%7D", true);
        }

    }
}
