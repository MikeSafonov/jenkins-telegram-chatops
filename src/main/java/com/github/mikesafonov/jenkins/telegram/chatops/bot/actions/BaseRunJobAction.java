package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public abstract class BaseRunJobAction implements Consumer<CommandContext> {
    protected final JobRunQueueService jobRunQueueService;

    protected void runJob(String jobName, CommandContext context) {
        Long chatId = context.getChatId();
        jobRunQueueService.registerJob(new JobToRun(jobName, chatId));
        context.getSender().sendMarkdownTextMessage(chatId, "Job *" + jobName + "* registered to run");
    }

}
