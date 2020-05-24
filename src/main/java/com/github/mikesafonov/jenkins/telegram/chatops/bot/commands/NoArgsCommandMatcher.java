package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
@EqualsAndHashCode
public class NoArgsCommandMatcher extends BaseCommandMatcher {

    @Override
    public boolean match(CommandContext context) {
        String[] args = context.getArgs();
        return args == null || args.length == 0;
    }
}
