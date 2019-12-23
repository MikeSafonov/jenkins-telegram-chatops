package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

/**
 * @author Mike Safonov
 */
public class JobNameBuilder {
    private JenkinsJob jenkinsJob;
    private String folderName;

    public JobNameBuilder(JenkinsJob jenkinsJob) {
        this.jenkinsJob = jenkinsJob;
    }

    public static JobNameBuilder from(JenkinsJob jenkinsJob) {
        return new JobNameBuilder(jenkinsJob);
    }

    public JobNameBuilder inFolder(String folderName) {
        this.folderName = folderName;
        return this;
    }

    public String build() {
        if (folderName == null) {
            return (jenkinsJob.getOriginalJob().getFullName() == null) ?
                    jenkinsJob.getOriginalJob().getName() :
                    jenkinsJob.getOriginalJob().getFullName();
        } else {
            return (jenkinsJob.getOriginalJob().getFullName() == null) ?
                    folderName + "/" + jenkinsJob.getOriginalJob().getName() :
                    jenkinsJob.getOriginalJob().getFullName();
        }
    }
}
