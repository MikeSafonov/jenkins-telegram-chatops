package com.github.mikesafonov.jenkins.telegram.chatops.config;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.ArgsParserService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.*;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.Command;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.builder.CommandsBuilder;
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
                                  ArgsParserService argsParserService) {
        return new CommandsBuilder().command("/help")
            .action(new SendHelpMessageAction(messageBuilderService))
            .and()
            .command("/h")
            .action(new SendHelpMessageAction(messageBuilderService))
            .and()
            .command("/jobs")
            .args().noArgs()
            .action(new RootJobsMessageAction(messageBuilderService, jenkinsService))
            .and()
            .command("/j")
            .args().noArgs()
            .action(new RootJobsMessageAction(messageBuilderService, jenkinsService))
            .and()
            .command("/jobs")
            .args().length(1)
            .action(new JobsFromArgsAction(messageBuilderService, jenkinsService))
            .and()
            .command("/j")
            .args().length(1)
            .action(new JobsFromArgsAction(messageBuilderService, jenkinsService))
            .and()
            .commandStartsWith("/jobs_")
            .args().noArgs()
            .action(new JobsFromCommandAction(messageBuilderService, jenkinsService))
            .and()
            .commandStartsWith("/j_")
            .args().noArgs()
            .action(new JobsFromCommandAction(messageBuilderService, jenkinsService))
            .and()
            .commandStartsWith("/run")
            .args().minLength(1)
            .action(new RunJobFromArgsAction(jobRunQueueService, argsParserService))
            .and()
            .commandStartsWith("/r")
            .args().minLength(1)
            .action(new RunJobFromArgsAction(jobRunQueueService, argsParserService))
            .and()
            .commandStartsWith("/run_")
            .action(new RunJobFromCommandAction(jobRunQueueService))
            .and()
            .commandStartsWith("/r_")
            .action(new RunJobFromCommandAction(jobRunQueueService))
            .and()
            .commandStartsWith("/last")
            .args().length(1)
            .action(new LastBuildAction(jenkinsService))
            .and()
            .commandStartsWith("/l")
            .args().length(1)
            .action(new LastBuildAction(jenkinsService))
            .and()
            .build();
    }
}
