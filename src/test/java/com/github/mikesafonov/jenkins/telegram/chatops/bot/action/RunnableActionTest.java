package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.BotEmoji;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.RunnableAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class RunnableActionTest {
    private RunnableAction action;
    private CommandContext context;
    private TelegramBotSender sender;
    private String commandText = BotEmoji.RUNNABLE_UNICODE + " hello";

    private Long chatId = 10L;

    @BeforeEach
    void setUp() {
        context = mock(CommandContext.class);
        sender = mock(TelegramBotSender.class);

        when(context.getChatId()).thenReturn(chatId);
        when(context.getCommandText()).thenReturn(commandText);
        when(context.getSender()).thenReturn(sender);

        action = new RunnableAction();
    }

    @Test
    void shouldSendMessage() {
        action.accept(context);

        var expected = SendMessage.builder()
                .chatId("10")
                .text("Please select action: ")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(
                                List.of(InlineKeyboardButton.builder()
                                        .text("Run")
                                        .callbackData("/r hello")
                                        .build()),
                                List.of(InlineKeyboardButton.builder()
                                        .text("Get last build")
                                        .callbackData("/l hello")
                                        .build())
                        ))
                        .build())
                .build();

        verify(sender).sendTelegramMessage(
                expected
        );
    }


}
