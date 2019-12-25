package com.github.mikesafonov.jenkins.telegram.chatops.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Mike Safonov
 */
@Data
@Component
@ConfigurationProperties(prefix = "build")
public class BuildInfo {
    private String time;
    private String artifact;
    private String group;
    private String name;
    private String version;
}
