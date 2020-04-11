package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;

import java.util.List;

/**
 * @author Mike Safonov
 */
public class JobsFromArgsAction extends BaseJobsMessageAction {
    private final JenkinsService jenkinsService;

    public JobsFromArgsAction(MessageBuilderService messageBuilderService, JenkinsService jenkinsService) {
        super(messageBuilderService);
        this.jenkinsService = jenkinsService;
    }

    @Override
    public void accept(CommandContext context) {
        String folderName = context.getArgs()[0];
        List<JenkinsJob> jobs = jenkinsService.getJobsInFolder(folderName);
        sendJobsListMessage(context, folderName, jobs);
    }
}
