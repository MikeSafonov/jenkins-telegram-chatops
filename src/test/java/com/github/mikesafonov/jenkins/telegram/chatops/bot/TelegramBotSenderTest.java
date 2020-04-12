package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
public class TelegramBotSenderTest {
    private TelegramBotProperties telegramBotProperties;
    private TelegramBotSender telegramBotSender;
    private Long chatId = 1L;
    private TelegramBotSender spySender;

    @BeforeEach
    void setUp() {
        DefaultBotOptions botOptions = new DefaultBotOptions();
        telegramBotProperties = mock(TelegramBotProperties.class);
        telegramBotSender = new TelegramBotSender(botOptions, telegramBotProperties);
        spySender = spy(telegramBotSender);
    }

    @Nested
    class GetBotToken {
        @Test
        void shouldReturnExpectedToken() {
            when(telegramBotProperties.getToken()).thenReturn("token");

            assertEquals("token", telegramBotSender.getBotToken());
        }
    }

    @Nested
    class SendUnauthorized {
        @Test
        @SneakyThrows
        void shouldSendMessage() {
            SendMessage sendMessage = new SendMessage(chatId, "⛔️ Unauthorized request");

            doReturn(null).when(spySender).sendMethod(any(SendMessage.class));

            spySender.sendUnauthorized(chatId);

            verify(spySender).sendMethod(sendMessage);
        }

        @Nested
        class WhenUnableToSend {
            @Test
            @SneakyThrows
            void shouldSendUnableToSendRequestWhenTelegramApiRequestException() {
                SendMessage sendMessage = new SendMessage(chatId,
                        "⚠️ Oops! Unable to send telegram message. Please look at logs for more information");

                doThrow(TelegramApiRequestException.class).when(spySender).sendMethod(any(SendMessage.class));

                spySender.sendUnauthorized(chatId);

                verify(spySender).sendMethod(sendMessage);
            }

            @Test
            @SneakyThrows
            void shouldSendUnableToSendRequestWhenTelegramApiException() {
                SendMessage sendMessage = new SendMessage(chatId,
                        "⚠️ Oops! Unable to send telegram message. Please look at logs for more information");

                doThrow(TelegramApiException.class).when(spySender).sendMethod(any(SendMessage.class));

                spySender.sendUnauthorized(chatId);

                verify(spySender).sendMethod(sendMessage);
            }
        }
    }

    @Nested
    class SendUnknownCommand {
        private String command = "/help";

        @Test
        @SneakyThrows
        void shouldSendMessage() {
            SendMessage sendMessage = new SendMessage(chatId, "⚠️ Unknown command: " + command);

            doReturn(null).when(spySender).sendMethod(any(SendMessage.class));

            spySender.sendUnknownCommand(chatId, command);

            verify(spySender).sendMethod(sendMessage);
        }

        @Nested
        class WhenUnableToSend {
            @Test
            @SneakyThrows
            void shouldSendUnableToSendRequestWhenTelegramApiRequestException() {
                SendMessage sendMessage = new SendMessage(chatId,
                        "⚠️ Oops! Unable to send telegram message. Please look at logs for more information");

                doThrow(TelegramApiRequestException.class).when(spySender).sendMethod(any(SendMessage.class));

                spySender.sendUnknownCommand(chatId, command);

                verify(spySender).sendMethod(sendMessage);
            }

            @Test
            @SneakyThrows
            void shouldSendUnableToSendRequestWhenTelegramApiException() {
                SendMessage sendMessage = new SendMessage(chatId,
                        "⚠️ Oops! Unable to send telegram message. Please look at logs for more information");

                doThrow(TelegramApiException.class).when(spySender).sendMethod(any(SendMessage.class));

                spySender.sendUnknownCommand(chatId, command);

                verify(spySender).sendMethod(sendMessage);
            }
        }
    }

    @Nested
    class SendMarkdownTextMessage {
        private String message = "myMessage";

        @Test
        @SneakyThrows
        void shouldSendMessage() {
            SendMessage sendMessage = new SendMessage(chatId, message).enableMarkdown(true);

            doReturn(null).when(spySender).sendMethod(any(SendMessage.class));

            spySender.sendMarkdownTextMessage(chatId, message);

            verify(spySender).sendMethod(sendMessage);
        }

        @Nested
        class WhenUnableToSend {
            @Test
            @SneakyThrows
            void shouldSendUnableToSendRequestWhenTelegramApiRequestException() {
                SendMessage sendMessage = new SendMessage(chatId,
                        "⚠️ Oops! Unable to send telegram message. Please look at logs for more information");

                doThrow(TelegramApiRequestException.class).when(spySender).sendMethod(any(SendMessage.class));

                spySender.sendMarkdownTextMessage(chatId, message);

                verify(spySender).sendMethod(sendMessage);
            }

            @Test
            @SneakyThrows
            void shouldSendUnableToSendRequestWhenTelegramApiException() {
                SendMessage sendMessage = new SendMessage(chatId,
                        "⚠️ Oops! Unable to send telegram message. Please look at logs for more information");

                doThrow(TelegramApiException.class).when(spySender).sendMethod(any(SendMessage.class));

                spySender.sendMarkdownTextMessage(chatId, message);

                verify(spySender).sendMethod(sendMessage);
            }
        }
    }
}
