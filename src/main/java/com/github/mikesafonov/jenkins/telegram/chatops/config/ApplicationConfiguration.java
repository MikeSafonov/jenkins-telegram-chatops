package com.github.mikesafonov.jenkins.telegram.chatops.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Mike Safonov
 */
@Configuration
@EnableRetry
@EnableScheduling
@PropertySource(value = {"classpath:META-INF/build-info.properties"}, ignoreResourceNotFound = true)
public class ApplicationConfiguration {

    @Bean
    public JenkinsHttpClient jenkinsHttpClient(JenkinsInstanceProperties jenkinsInstanceProperties) throws URISyntaxException {
        JenkinsHttpClient client = new JenkinsHttpClient(
                new URI(jenkinsInstanceProperties.getUrl()),
                jenkinsInstanceProperties.getUsername(),
                jenkinsInstanceProperties.getToken()
        );

        Field field = null;
        try {
            // hack for setting FAIL_ON_INVALID_SUBTYPE to false
            field = JenkinsHttpClient.class.getDeclaredField("mapper");
            field.setAccessible(true);
            ObjectMapper objectMapper = (ObjectMapper) field.get(client);
            objectMapper.disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
            objectMapper.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }


        return client;
    }

    @Bean
    public JenkinsServer jenkinsServer(JenkinsHttpClient jenkinsHttpClient) {
        return new JenkinsServer(
                jenkinsHttpClient
        );
    }

    @Bean
    public DefaultBotOptions botOptions(TelegramBotProperties telegramBotProperties) {
        DefaultBotOptions botOptions = new DefaultBotOptions();
        RequestConfig.Builder builder = RequestConfig.custom()
                .setSocketTimeout(telegramBotProperties.getSocketTimeout())
                .setConnectionRequestTimeout(telegramBotProperties.getConnectionRequestTimeout())
                .setConnectTimeout(telegramBotProperties.getConnectionTimeout());
        if (telegramBotProperties.getProxyHost() != null && telegramBotProperties.getProxyPort() != null) {
            HttpHost httpHost = new HttpHost(telegramBotProperties.getProxyHost(), telegramBotProperties.getProxyPort());
            builder = builder.setProxy(httpHost);
        }
        botOptions.setRequestConfig(builder.build());
        return botOptions;
    }

    @Bean
    public Executor jobRunExecutor(JenkinsInstanceProperties properties){
        return Executors.newFixedThreadPool(properties.getPoolSize());
    }
}
