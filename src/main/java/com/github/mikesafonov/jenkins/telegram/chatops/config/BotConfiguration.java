package com.github.mikesafonov.jenkins.telegram.chatops.config;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.*;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.Command;
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
                                  JobRunQueueService jobRunQueueService) {
        return List.of(
                Command.builder()
                        .command(Command.equals("/help"))
                        .authorized(true)
                        .action(new SendHelpMessageAction(messageBuilderService))
                        .build(),
                Command.builder()
                        .command(Command.equals("/jobs"))
                        .authorized(true)
                        .action(new RootJobsMessageAction(messageBuilderService, jenkinsService))
                        .build(),
                Command.builder()
                        .command(Command.equals("/jobs"))
                        .authorized(true)
                        .argsCount(1)
                        .action(new JobsFromArgsAction(messageBuilderService, jenkinsService))
                        .build(),
                Command.builder()
                        .command(Command.startsWith("/jobs_"))
                        .authorized(true)
                        .action(new JobsFromCommandAction(messageBuilderService, jenkinsService))
                        .build(),
                Command.builder()
                        .command(Command.startsWith("/run"))
                        .authorized(true)
                        .argsCount(1)
                        .action(new RunJobFromArgsAction(jobRunQueueService))
                        .build(),
                Command.builder()
                        .command(Command.startsWith("/run_"))
                        .authorized(true)
                        .action(new RunJobFromCommandAction(jobRunQueueService))
                        .build()
        );
    }
}
