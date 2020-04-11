package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.RunJobFromArgsAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class RunJobFromArgsActionTest {
    private JobRunQueueService jobRunQueueService;
    private RunJobFromArgsAction action;
    private Long chatId;
    private CommandContext context;
    private TelegramBotSender telegramBotSender;

    @BeforeEach
    void setUp() {
        jobRunQueueService = mock(JobRunQueueService.class);
        action = new RunJobFromArgsAction(jobRunQueueService);

        context = mock(CommandContext.class);
        telegramBotSender = mock(TelegramBotSender.class);
        chatId = 1L;

        when(context.getCommandText()).thenReturn("/run test");
        when(context.getArgs()).thenReturn(new String[]{"test"});
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
