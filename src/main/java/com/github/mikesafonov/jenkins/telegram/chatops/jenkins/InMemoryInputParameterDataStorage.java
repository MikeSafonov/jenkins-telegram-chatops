package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.InputParameterWithValue;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Mike Safonov
 */
@Service
public class InMemoryInputParameterDataStorage {
    private final Map<String, InputParameterWithValue> storage = new HashMap<>();

    public String put(InputParameterWithValue parameter) {
        var uuid = UUID.randomUUID().toString();
        storage.put(uuid, parameter);
        return uuid;
    }

    public Optional<InputParameterWithValue> pull(String uuid) {
        var value = storage.get(uuid);
        if (value != null) {
            storage.remove(uuid);
        }
        return Optional.ofNullable(value);
    }
}
