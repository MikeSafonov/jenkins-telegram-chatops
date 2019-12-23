package com.github.mikesafonov.jenkins.telegram.chatops.config;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

/**
 * @author Mike Safonov
 */
@Configuration
public class ApplicationConfiguration {

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
}
