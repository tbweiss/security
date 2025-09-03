package com.tbw.security.securityevents;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationLifecycleEventTest {

    @Test
    void testEventCreation() {
        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(EventHeader.newBuilder()
            .setEventId("test-id")
            .setEventVersion("1.0")
            .setTimestamp(java.time.Instant.ofEpochMilli(System.currentTimeMillis()))
            .setApplicationName("test-app")
            .setApplicationVersion("1.0.0")
            .setEnvironment(Environment.DEVELOPMENT)
            .setSourceHost("localhost")
            .setSeverity(Severity.INFO)
            .build())
            .build();

        assertNotNull(event);
        assertEquals("test-id", event.getHeader().getEventId());
        assertEquals("test-app", event.getHeader().getApplicationName());
        assertEquals(Environment.DEVELOPMENT, event.getHeader().getEnvironment());
    }
}
