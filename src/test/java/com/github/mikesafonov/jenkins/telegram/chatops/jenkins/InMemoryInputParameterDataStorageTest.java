package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.InputParameterWithValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Mike Safonov
 */
class InMemoryInputParameterDataStorageTest {
    private InMemoryInputParameterDataStorage dataStorage;

    @BeforeEach
    void setUp(){
        dataStorage = new InMemoryInputParameterDataStorage();
    }

    @Test
    void shouldReturnExpected() {
        var parameter = new InputParameterWithValue(null, null, "name", "value");
        var key = dataStorage.put(parameter);

        var actual = dataStorage.pull(key);

        assertEquals(parameter, actual.get());
    }
}
