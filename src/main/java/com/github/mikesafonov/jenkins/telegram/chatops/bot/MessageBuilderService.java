package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.config.BuildInfo;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mike Safonov
 */
@Service
@RequiredArgsConstructor
public class MessageBuilderService {
    private final BuildInfo buildInfo;

    public String getHelpMessage() {
        return "This is [jenkins-telegram-chatops](https://github.com/MikeSafonov/jenkins-telegram-chatops) version " + buildInfo.getVersion() +
                "\n\nSupported commands:\n" +
                "*/jobs* - listing Jenkins jobs\n" +
                "*/run* _jobName_ - running specific Jenkins job\n" +
                "*/help* - prints help message";
    }

    public String buildMessageForJob(JenkinsJob jenkinsJob) {
        StringBuilder stringBuilder = new StringBuilder();
        if (jenkinsJob.isFolder()) {
            stringBuilder
                    .append("\uD83D\uDDBF");
        } else {
            stringBuilder.append("⚫");
        }
        return stringBuilder
                .append(jenkinsJob.getOriginalJob().getName())
                .append("\n")
                .toString();
    }
}
