package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mike Safonov
 */
@Service
public class ArgsParserService {

    private static final String SPLITERATOR = "=";

    public Map<String, String> parse(String[] args) {
        return Arrays.stream(args)
                .map(argument -> argument.split(SPLITERATOR))
                .filter(params -> params.length == 2)
                .collect(Collectors.toMap(params -> params[0], params -> params[1]));
    }
}
