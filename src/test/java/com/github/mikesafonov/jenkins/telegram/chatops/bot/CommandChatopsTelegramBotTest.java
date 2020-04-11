package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.Command;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.UpdateCheck;
import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class CommandChatopsTelegramBotTest {
    private DefaultBotOptions defaultBotOptions;
    private TelegramBotProperties telegramBotProperties;
    private BotSecurityService botSecurityService;
    private TelegramBotSender telegramBotSender;

    @BeforeEach
    void setUp() {
        defaultBotOptions = new DefaultBotOptions();
        telegramBotProperties = mock(TelegramBotProperties.class);
        botSecurityService = mock(BotSecurityService.class);
        telegramBotSender = mock(TelegramBotSender.class);
    }

    @Nested
    class GetBotToken {
        private CommandChatopsTelegramBot telegramBot;

        @BeforeEach
        void setUp() {
            telegramBot = new CommandChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                    botSecurityService, telegramBotSender, Collections.emptyList());
        }

        @Test
        void shouldReturnExpectedToken() {
            when(telegramBotProperties.getToken()).thenReturn("token");

            assertEquals("token", telegramBot.getBotToken());
        }
    }

    @Nested
    class GetBotUsername {
        private CommandChatopsTelegramBot telegramBot;

        @BeforeEach
        void setUp() {
            telegramBot = new CommandChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                    botSecurityService, telegramBotSender, Collections.emptyList());
        }

        @Test
        void shouldReturnExpectedToken() {
            when(telegramBotProperties.getName()).thenReturn("token");

            assertEquals("token", telegramBot.getBotUsername());
        }
    }

    @Nested
    class OnUpdateReceived {
        private Long chatId = 1L;

        @Nested
        class WhenNoCommands {
            @Test
            void shouldSendUnknownCommand() {
                CommandChatopsTelegramBot bot = new CommandChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                        botSecurityService, telegramBotSender, Collections.emptyList());
                Update update = mock(Update.class);
                Message message = mock(Message.class);
                when(update.getMessage()).thenReturn(message);
                when(message.getChatId()).thenReturn(chatId);
                when(message.getText()).thenReturn("command");

                bot.onUpdateReceived(update);

                verify(telegramBotSender).sendUnknownCommand(chatId, "command");
            }
        }

        @Nested
        class WhenNoOneMatch {
            @Test
            void shouldSendUnknownCommand() {
                UpdateCheck updateCheck = mock(UpdateCheck.class);

                when(updateCheck.support(any(Update.class))).thenReturn(false);

                Command one = mock(Command.class);
                Command two = mock(Command.class);
                List<Command> commands = List.of(one, two);

                when(one.getCommand()).thenReturn(updateCheck);
                when(two.getCommand()).thenReturn(updateCheck);

                CommandChatopsTelegramBot bot = new CommandChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                        botSecurityService, telegramBotSender, commands);
                Update update = mock(Update.class);
                Message message = mock(Message.class);
                when(update.getMessage()).thenReturn(message);
                when(message.getChatId()).thenReturn(chatId);
                when(message.getText()).thenReturn("command");

                bot.onUpdateReceived(update);

                verify(telegramBotSender).sendUnknownCommand(chatId, "command");
            }
        }

        @Nested
        class WhenArgsNotMatch {
            @Test
            void shouldSendUnknownCommand() {
                UpdateCheck updateCheck = mock(UpdateCheck.class);

                when(updateCheck.support(any(Update.class))).thenReturn(true);

                Command one = mock(Command.class);
                List<Command> commands = List.of(one);

                when(one.getCommand()).thenReturn(updateCheck);
                when(one.getArgsCount()).thenReturn(10);

                CommandChatopsTelegramBot bot = new CommandChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                        botSecurityService, telegramBotSender, commands);
                Update update = mock(Update.class);
                Message message = mock(Message.class);
                when(update.getMessage()).thenReturn(message);
                when(message.getChatId()).thenReturn(chatId);
                when(message.getText()).thenReturn("command");

                bot.onUpdateReceived(update);

                verify(telegramBotSender).sendUnknownCommand(chatId, "command");
            }
        }

        @Nested
        class WhenUnauthorized {
            private Command command;
            private CommandChatopsTelegramBot bot;

            @BeforeEach
            void setUp() {
                UpdateCheck updateCheck = mock(UpdateCheck.class);
                when(updateCheck.support(any(Update.class))).thenReturn(true);

                command = mock(Command.class);
                when(command.getCommand()).thenReturn(updateCheck);
                when(command.getArgsCount()).thenReturn(0);

                bot = new CommandChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                        botSecurityService, telegramBotSender, List.of(command));

                when(botSecurityService.isAllowed(any(Update.class))).thenReturn(false);
            }

            @Test
            void shouldSendUnauthorizedWhenCommandRequiresAuthorization() {
                when(command.isAuthorized()).thenReturn(true);

                Update update = mock(Update.class);
                Message message = mock(Message.class);
                when(update.getMessage()).thenReturn(message);
                when(message.getChatId()).thenReturn(chatId);
                when(message.getText()).thenReturn("command");

                bot.onUpdateReceived(update);

                verify(telegramBotSender).sendUnauthorized(chatId);
            }

            @Test
            void shouldCallActionWhenCommandNotRequiresAuthorization() {
                Consumer<CommandContext> action = mock(Consumer.class);
                when(command.getAction()).thenReturn(action);
                when(command.isAuthorized()).thenReturn(false);

                Update update = mock(Update.class);
                Message message = mock(Message.class);
                when(update.getMessage()).thenReturn(message);
                when(message.getChatId()).thenReturn(chatId);
                when(message.getText()).thenReturn("command");

                CommandContext commandContext = new CommandContext(update, false, telegramBotSender, telegramBotProperties);

                bot.onUpdateReceived(update);

                verify(action).accept(commandContext);
            }
        }

        @Nested
        class WhenAuthorized {
            private Command command;
            private CommandChatopsTelegramBot bot;

            @BeforeEach
            void setUp() {
                UpdateCheck updateCheck = mock(UpdateCheck.class);
                when(updateCheck.support(any(Update.class))).thenReturn(true);

                command = mock(Command.class);
                when(command.getCommand()).thenReturn(updateCheck);
                when(command.getArgsCount()).thenReturn(0);

                bot = new CommandChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                        botSecurityService, telegramBotSender, List.of(command));

                when(botSecurityService.isAllowed(any(Update.class))).thenReturn(true);
            }

            @Test
            void shouldCallActionWhenCommandRequiresAuthorization() {
                Consumer<CommandContext> action = mock(Consumer.class);
                when(command.getAction()).thenReturn(action);
                when(command.isAuthorized()).thenReturn(true);

                Update update = mock(Update.class);
                Message message = mock(Message.class);
                when(update.getMessage()).thenReturn(message);
                when(message.getChatId()).thenReturn(chatId);
                when(message.getText()).thenReturn("command");

                CommandContext commandContext = new CommandContext(update, true, telegramBotSender, telegramBotProperties);

                bot.onUpdateReceived(update);

                verify(action).accept(commandContext);
            }

            @Test
            void shouldCallActionWhenCommandNotRequiresAuthorization() {
                Consumer<CommandContext> action = mock(Consumer.class);
                when(command.getAction()).thenReturn(action);
                when(command.isAuthorized()).thenReturn(false);

                Update update = mock(Update.class);
                Message message = mock(Message.class);
                when(update.getMessage()).thenReturn(message);
                when(message.getChatId()).thenReturn(chatId);
                when(message.getText()).thenReturn("command");

                CommandContext commandContext = new CommandContext(update, true, telegramBotSender, telegramBotProperties);

                bot.onUpdateReceived(update);

                verify(action).accept(commandContext);
            }
        }
    }

}
