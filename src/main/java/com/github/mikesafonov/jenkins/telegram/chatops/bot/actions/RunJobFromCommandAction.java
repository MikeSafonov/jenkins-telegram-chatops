package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import com.github.mikesafonov.jenkins.telegram.chatops.utils.HexUtils;

/**
 * @author Mike Safonov
 */
public class RunJobFromCommandAction extends BaseRunJobAction {

    public RunJobFromCommandAction(JobRunQueueService jobRunQueueService) {
        super(jobRunQueueService);
    }

    @Override
    public void accept(CommandContext context) {
        String commandText = context.getCommandText();
        String jobNameHex = commandText
            .replace("/run_", "")
            .replace("/r_", "");
        String jobName = HexUtils.fromHex(jobNameHex);
        runJob(jobName, context);
    }
}
