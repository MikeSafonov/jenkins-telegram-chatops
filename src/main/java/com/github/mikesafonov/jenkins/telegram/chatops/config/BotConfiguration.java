package com.github.mikesafonov.jenkins.telegram.chatops.config;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.ArgsParserService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserStateService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.*;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.Command;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.builder.CommandsBuilder;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsServerWrapper;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Mike Safonov
 */
@Configuration
public class BotConfiguration {

    @Bean
    public List<Command> commands(MessageBuilderService messageBuilderService,
                                  JenkinsService jenkinsService,
                                  JobRunQueueService jobRunQueueService,
                                  UserStateService userStateService,
                                  JenkinsServerWrapper jenkinsServerWrapper,
                                  ArgsParserService argsParserService) {
        return new CommandsBuilder()
                .command("/help")
                .action(new SendHelpMessageAction(messageBuilderService))
                .and()
                .command("/jobs")
                .state(UserState.WAIT_COMMAND)
                .action(new JobsAction(jenkinsService, userStateService))
                .and()
                .folder()
                .action(new JobsAction(jenkinsService, userStateService))
                .and()
                .runnable()
                .action(new RunnableAction())
                .and()
                .callbackStartsWith("/r")
                .state(UserState.WAIT_COMMAND)
                .action(new RunJobCallbackAction(jobRunQueueService, userStateService, jenkinsServerWrapper))
                .and()
                .callbackStartsWith("/l")
                .state(UserState.WAIT_COMMAND)
                .action(new LastBuildAction(jenkinsService, userStateService))
                .and()
                .input()
                .state(UserState.WAIT_PARAMETERS)
                .action(new RunJobWithParametersCommandAction(jobRunQueueService, userStateService, argsParserService))
                .and()
                .build();
    }
}
