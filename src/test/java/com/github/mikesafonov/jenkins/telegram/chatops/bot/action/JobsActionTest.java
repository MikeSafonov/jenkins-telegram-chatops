package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.BotEmoji;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserStateService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.JobsAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class JobsActionTest {

    private JenkinsService jenkinsService;
    private UserStateService userStateService;
    private JobsAction action;
    private CommandContext context;
    private TelegramBotSender sender;
    private Long chatId = 10L;

    @BeforeEach
    void setUp() {
        context = mock(CommandContext.class);
        sender = mock(TelegramBotSender.class);
        jenkinsService = mock(JenkinsService.class);
        userStateService = mock(UserStateService.class);

        when(context.getChatId()).thenReturn(chatId);
        when(context.getSender()).thenReturn(sender);

        action = new JobsAction(jenkinsService, userStateService);
    }

    @Test
    void shouldSendMessage() {
        when(context.getCommandText()).thenReturn("/jobs");
        var jobOne = mock(JenkinsJob.class);
        when(jobOne.isFolder()).thenReturn(true);
        when(jobOne.getFullName()).thenReturn("folder");
        var jobTwo = mock(JenkinsJob.class);
        when(jobTwo.isFolder()).thenReturn(false);
        when(jobTwo.getFullName()).thenReturn("runnable");
        when(jenkinsService.getJobs()).thenReturn(List.of(
                jobOne, jobTwo
        ));

        action.accept(context);

        var captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(sender).sendTelegramMessage(captor.capture());
        var message = captor.getValue();

        assertThat(message).satisfies(sendMessage -> {
            assertThat(sendMessage.getChatId()).isEqualTo("10");
            assertThat(sendMessage.getText()).isEqualTo("Jobs: ");
            assertThat(sendMessage.getReplyMarkup()).satisfies(replyKeyboard -> {
                assertThat(replyKeyboard).isInstanceOf(ReplyKeyboardMarkup.class);
                var rowOne = new KeyboardRow();
                rowOne.add(BotEmoji.FOLDER_UNICODE + " folder");
                var rowTwo = new KeyboardRow();
                rowTwo.add(BotEmoji.RUNNABLE_UNICODE + " runnable");

                assertThat(((ReplyKeyboardMarkup) replyKeyboard).getKeyboard())
                        .containsOnly(
                                rowOne, rowTwo
                        );
            });
        });

    }

    @Test
    void shouldChangeState() {
        when(context.getCommandText()).thenReturn("");

        action.accept(context);

        verify(userStateService).update(chatId, UserState.WAIT_COMMAND);
    }
}
