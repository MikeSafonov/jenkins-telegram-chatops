package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Safonov
 */
@Service
public class ArgsParserService {

    private static final String SPLITERATOR = "=";

    public Map<String, String> parse(String[] args) {
        Map<String, String> parameters = new HashMap<>();
        for (String arg : args) {
            String[] split = arg.split(SPLITERATOR);
            if (split.length == 2) {
                parameters.put(split[0], split[1]);
            }
        }
        return parameters;
    }
}
