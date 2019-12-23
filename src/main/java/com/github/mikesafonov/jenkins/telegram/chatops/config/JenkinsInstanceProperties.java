package com.github.mikesafonov.jenkins.telegram.chatops.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Mike Safonov
 */
@Component
@Data
@ConfigurationProperties(prefix = "jenkins")
public class JenkinsInstanceProperties {
    private String url;
    private String username;
    private String token;
}
