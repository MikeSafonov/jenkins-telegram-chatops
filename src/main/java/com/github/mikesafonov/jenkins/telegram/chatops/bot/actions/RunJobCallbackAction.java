package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserStateService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsServerWrapper;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.ParametersDefinitionProperty;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class RunJobCallbackAction implements Consumer<CommandContext> {
    private final JobRunQueueService jobRunQueueService;
    private final UserStateService userStateService;
    private final JenkinsServerWrapper jenkinsServerWrapper;

    @Override
    public void accept(CommandContext context) {
        var jobName = context.getUpdate().getCallbackQuery().getData().replace("/r ", "");
        var job = jenkinsServerWrapper.getJobByNameWithProperties(jobName);
        var requiredParameters = job.getParametersDefinitionProperty()
                .map(ParametersDefinitionProperty::getParameterDefinitions)
                .orElseGet(List::of);
        if (requiredParameters.isEmpty()) {
            Long chatId = context.getChatId();
            doRunJob(context, new JobToRun(jobName, chatId));
            userStateService.update(context.getChatId(), UserState.WAIT_COMMAND);
        } else {
            var builder = new StringBuilder();
            builder.append("Job *" + jobName + "* requires input parameters:\n");
            for (var parameter : requiredParameters) {
                builder.append(parameter.toString());
            }
            context.getSender().sendMarkdownTextMessage(context.getChatId(), builder.toString());
            userStateService.update(context.getChatId(), UserState.WAIT_PARAMETERS, jobName);
        }
    }

    private void doRunJob(CommandContext context, JobToRun jobToRun) {
        jobRunQueueService.registerJob(jobToRun);
        context.getSender().sendMarkdownTextMessage(context.getChatId(),
                "Job *" + jobToRun.getJobName() + "* registered to run");
    }
}
