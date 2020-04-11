package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.RunJobFromCommandAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import com.github.mikesafonov.jenkins.telegram.chatops.utils.HexUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class RunJobFromCommandActionTest {
    private JobRunQueueService jobRunQueueService;
    private RunJobFromCommandAction action;
    private Long chatId;
    private String jobNameHex = HexUtils.toHex("test");
    private CommandContext context;
    private TelegramBotSender telegramBotSender;

    @BeforeEach
    void setUp() {
        jobRunQueueService = mock(JobRunQueueService.class);
        action = new RunJobFromCommandAction(jobRunQueueService);

        context = mock(CommandContext.class);
        telegramBotSender = mock(TelegramBotSender.class);
        chatId = 1L;

        when(context.getCommandText()).thenReturn("/run_" + jobNameHex);
        when(context.getSender()).thenReturn(telegramBotSender);
        when(context.getChatId()).thenReturn(chatId);
    }

    @Test
    void shouldRunJob() {
        JobToRun job = new JobToRun("test", chatId);

        action.accept(context);

        verify(jobRunQueueService).registerJob(job);
    }

    @Test
    void shouldSendMessage() {
        action.accept(context);

        verify(telegramBotSender).sendMarkdownTextMessage(chatId, "Job *test* registered to run");
    }
}
