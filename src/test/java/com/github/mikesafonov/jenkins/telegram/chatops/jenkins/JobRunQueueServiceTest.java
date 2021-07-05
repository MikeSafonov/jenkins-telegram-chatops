package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
public class JobRunQueueServiceTest {
    private JenkinsService jenkinsService;
    private TelegramBotSender telegramBotSender;
    private JobRunQueueService jobRunQueueService;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        jenkinsService = mock(JenkinsService.class);
        telegramBotSender = mock(TelegramBotSender.class);
        executor = Executors.newSingleThreadExecutor();
        jobRunQueueService = new JobRunQueueService(jenkinsService, telegramBotSender, executor);
    }

    @Test
    void shouldAddNewJobInQueue() throws InterruptedException {
        JobToRun job = new JobToRun("name", 1L);
        BuildWithDetails build = mock(BuildWithDetails.class);
        String url = "some.url";
        when(build.getResult()).thenReturn(BuildResult.SUCCESS);
        when(build.getUrl()).thenReturn(url);

        var message = "Build of *" + job.getJobName() + "* has been finished\nResult: *"
                + build.getResult() + "*\n[Launch on Jenkins](" + build.getUrl() + ")";

        when(jenkinsService.runJob(job)).thenReturn(build);

        jobRunQueueService.registerJob(job);
        jobRunQueueService.runJobs();

        executor.awaitTermination(1, TimeUnit.SECONDS);

        verify(jenkinsService).runJob(job);
        verify(telegramBotSender).sendMarkdownTextMessage(job.getUserId(), message);
    }

    @Test
    void shouldNotifyAboutException() throws InterruptedException {
        JobToRun job = new JobToRun("name", 1L);

        var exception = new RuntimeException("exception message");
        var message = "Exception when running job *" + job.getJobName() + "*:\n" + exception.getMessage();

        when(jenkinsService.runJob(job)).thenThrow(exception);

        jobRunQueueService.registerJob(job);
        jobRunQueueService.runJobs();

        executor.awaitTermination(1, TimeUnit.SECONDS);

        verify(jenkinsService).runJob(job);
        verify(telegramBotSender).sendMarkdownTextMessage(job.getUserId(), message);
    }

}
