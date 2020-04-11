package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobNameBuilder;
import com.github.mikesafonov.jenkins.telegram.chatops.utils.HexUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public abstract class BaseJobsMessageAction implements Consumer<CommandContext> {
    protected final MessageBuilderService messageBuilderService;

    protected void sendJobsListMessage(CommandContext context, String folderName, List<JenkinsJob> jobs) {
        StringBuilder stringBuilder = new StringBuilder();
        jobs.forEach(jenkinsJob -> {
            String textMessage = messageBuilderService.buildMessageForJob(jenkinsJob);
            String jobName = JobNameBuilder.from(jenkinsJob)
                    .inFolder(folderName)
                    .build();
            String jobHex = HexUtils.toHex(jobName);
            stringBuilder.append(textMessage).append("\n");

            if (jenkinsJob.isBuildable()) {
                stringBuilder.append("/run_").append(jobHex);
            } else {
                stringBuilder.append("/jobs_").append(jobHex);
            }
            stringBuilder.append("\n\n");
        });
        context.getSender().sendTextMessage(context.getChatId(), stringBuilder.toString());
    }
}
