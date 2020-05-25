package com.github.mikesafonov.jenkins.telegram.chatops.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mike Safonov
 */
class DurationFormatterTest {

    @Nested
    class WhenAll {

        @Test
        void shouldReturnExpectedString() {
            Duration duration = Duration.ofHours(2)
                .plusMinutes(10)
                .plusSeconds(5)
                .plusMillis(2);
            DurationFormatter formatter = new DurationFormatter(duration);

            assertThat(formatter.format()).isEqualTo("2 h 10 m 5 s 2 ms ");
        }
    }

    @Nested
    class WhenNoHours {

        @Test
        void shouldReturnExpectedString() {
            Duration duration = Duration.ofMinutes(10)
                .plusSeconds(5)
                .plusMillis(2);
            DurationFormatter formatter = new DurationFormatter(duration);

            assertThat(formatter.format()).isEqualTo("10 m 5 s 2 ms ");
        }
    }

    @Nested
    class WheNoMinutes {

        @Test
        void shouldReturnExpectedString() {
            Duration duration = Duration.ofSeconds(5)
                .plusMillis(2);
            DurationFormatter formatter = new DurationFormatter(duration);

            assertThat(formatter.format()).isEqualTo("5 s 2 ms ");
        }
    }

    @Nested
    class WheNoMillis {

        @Test
        void shouldReturnExpectedString() {
            Duration duration = Duration.ofMillis(2);
            DurationFormatter formatter = new DurationFormatter(duration);

            assertThat(formatter.format()).isEqualTo("2 ms ");
        }
    }

}
