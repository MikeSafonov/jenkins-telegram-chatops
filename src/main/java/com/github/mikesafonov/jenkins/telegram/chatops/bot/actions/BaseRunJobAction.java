package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public abstract class BaseRunJobAction implements Consumer<CommandContext> {

    protected final JobRunQueueService jobRunQueueService;

    protected void runJob(String jobName, CommandContext context, Map<String, String> parameters) {
        Long chatId = context.getChatId();
        doRunJob(context, new JobToRun(jobName, chatId, parameters));
    }

    protected void runJob(String jobName, CommandContext context) {
        Long chatId = context.getChatId();
        doRunJob(context, new JobToRun(jobName, chatId));
    }

    private void doRunJob(CommandContext context, JobToRun jobToRun) {
        jobRunQueueService.registerJob(jobToRun);
        context.getSender().sendMarkdownTextMessage(context.getChatId(),
            "Job *" + jobToRun.getJobName() + "* registered to run");
    }
}
