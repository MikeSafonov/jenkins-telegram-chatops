package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.ArgsParserService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserStateService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class RunJobWithParametersCommandAction implements Consumer<CommandContext> {
    private final JobRunQueueService jobRunQueueService;
    private final UserStateService userStateService;
    private final ArgsParserService argsParserService;

    @Override
    public void accept(CommandContext context) {
        var jobName = userStateService.getJobName(context.getChatId());
        Map<String, String> parameters = Collections.emptyMap();
        if (context.getArgs().length > 0) {
            parameters = argsParserService.parse(context.getArgs());
        }
        Long chatId = context.getChatId();
        doRunJob(context, new JobToRun(jobName, chatId, parameters));
        userStateService.update(context.getChatId(), UserState.WAIT_COMMAND);
    }

    private void doRunJob(CommandContext context, JobToRun jobToRun) {
        jobRunQueueService.registerJob(jobToRun);
        context.getSender().sendMarkdownTextMessage(context.getChatId(),
                "Job *" + jobToRun.getJobName() + "* registered to run");
    }
}
