package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

/**
 * @author Mike Safonov
 */
@Value
@RequiredArgsConstructor
public class AndCommandMatcher implements CommandMatcher {

    private final List<CommandMatcher> matchers;

    @Override
    public boolean match(CommandContext context) {
        return matchers.stream()
                .allMatch(matcher -> matcher.match(context));
    }
}
