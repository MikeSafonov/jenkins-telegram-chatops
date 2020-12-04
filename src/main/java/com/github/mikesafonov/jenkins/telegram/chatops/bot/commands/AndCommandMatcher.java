package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

/**
 * @author Mike Safonov
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class AndCommandMatcher extends BaseCommandMatcher {

    private final List<CommandMatcher> matchers;

    @Override
    public boolean match(CommandContext context) {
        return matchers.stream()
            .allMatch(matcher -> matcher.match(context));
    }
}
