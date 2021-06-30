package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
@RequiredArgsConstructor
public class StateCommandMatcher implements CommandMatcher {
    private final UserState expected;

    @Override
    public boolean match(CommandContext context) {
        return context.getState() == expected;
    }
}
