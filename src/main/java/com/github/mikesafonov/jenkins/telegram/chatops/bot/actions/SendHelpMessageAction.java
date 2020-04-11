package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class SendHelpMessageAction implements Consumer<CommandContext> {
    private final MessageBuilderService messageBuilderService;

    @Override
    public void accept(CommandContext context) {
        context.getSender().sendMarkdownTextMessage(context.getChatId(),
                messageBuilderService.getHelpMessage());
    }
}
