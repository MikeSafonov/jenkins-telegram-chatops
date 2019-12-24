package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.offbytwo.jenkins.model.Job;

/**
 * Builder class for Jenkins job name.
 *
 * @author Mike Safonov
 */
public class JobNameBuilder {
    private Job jenkinsJob;
    private String folderName;

    private JobNameBuilder(Job jenkinsJob) {
        this.jenkinsJob = jenkinsJob;
    }

    public static JobNameBuilder from(JenkinsJob jenkinsJob) {
        return new JobNameBuilder(jenkinsJob.getOriginalJob());
    }

    public static JobNameBuilder from(Job job) {
        return new JobNameBuilder(job);
    }

    public JobNameBuilder inFolder(String folderName) {
        this.folderName = folderName;
        return this;
    }

    public String build() {
        if (folderName == null) {
            return (jenkinsJob.getFullName() == null) ?
                    jenkinsJob.getName() :
                    jenkinsJob.getFullName();
        } else {
            return (jenkinsJob.getFullName() == null) ?
                    folderName + "/" + jenkinsJob.getName() :
                    jenkinsJob.getFullName();
        }
    }
}
