package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;

import java.util.List;

/**
 * @author Mike Safonov
 */
public class RootJobsMessageAction extends BaseJobsMessageAction {
    private final JenkinsService jenkinsService;

    public RootJobsMessageAction(MessageBuilderService messageBuilderService, JenkinsService jenkinsService) {
        super(messageBuilderService);
        this.jenkinsService = jenkinsService;
    }

    @Override
    public void accept(CommandContext context) {
        List<JenkinsJob> jobs = jenkinsService.getJobs();
        sendJobsListMessage(context, null, jobs);
    }
}
