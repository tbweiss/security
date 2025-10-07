package com.example.event.configuration;

import com.example.event.listener.ApplicationLifecycleEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(EventProperties.class)
public class EventAutoConfiguration {

    private final EventProperties properties;

    public EventAutoConfiguration(EventProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "application.events", name = "enabled", matchIfMissing = true)
    public ApplicationLifecycleEventListener applicationLifecycleEventListener(Environment environment) {
        return new ApplicationLifecycleEventListener(environment);
    }
}
