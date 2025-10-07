package com.example.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.example.event.configuration.EventAutoConfiguration;
import com.example.event.configuration.EventProperties;
import com.example.event.listener.ApplicationLifecycleEventListener;

class EventAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(EventAutoConfiguration.class));

    @Test
    void whenNoPropertiesSet_thenEventListenerIsCreated() {
        contextRunner.withUserConfiguration(TestConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ApplicationLifecycleEventListener.class);
                    assertThat(context).hasSingleBean(EventProperties.class);
                    
                    EventProperties properties = context.getBean(EventProperties.class);
                    assertThat(properties.isEnabled()).isTrue();
                });
    }

    @Test
    void whenEventsDisabled_thenEventListenerIsNotCreated() {
        contextRunner.withUserConfiguration(TestConfig.class)
                .withPropertyValues("application.events.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ApplicationLifecycleEventListener.class);
                    assertThat(context).hasSingleBean(EventProperties.class);
                    
                    EventProperties properties = context.getBean(EventProperties.class);
                    assertThat(properties.isEnabled()).isFalse();
                });
    }

    @Test
    void whenCustomListenerExists_thenAutoConfiguredListenerIsNotCreated() {
        contextRunner.withUserConfiguration(CustomListenerConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ApplicationLifecycleEventListener.class);
                    assertThat(context.getBean(ApplicationLifecycleEventListener.class))
                            .isInstanceOf(CustomApplicationLifecycleEventListener.class);
                });
    }

    @Configuration
    static class TestConfig {
        @Bean
        Environment environment() {
            return new org.springframework.mock.env.MockEnvironment();
        }
    }

    @Configuration
    static class CustomListenerConfig {
        @Bean
        Environment environment() {
            return new org.springframework.mock.env.MockEnvironment();
        }

        @Bean
        ApplicationLifecycleEventListener customListener(Environment environment) {
            return new CustomApplicationLifecycleEventListener(environment);
        }
    }

    static class CustomApplicationLifecycleEventListener extends ApplicationLifecycleEventListener {
        CustomApplicationLifecycleEventListener(Environment environment) {
            super(environment);
        }
    }
}
