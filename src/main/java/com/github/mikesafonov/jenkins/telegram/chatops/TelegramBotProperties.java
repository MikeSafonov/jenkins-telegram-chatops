package com.github.mikesafonov.jenkins.telegram.chatops;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mike Safonov
 */
@Component
@Data
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotProperties {
    private String name;
    private String token;
    private String proxyHost;
    private Integer proxyPort;

    private int connectionTimeout;
    private int connectionRequestTimeout;
    private int socketTimeout;

    private List<Long> users;
}
