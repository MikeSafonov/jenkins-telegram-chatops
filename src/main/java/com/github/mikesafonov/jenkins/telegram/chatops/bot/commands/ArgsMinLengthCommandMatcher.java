package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
@EqualsAndHashCode
public class ArgsMinLengthCommandMatcher extends BaseCommandMatcher {

    private final int minExpectedLength;

    @Override
    public boolean match(CommandContext context) {
        String[] args = context.getArgs();
        return args != null && minExpectedLength <= args.length;
    }
}
