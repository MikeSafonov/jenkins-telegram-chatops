package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.SendHelpMessageAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class SendHelpMessageActionTest {
    private MessageBuilderService messageBuilderService;
    private SendHelpMessageAction action;
    private TelegramBotSender telegramBotSender;

    @BeforeEach
    void setUp() {
        telegramBotSender = mock(TelegramBotSender.class);
        messageBuilderService = mock(MessageBuilderService.class);
        action = new SendHelpMessageAction(messageBuilderService);
    }

    @Test
    void shouldSendHelpMessage() {
        Long chatId = 1L;
        CommandContext context = mock(CommandContext.class);
        String helpMessage = "message";

        when(context.getChatId()).thenReturn(chatId);
        when(context.getSender()).thenReturn(telegramBotSender);
        when(messageBuilderService.getHelpMessage()).thenReturn(helpMessage);

        action.accept(context);

        verify(telegramBotSender).sendMarkdownTextMessage(chatId, helpMessage);
    }

}
