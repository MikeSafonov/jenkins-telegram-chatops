package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import com.github.mikesafonov.jenkins.telegram.chatops.utils.DurationFormatter;
import com.offbytwo.jenkins.model.BuildResult;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class LastBuildAction implements Consumer<CommandContext> {

    private final JenkinsService jenkinsService;

    @Override
    public void accept(CommandContext context) {
        String jobName = context.getArgs()[0];
        var lastBuild = jenkinsService.getLastBuild(jobName);
        String message;
        if (lastBuild.getResult() == BuildResult.NOT_BUILT) {
            message = "No last build of *" + jobName + "*";
        } else {
            message = "Last build of *" +
                jobName +
                "* #" +
                lastBuild.getNumber() +
                "\n*Result*: " +
                lastBuild.getResult() +
                "\n*Duration*: " +
                getDurationString(lastBuild.getDuration()) +
                "\n[Launch on Jenkins](" +
                lastBuild.getUrl() +
                ")";
        }
        context.getSender().sendMarkdownTextMessage(context.getChatId(), message);
    }

    private String getDurationString(long msValue) {
        Duration duration = Duration.of(msValue, ChronoUnit.MILLIS);
        return new DurationFormatter(duration).format();
    }
}
