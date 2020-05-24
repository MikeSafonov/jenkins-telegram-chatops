package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
@EqualsAndHashCode
public class ArgsLengthCommandMatcher extends BaseCommandMatcher {

    private final int expectedLength;

    @Override
    public boolean match(CommandContext context) {
        String[] args = context.getArgs();
        return args != null && expectedLength == args.length;
    }
}
