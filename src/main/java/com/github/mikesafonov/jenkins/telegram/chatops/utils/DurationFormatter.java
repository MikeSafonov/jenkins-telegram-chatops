package com.github.mikesafonov.jenkins.telegram.chatops.utils;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class DurationFormatter {

    private final Duration duration;

    public String format() {
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();
        int millis = duration.toMillisPart();

        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours).append(" h ");
        }
        if (minutes > 0) {
            builder.append(minutes).append(" m ");
        }
        if (seconds > 0) {
            builder.append(seconds).append(" s ");
        }
        if (millis > 0) {
            builder.append(millis).append(" ms ");
        }

        return builder.toString();

    }

}
