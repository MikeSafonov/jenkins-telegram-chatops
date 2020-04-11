package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;

/**
 * @author Mike Safonov
 */
public class RunJobFromArgsAction extends BaseRunJobAction {

    public RunJobFromArgsAction(JobRunQueueService jobRunQueueService) {
        super(jobRunQueueService);
    }

    @Override
    public void accept(CommandContext context) {
        String jobName = context.getArgs()[0];
        runJob(jobName, context);
    }
}
