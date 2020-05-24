package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.offbytwo.jenkins.model.BuildWithDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * In-memory queue of Jenkins jobs to run
 *
 * @author Mike Safonov
 */
@Service
@RequiredArgsConstructor
public class JobRunQueueService {

    private final JenkinsService jenkinsService;
    private final TelegramBotSender telegramBotSender;
    private final Executor jobRunExecutor;
    private BlockingQueue<JobToRun> jobs = new LinkedBlockingQueue<>();

    /**
     * add new job to queue
     *
     * @param jobToRun Jenkins job
     */
    public void registerJob(JobToRun jobToRun) {
        jobs.add(jobToRun);
    }

    @Scheduled(fixedDelay = 1000)
    public void runJobs() {
        getJobsToRun().forEach(this::doRunJob);
    }

    /**
     * @return all current jobs from {@code jobs} queue.
     */
    private List<JobToRun> getJobsToRun() {
        var jobToRuns = new ArrayList<JobToRun>();
        jobs.drainTo(jobToRuns);
        return jobToRuns;
    }

    private void doRunJob(JobToRun job) {
        CompletableFuture.supplyAsync(() -> jenkinsService.runJob(job.getJobName(), job.getParameters()), jobRunExecutor)
            .thenAccept(build -> {
                telegramBotSender.sendMarkdownTextMessage(job.getUserId(),
                    createSuccessMessage(job, build));
            })
            .exceptionally(e -> {
                telegramBotSender.sendMarkdownTextMessage(job.getUserId(),
                    createExceptionallyMessage(job, e));
                return null;
            });
    }

    private String createSuccessMessage(JobToRun job, BuildWithDetails build) {
        return "Build of *" + job.getJobName() + "* has been finished\nResult: *"
            + build.getResult() + "*\n[Launch on Jenkins](" + build.getUrl() + ")";
    }

    private String createExceptionallyMessage(JobToRun job, Throwable e) {
        return "Exception when running job *" + job.getJobName() + "*:\n" + e.getCause().getMessage();
    }
}
