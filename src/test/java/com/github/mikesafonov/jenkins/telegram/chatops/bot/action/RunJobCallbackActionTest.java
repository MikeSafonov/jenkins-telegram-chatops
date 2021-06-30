package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserStateService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.RunJobCallbackAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsServerWrapper;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.JobWithDetailsWithProperties;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.ParametersDefinitionProperty;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters.ChoiceParameterDefinition;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters.ParameterDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class RunJobCallbackActionTest {
    private JobRunQueueService jobRunQueueService;
    private UserStateService userStateService;
    private JenkinsServerWrapper jenkinsServerWrapper;
    private JobWithDetailsWithProperties job;
    private CommandContext context;
    private String jobName = "job";
    private Long chatId = 10L;
    private TelegramBotSender sender;
    private RunJobCallbackAction action;

    @BeforeEach
    void setUp() {
        jobRunQueueService = mock(JobRunQueueService.class);
        userStateService = mock(UserStateService.class);
        jenkinsServerWrapper = mock(JenkinsServerWrapper.class);
        job = mock(JobWithDetailsWithProperties.class);
        sender = mock(TelegramBotSender.class);
        context = mock(CommandContext.class);
        var update = mock(Update.class);
        var callback = mock(CallbackQuery.class);
        when(context.getSender()).thenReturn(sender);
        when(context.getChatId()).thenReturn(chatId);
        when(context.getUpdate()).thenReturn(update);
        when(update.getCallbackQuery()).thenReturn(callback);
        when(callback.getData()).thenReturn("/r " + jobName);

        when(jenkinsServerWrapper.getJobByNameWithProperties(jobName)).thenReturn(job);

        action = new RunJobCallbackAction(
                jobRunQueueService, userStateService, jenkinsServerWrapper
        );
    }

    @Nested
    class WhenJobNotRequireParameters {

        @BeforeEach
        void setUp() {
            when(job.getParametersDefinitionProperty()).thenReturn(Optional.empty());
        }

        @Test
        void shouldSendMessage() {
            action.accept(context);

            verify(sender).sendMarkdownTextMessage(chatId, "Job *" + jobName + "* registered to run");
        }

        @Test
        void shouldRegisterJob() {
            action.accept(context);

            verify(jobRunQueueService).registerJob(new JobToRun(jobName, chatId));
        }

        @Test
        void shouldChangeState() {
            action.accept(context);

            verify(userStateService).update(chatId, UserState.WAIT_COMMAND);
        }
    }

    @Nested
    class WhenJobRequireParameters {
        private List<ParameterDefinition> definitionList;

        @BeforeEach
        void setUp() {
            var parametersDefinition = mock(ParametersDefinitionProperty.class);
            when(job.getParametersDefinitionProperty()).thenReturn(Optional.of(parametersDefinition));
            var param = new ChoiceParameterDefinition(List.of("one"), null);
            param.setDescription("description");
            param.setName("name");

            definitionList = List.of(
                    param
            );
            when(parametersDefinition.getParameterDefinitions()).thenReturn(definitionList);
        }

        @Test
        void shouldSendMessage() {
            action.accept(context);

            verify(sender).sendMarkdownTextMessage(chatId, "Job *job* requires input parameters:\n" +
                    "name(description)(one)");
        }

        @Test
        void shouldChangeState() {
            action.accept(context);

            verify(userStateService).update(chatId, UserState.WAIT_PARAMETERS, jobName);
        }

    }
}
