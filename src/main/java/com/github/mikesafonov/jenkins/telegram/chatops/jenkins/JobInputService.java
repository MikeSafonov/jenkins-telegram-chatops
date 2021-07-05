package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.InputParameterWithValue;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.PendingInput;
import com.offbytwo.jenkins.model.Build;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Safonov
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class JobInputService {

    private final ObjectMapper objectMapper;

    public List<PendingInput> getPendingInputs(Build build) {
        try {
            var url = build.getUrl() + "/wfapi/pendingInputActions";
            var json = build.getClient().get(url);
            var pendingInputs = objectMapper.readValue(json, PendingInput[].class);
            if (pendingInputs.length > 0) {
                return List.of(pendingInputs);
            }
            return List.of();
        } catch (IOException e) {
            log.debug(e);
            return List.of();
        }
    }

    @SneakyThrows
    public void postInput(InputParameterWithValue parameter) {
        var build = parameter.getBuild();
        var url = new StringBuilder(build.getUrl())
                .append("input/")
                .append(parameter.getPendingInput().getId())
                .append("/submit?proceed=")
                .append(parameter.getPendingInput().getProceedText())
                .append("&json=")
                .append(encodeParameters(parameter))
                .toString();

        build.getClient().post(url, true);
    }

    @SneakyThrows
    private String encodeParameters(InputParameterWithValue parameter) {
        var paramFields = Map.of(
                "parameter", Map.of(
                        "name", parameter.getName(),
                        "value", parameter.getValue()
                )
        );
        return URLEncoder.encode(objectMapper.writeValueAsString(paramFields),
                StandardCharsets.UTF_8);

    }
}
