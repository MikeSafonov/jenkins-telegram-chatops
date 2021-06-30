package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class CommandContextTest {
    private TelegramBotSender sender;
    private TelegramBotProperties telegramBotProperties;
    private Update update;
    private Message message;
    private Long chatId = 1L;

    @BeforeEach
    void setUp() {
        sender = mock(TelegramBotSender.class);
        telegramBotProperties = mock(TelegramBotProperties.class);
        update = mock(Update.class);
        message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
    }

    @Test
    void shouldDeleteBotName() {
        when(telegramBotProperties.getName()).thenReturn("bot");
        when(message.getText()).thenReturn("/help@bot");

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties,
                UserState.WAIT_COMMAND, chatId);

        assertEquals("/help", commandContext.getCommandText());
    }

    @Test
    void shouldReturnChatId() {
        when(telegramBotProperties.getName()).thenReturn("bot");
        when(message.getText()).thenReturn("/help@bot");

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties,
                UserState.WAIT_COMMAND, chatId);

        assertEquals(chatId, commandContext.getChatId());
    }

    @Test
    void shouldParseParams() {
        when(telegramBotProperties.getName()).thenReturn("bot");
        when(message.getText()).thenReturn("one two");

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties,
                UserState.WAIT_COMMAND, chatId);

        assertThat(commandContext.getArgs()).containsExactly("one", "two");
    }

    @Test
    void shouldContainUpdate() {
        when(telegramBotProperties.getName()).thenReturn("bot");
        when(message.getText()).thenReturn("/help one two");

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties,
                UserState.WAIT_COMMAND, chatId);

        assertEquals(update, commandContext.getUpdate());
    }

    @Test
    void shouldContainSender() {
        when(telegramBotProperties.getName()).thenReturn("bot");
        when(message.getText()).thenReturn("/help one two");

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties,
                UserState.WAIT_COMMAND, chatId);

        assertEquals(sender, commandContext.getSender());
    }
}
