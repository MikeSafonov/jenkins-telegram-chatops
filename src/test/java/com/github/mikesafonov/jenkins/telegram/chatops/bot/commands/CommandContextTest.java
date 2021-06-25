package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
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

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties);

        assertEquals("/help", commandContext.getCommandText());
    }

    @Test
    void shouldReturnChatId() {
        Long chatId = 1L;

        when(telegramBotProperties.getName()).thenReturn("bot");
        when(message.getText()).thenReturn("/help@bot");
        when(message.getChatId()).thenReturn(chatId);

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties);

        assertEquals(chatId, commandContext.getChatId());
    }

    @Test
    void shouldParseParams() {
        when(telegramBotProperties.getName()).thenReturn("bot");
        when(message.getText()).thenReturn("/help one two");

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties);

        assertThat(commandContext.getArgs()).containsExactly("one", "two");
    }

    @Test
    void shouldContainsEmptyParams(){
        when(telegramBotProperties.getName()).thenReturn("bot");
        when(message.getText()).thenReturn("/help");

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties);

        assertThat(commandContext.getArgs()).isEmpty();
    }

    @Test
    void shouldContainUpdate() {
        when(telegramBotProperties.getName()).thenReturn("bot");
        when(message.getText()).thenReturn("/help one two");

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties);

        assertEquals(update, commandContext.getUpdate());
    }

    @Test
    void shouldContainSender() {
        when(telegramBotProperties.getName()).thenReturn("bot");
        when(message.getText()).thenReturn("/help one two");

        CommandContext commandContext = new CommandContext(update, sender, telegramBotProperties);

        assertEquals(sender, commandContext.getSender());
    }
}
