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
        CommandsBuilder builder = new CommandsBuilder();
        return builder.command("/help")
            .authorized()
            .action(new SendHelpMessageAction(messageBuilderService))
            .and()
            .command("/jobs")
            .authorized()
            .action(new RootJobsMessageAction(messageBuilderService, jenkinsService))
            .and()
            .command("/jobs")
            .authorized()
            .args().length(1)
            .action(new JobsFromArgsAction(messageBuilderService, jenkinsService))
            .and()
            .commandStartsWith("/jobs_")
            .authorized()
            .args().noArgs()
            .action(new JobsFromCommandAction(messageBuilderService, jenkinsService))
            .and()
            .commandStartsWith("/run")
            .args().minLength(1)
            .authorized()
            .action(new RunJobFromArgsAction(jobRunQueueService, argsParserService))
            .and()
            .commandStartsWith("/run_")
            .authorized()
            .action(new RunJobFromCommandAction(jobRunQueueService))
            .and()
            .build();
    }
}
