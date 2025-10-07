package com.example.event.listener;

import com.tbw.security.securityevents.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Component
public class ApplicationLifecycleEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationLifecycleEventListener.class);
    private final Environment environment;
    private final Instant applicationStartTime;

    public ApplicationLifecycleEventListener(Environment environment) {
        this.environment = environment;
        this.applicationStartTime = Instant.now();
    }

    private EventHeader createEventHeader(Severity severity) {
        return EventHeader.newBuilder()
                .setEventId("evt-" + UUID.randomUUID().toString())
                .setEventVersion("1.0")
                .setTimestamp(Instant.now())
                .setCorrelationId(null)
                .setTraceId(null)
                .setApplicationName(environment.getProperty("spring.application.name", "unknown"))
                .setApplicationVersion(environment.getProperty("spring.application.version", "unknown"))
                .setEnvironment(com.tbw.security.securityevents.Environment.valueOf(
                    environment.getProperty("spring.profiles.active", "DEVELOPMENT").toUpperCase()))
                .setSourceHost(environment.getProperty("HOST", "localhost"))
                .setSourceInstanceId(null)
                .setUserId(null)
                .setSessionId(null)
                .setSeverity(severity)
                .build();
    }

    @EventListener
    public void handleApplicationStarting(ApplicationStartingEvent event) {
        ApplicationStartup startupBody = ApplicationStartup.newBuilder()
                .setStartupTimeMs(0L) // Not completed yet
                .setJvmVersion(System.getProperty("java.version"))
                .setMemoryAllocatedMb(Runtime.getRuntime().totalMemory() / (1024 * 1024))
                .setConfigurationProfile(environment.getProperty("spring.profiles.active", "default"))
                .setEnabledFeatures(Arrays.asList(environment.getActiveProfiles()))
                .build();

        ApplicationLifecycleEvent lifecycleEvent = ApplicationLifecycleEvent.newBuilder()
                .setHeader(createEventHeader(Severity.INFO))
                .setBody(startupBody)
                .build();

        logger.info("Application Starting: {}", lifecycleEvent);
    }

    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        ApplicationStartup startupBody = ApplicationStartup.newBuilder()
                .setStartupTimeMs(System.currentTimeMillis() - applicationStartTime.toEpochMilli())
                .setJvmVersion(System.getProperty("java.version"))
                .setMemoryAllocatedMb(Runtime.getRuntime().totalMemory() / (1024 * 1024))
                .setConfigurationProfile(environment.getProperty("spring.profiles.active", "default"))
                .setEnabledFeatures(Arrays.asList(environment.getActiveProfiles()))
                .build();

        ApplicationLifecycleEvent lifecycleEvent = ApplicationLifecycleEvent.newBuilder()
                .setHeader(createEventHeader(Severity.INFO))
                .setBody(startupBody)
                .build();

        logger.info("Application Ready: {}", lifecycleEvent);
    }

    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        long uptimeSeconds = (System.currentTimeMillis() - applicationStartTime.toEpochMilli()) / 1000;
        
        ApplicationShutdown shutdownBody = ApplicationShutdown.newBuilder()
                .setShutdownReason(ShutdownReason.GRACEFUL)
                .setUptimeSeconds(uptimeSeconds)
                .setFinalMemoryUsageMb(Runtime.getRuntime().totalMemory() / (1024 * 1024))
                .setCleanupTimeMs(0L)
                .setExitCode(0)
                .build();

        ApplicationLifecycleEvent lifecycleEvent = ApplicationLifecycleEvent.newBuilder()
                .setHeader(createEventHeader(Severity.INFO))
                .setBody(shutdownBody)
                .build();

        logger.info("Application Shutting Down: {}", lifecycleEvent);
    }
}
