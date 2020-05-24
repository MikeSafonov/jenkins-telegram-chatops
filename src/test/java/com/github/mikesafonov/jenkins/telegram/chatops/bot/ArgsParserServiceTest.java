package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mike Safonov
 */
class ArgsParserServiceTest {

    private ArgsParserService argsParserService;

    @BeforeEach
    void setUp() {
        argsParserService = new ArgsParserService();
    }

    public static Stream<Arguments> parsingProvider() {
        return Stream.of(
            Arguments.of(new String[]{"asdasdasd", "rrewerwer"}, Collections.emptyMap()),
            Arguments.of(new String[]{"one=two", "asdasd"}, Map.of("one", "two")),
            Arguments.of(new String[]{"asdadas", "one=two"}, Map.of("one", "two")),
            Arguments.of(new String[]{"one=two", "three=four"}, Map.of("one", "two", "three", "four"))
        );

    }

    @ParameterizedTest
    @MethodSource("parsingProvider")
    void shouldReturnExpectedMap(String[] input, Map<String, String> expected) {
        assertThat(argsParserService.parse(input)).isEqualTo(expected);
    }
}
