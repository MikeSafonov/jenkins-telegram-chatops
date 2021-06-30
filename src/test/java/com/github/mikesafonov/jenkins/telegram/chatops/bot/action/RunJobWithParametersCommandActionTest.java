package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.ArgsParserService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserStateService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.RunJobWithParametersCommandAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class RunJobWithParametersCommandActionTest {
    private JobRunQueueService jobRunQueueService;
    private UserStateService userStateService;
    private ArgsParserService argsParserService;
    private TelegramBotSender sender;
    private CommandContext context;
    private Long chatId = 10L;
    private String jobName = "hello";

    private RunJobWithParametersCommandAction action;

    @BeforeEach
    void setUp() {
        jobRunQueueService = mock(JobRunQueueService.class);
        userStateService = mock(UserStateService.class);
        argsParserService = mock(ArgsParserService.class);
        sender = mock(TelegramBotSender.class);
        context = mock(CommandContext.class);
        when(context.getChatId()).thenReturn(chatId);
        when(context.getSender()).thenReturn(sender);
        when(userStateService.getJobName(chatId)).thenReturn(jobName);

        action = new RunJobWithParametersCommandAction(jobRunQueueService, userStateService, argsParserService);
    }

    @Nested
    class WhenNoArgs {
        @BeforeEach
        void setUp() {
            when(context.getArgs()).thenReturn(new String[]{});
        }

        @Test
        void shouldSendMessage() {
            action.accept(context);

            verify(sender).sendMarkdownTextMessage(chatId, "Job *" + jobName + "* registered to run");
        }

        @Test
        void shouldRegisterJob() {
            action.accept(context);

            verify(jobRunQueueService).registerJob(new JobToRun(jobName, chatId, Map.of()));
        }

        @Test
        void shouldChangeState() {
            action.accept(context);

            verify(userStateService).update(chatId, UserState.WAIT_COMMAND);
        }
    }


    @Nested
    class WhenArgs {
        @BeforeEach
        void setUp() {
            var params = new String[]{"one", "two"};
            when(context.getArgs()).thenReturn(params);
            when(argsParserService.parse(params)).thenReturn(Map.of("one", "two"));
        }

        @Test
        void shouldSendMessage() {
            action.accept(context);

            verify(sender).sendMarkdownTextMessage(chatId, "Job *" + jobName + "* registered to run");
        }

        @Test
        void shouldRegisterJob() {
            action.accept(context);

            verify(jobRunQueueService).registerJob(new JobToRun(jobName, chatId, Map.of("one", "two")));
        }

        @Test
        void shouldChangeState() {
            action.accept(context);

            verify(userStateService).update(chatId, UserState.WAIT_COMMAND);
        }
    }
}
