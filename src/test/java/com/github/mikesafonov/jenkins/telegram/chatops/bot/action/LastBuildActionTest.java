package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.LastBuildAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class LastBuildActionTest {

    private JenkinsService jenkinsService;
    private LastBuildAction action;
    private CommandContext context;
    private TelegramBotSender telegramBotSender;
    private String jobName;
    private Long chatId;

    @BeforeEach
    void setUp() {
        jenkinsService = mock(JenkinsService.class);
        action = new LastBuildAction(jenkinsService);

        telegramBotSender = mock(TelegramBotSender.class);
        context = mock(CommandContext.class);
        jobName = "name";
        chatId = 10L;

        when(context.getArgs()).thenReturn(new String[]{jobName});
        when(context.getSender()).thenReturn(telegramBotSender);
        when(context.getChatId()).thenReturn(chatId);
    }

    @Nested
    class WhenNoLastBuild {

        @BeforeEach
        void setUp() {
            when(jenkinsService.getLastBuild(jobName)).thenReturn(BuildWithDetails.BUILD_HAS_NEVER_RUN);
        }

        @Test
        void shouldSendExpectedMessage() {
            action.accept(context);

            verify(telegramBotSender).sendMarkdownTextMessage(chatId, "No last build of *" + jobName + "*");
        }

    }

    @Nested
    class WhenLastBuildExist {

        private int number;
        private BuildResult result;
        private long duration;
        private String url;

        @BeforeEach
        void setUp() {
            BuildWithDetails build = mock(BuildWithDetails.class);

            duration = 1000;
            result = BuildResult.SUCCESS;
            number = 12;
            url = "url";

            when(jenkinsService.getLastBuild(jobName)).thenReturn(build);
            when(build.getResult()).thenReturn(result);
            when(build.getNumber()).thenReturn(number);
            when(build.getDuration()).thenReturn(duration);
            when(build.getUrl()).thenReturn(url);
        }

        @Test
        void shouldSendExpectedMessage() {

            String message = "Last build of *" +
                jobName +
                "* #" +
                number +
                "\n*Result*: " +
                result +
                "\n*Duration*: 1 s " +
                "\n[Launch on Jenkins](" +
                url +
                ")";

            action.accept(context);

            verify(telegramBotSender)
                .sendMarkdownTextMessage(chatId, message);
        }
    }


}
