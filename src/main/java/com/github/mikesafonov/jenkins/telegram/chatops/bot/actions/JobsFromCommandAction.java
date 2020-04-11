package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import com.github.mikesafonov.jenkins.telegram.chatops.utils.HexUtils;

import java.util.List;

/**
 * @author Mike Safonov
 */
public class JobsFromCommandAction extends BaseJobsMessageAction {
    private final JenkinsService jenkinsService;

    public JobsFromCommandAction(MessageBuilderService messageBuilderService, JenkinsService jenkinsService) {
        super(messageBuilderService);
        this.jenkinsService = jenkinsService;
    }

    @Override
    public void accept(CommandContext context) {
        String folderNameHex = context.getCommandText().replace("/jobs_", "");
        String folderName = HexUtils.fromHex(folderNameHex);
        List<JenkinsJob> jobs = jenkinsService.getJobsInFolder(folderName);
        sendJobsListMessage(context, folderName, jobs);
    }
}
