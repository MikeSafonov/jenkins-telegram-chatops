package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserStateService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.InputParameterAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.InMemoryInputParameterDataStorage;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobInputService;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.InputParameterWithValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class InputParameterActionTest {
    private InMemoryInputParameterDataStorage dataStorage;
    private JobInputService jobInputService;
    private UserStateService userStateService;
    private CommandContext context;
    private TelegramBotSender sender;
    private String uuid;
    private InputParameterAction action;

    @BeforeEach
    void setUp() {
        context = mock(CommandContext.class);
        sender = mock(TelegramBotSender.class);
        jobInputService = mock(JobInputService.class);
        dataStorage = mock(InMemoryInputParameterDataStorage.class);
        userStateService = mock(UserStateService.class);
        Update update = mock(Update.class);
        uuid = UUID.randomUUID().toString();
        var callback = mock(CallbackQuery.class);

        Long chatId = 10L;
        when(context.getChatId()).thenReturn(chatId);
        when(context.getSender()).thenReturn(sender);
        when(context.getUpdate()).thenReturn(update);
        when(update.getCallbackQuery()).thenReturn(callback);
        when(callback.getData()).thenReturn(uuid);

        action = new InputParameterAction(dataStorage, jobInputService, userStateService);
    }

    @Nested
    class WhenNoData {
        @BeforeEach
        void setUp() {
            when(dataStorage.pull(uuid)).thenReturn(Optional.empty());
        }

        @Test
        void shouldSendMessage() {
            action.accept(context);

            var captor = ArgumentCaptor.forClass(SendMessage.class);
            verify(sender).sendTelegramMessage(captor.capture());
            var message = captor.getValue();
            assertThat(message).satisfies(sendMessage -> {
                assertThat(sendMessage.getChatId()).isEqualTo("10");
                assertThat(sendMessage.getText()).isEqualTo("No data was found");
            });
        }

        @Test
        void shouldUpdateState() {
            action.accept(context);

            verify(userStateService).update(10L, UserState.WAIT_COMMAND);
        }
    }

    @Nested
    class WhenDataExist {
        private InputParameterWithValue parameter;

        @BeforeEach
        void setUp() {
            parameter = new InputParameterWithValue(null, null, "name", "value");
            when(dataStorage.pull(uuid)).thenReturn(Optional.of(parameter));
        }

        @Test
        void shouldSendMessage() {
            action.accept(context);

            verify(jobInputService).postInput(parameter);
        }

        @Test
        void shouldUpdateState() {
            action.accept(context);

            verify(userStateService).update(10L, UserState.WAIT_COMMAND);
        }
    }
}
