package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.BotEmoji;
import lombok.EqualsAndHashCode;

/**
 * @author Mike Safonov
 */
@EqualsAndHashCode
public class IsFolderCommandMatcher implements CommandMatcher {
    @Override
    public boolean match(CommandContext context) {
        return context.getCommandText().startsWith(BotEmoji.FOLDER_UNICODE);
    }
}
