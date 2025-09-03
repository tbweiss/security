package com.tbw.security.securityevents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;
import java.util.Arrays;

class ApplicationLifecycleEventTest {
    private EventHeader testHeader;
    private static final String TEST_EVENT_ID = "test-" + UUID.randomUUID();
    private static final String TEST_APP_NAME = "security-app";
    private static final String TEST_APP_VERSION = "2.0.0";
    private static final String TEST_SOURCE_HOST = "app-server-01";
    private static final String TEST_CORRELATION_ID = "corr-" + UUID.randomUUID();
    private static final String TEST_TRACE_ID = "trace-" + UUID.randomUUID();
    private static final String TEST_USER_ID = "user-123";
    private static final String TEST_SESSION_ID = "session-456";

    @BeforeEach
    void setUp() {
        testHeader = EventHeader.newBuilder()
            .setEventId(TEST_EVENT_ID)
            .setEventVersion("1.0")
            .setTimestamp(Instant.now())
            .setCorrelationId(TEST_CORRELATION_ID)
            .setTraceId(TEST_TRACE_ID)
            .setApplicationName(TEST_APP_NAME)
            .setApplicationVersion(TEST_APP_VERSION)
            .setEnvironment(Environment.DEVELOPMENT)
            .setSourceHost(TEST_SOURCE_HOST)
            .setSourceInstanceId(null)
            .setUserId(TEST_USER_ID)
            .setSessionId(TEST_SESSION_ID)
            .setSeverity(Severity.INFO)
            .build();
    }

        @Test
    void testEventHeaderCreation() {
        assertNotNull(testHeader);
        assertEquals(TEST_EVENT_ID, testHeader.getEventId());
        assertEquals("1.0", testHeader.getEventVersion());
        assertNotNull(testHeader.getTimestamp());
        assertEquals(TEST_CORRELATION_ID, testHeader.getCorrelationId());
        assertEquals(TEST_TRACE_ID, testHeader.getTraceId());
        assertEquals(TEST_APP_NAME, testHeader.getApplicationName());
        assertEquals(TEST_APP_VERSION, testHeader.getApplicationVersion());
        assertEquals(Environment.DEVELOPMENT, testHeader.getEnvironment());
        assertEquals(TEST_SOURCE_HOST, testHeader.getSourceHost());
        assertNull(testHeader.getSourceInstanceId());
        assertEquals(TEST_USER_ID, testHeader.getUserId());
        assertEquals(TEST_SESSION_ID, testHeader.getSessionId());
        assertEquals(Severity.INFO, testHeader.getSeverity());
    }

    @Test
    void testApplicationStartupEvent() {
        ApplicationStartup startupBody = ApplicationStartup.newBuilder()
            .setStartupTimeMs(1500L)
            .setJvmVersion("11.0.12")
            .setMemoryAllocatedMb(1024L)
            .setConfigurationProfile("dev")
            .setEnabledFeatures(Arrays.asList("feature1", "feature2"))
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(startupBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof ApplicationStartup);
        
        ApplicationStartup actualBody = (ApplicationStartup) event.getBody();
        assertEquals(1500L, actualBody.getStartupTimeMs());
        assertEquals("11.0.12", actualBody.getJvmVersion());
        assertEquals(1024L, actualBody.getMemoryAllocatedMb());
        assertEquals("dev", actualBody.getConfigurationProfile());
        assertEquals(Arrays.asList("feature1", "feature2"), actualBody.getEnabledFeatures());
    }

    @Test
    void testApplicationShutdownEvent() {
        ApplicationShutdown shutdownBody = ApplicationShutdown.newBuilder()
            .setShutdownReason(ShutdownReason.GRACEFUL)
            .setUptimeSeconds(3600L)  // 1 hour uptime
            .setFinalMemoryUsageMb(2048L)
            .setCleanupTimeMs(500L)
            .setExitCode(0)
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(shutdownBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof ApplicationShutdown);
        
        ApplicationShutdown actualBody = (ApplicationShutdown) event.getBody();
        assertEquals(ShutdownReason.GRACEFUL, actualBody.getShutdownReason());
        assertEquals(3600L, actualBody.getUptimeSeconds());
        assertEquals(2048L, actualBody.getFinalMemoryUsageMb());
        assertEquals(500L, actualBody.getCleanupTimeMs());
        assertEquals(0, actualBody.getExitCode());
    }

    @Test
    void testConfigurationChangeEvent() {
        // Test a configuration change that requires restart
        ConfigurationChange configBody = ConfigurationChange.newBuilder()
            .setConfigKey("database.max_connections")
            .setOldValue("50")
            .setNewValue("100")
            .setChangeSource(ConfigSource.ADMIN_INTERFACE)
            .setRequiresRestart(true)
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(configBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof ConfigurationChange);
        
        ConfigurationChange actualBody = (ConfigurationChange) event.getBody();
        assertEquals("database.max_connections", actualBody.getConfigKey());
        assertEquals("50", actualBody.getOldValue());
        assertEquals("100", actualBody.getNewValue());
        assertEquals(ConfigSource.ADMIN_INTERFACE, actualBody.getChangeSource());
        assertTrue(actualBody.getRequiresRestart());
    }

    @Test
    void testConfigurationChangeEventWithNullValues() {
        // Test a new configuration being added (no old value)
        ConfigurationChange configBody = ConfigurationChange.newBuilder()
            .setConfigKey("feature.new_feature.enabled")
            .setOldValue(null)  // null because it's a new configuration
            .setNewValue("true")
            .setChangeSource(ConfigSource.FILE)
            .setRequiresRestart(false)
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(configBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof ConfigurationChange);
        
        ConfigurationChange actualBody = (ConfigurationChange) event.getBody();
        assertEquals("feature.new_feature.enabled", actualBody.getConfigKey());
        assertNull(actualBody.getOldValue());
        assertEquals("true", actualBody.getNewValue());
        assertEquals(ConfigSource.FILE, actualBody.getChangeSource());
        assertFalse(actualBody.getRequiresRestart());
    }

    @Test
    void testServiceStatusChangeEvent() {
        // Test service transitioning from STARTING to HEALTHY
        ServiceStatusChange statusBody = ServiceStatusChange.newBuilder()
            .setServiceName("auth-service")
            .setPreviousStatus(ServiceStatus.STARTING)
            .setCurrentStatus(ServiceStatus.HEALTHY)
            .setStatusDetails("Service started successfully")
            .setHealthCheckUrl("https://auth-service/health")
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(statusBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof ServiceStatusChange);
        
        ServiceStatusChange actualBody = (ServiceStatusChange) event.getBody();
        assertEquals("auth-service", actualBody.getServiceName());
        assertEquals(ServiceStatus.STARTING, actualBody.getPreviousStatus());
        assertEquals(ServiceStatus.HEALTHY, actualBody.getCurrentStatus());
        assertEquals("Service started successfully", actualBody.getStatusDetails());
        assertEquals("https://auth-service/health", actualBody.getHealthCheckUrl());
    }

    @Test
    void testServiceStatusChangeEventDegraded() {
        // Test service transitioning from HEALTHY to DEGRADED with detailed status
        ServiceStatusChange statusBody = ServiceStatusChange.newBuilder()
            .setServiceName("database-service")
            .setPreviousStatus(ServiceStatus.HEALTHY)
            .setCurrentStatus(ServiceStatus.DEGRADED)
            .setStatusDetails("High latency detected: 500ms response time")
            .setHealthCheckUrl("https://database-service/health")
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(statusBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof ServiceStatusChange);
        
        ServiceStatusChange actualBody = (ServiceStatusChange) event.getBody();
        assertEquals("database-service", actualBody.getServiceName());
        assertEquals(ServiceStatus.HEALTHY, actualBody.getPreviousStatus());
        assertEquals(ServiceStatus.DEGRADED, actualBody.getCurrentStatus());
        assertEquals("High latency detected: 500ms response time", actualBody.getStatusDetails());
        assertEquals("https://database-service/health", actualBody.getHealthCheckUrl());
    }

    @Test
    void testDatabaseMigrationOperationEvent() {
        // Test a successful database migration operation
        DatabaseOperation dbOpBody = DatabaseOperation.newBuilder()
            .setOperationType(DatabaseOperationType.MIGRATION)
            .setDatabaseName("user_management_db")
            .setOperationStatus(OperationStatus.COMPLETED)
            .setDurationMs(15000L)  // 15 seconds
            .setAffectedTables(Arrays.asList("users", "roles", "permissions"))
            .setErrorMessage(null)
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(dbOpBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof DatabaseOperation);
        
        DatabaseOperation actualBody = (DatabaseOperation) event.getBody();
        assertEquals(DatabaseOperationType.MIGRATION, actualBody.getOperationType());
        assertEquals("user_management_db", actualBody.getDatabaseName());
        assertEquals(OperationStatus.COMPLETED, actualBody.getOperationStatus());
        assertEquals(15000L, actualBody.getDurationMs());
        assertEquals(Arrays.asList("users", "roles", "permissions"), actualBody.getAffectedTables());
        assertNull(actualBody.getErrorMessage());
    }

    @Test
    void testDatabaseBackupFailureEvent() {
        // Test a failed database backup operation
        DatabaseOperation dbOpBody = DatabaseOperation.newBuilder()
            .setOperationType(DatabaseOperationType.BACKUP)
            .setDatabaseName("financial_records_db")
            .setOperationStatus(OperationStatus.FAILED)
            .setDurationMs(5000L)  // 5 seconds before failure
            .setAffectedTables(Arrays.asList("transactions", "accounts", "audit_logs"))
            .setErrorMessage("Insufficient disk space for backup")
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(dbOpBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof DatabaseOperation);
        
        DatabaseOperation actualBody = (DatabaseOperation) event.getBody();
        assertEquals(DatabaseOperationType.BACKUP, actualBody.getOperationType());
        assertEquals("financial_records_db", actualBody.getDatabaseName());
        assertEquals(OperationStatus.FAILED, actualBody.getOperationStatus());
        assertEquals(5000L, actualBody.getDurationMs());
        assertEquals(Arrays.asList("transactions", "accounts", "audit_logs"), actualBody.getAffectedTables());
        assertEquals("Insufficient disk space for backup", actualBody.getErrorMessage());
    }

    @Test
    void testDatabaseMaintenanceInProgressEvent() {
        // Test an in-progress database maintenance operation
        DatabaseOperation dbOpBody = DatabaseOperation.newBuilder()
            .setOperationType(DatabaseOperationType.MAINTENANCE)
            .setDatabaseName("analytics_db")
            .setOperationStatus(OperationStatus.IN_PROGRESS)
            .setDurationMs(null)  // null duration because it's still running
            .setAffectedTables(Arrays.asList("metrics", "events", "aggregates"))
            .setErrorMessage(null)
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(dbOpBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof DatabaseOperation);
        
        DatabaseOperation actualBody = (DatabaseOperation) event.getBody();
        assertEquals(DatabaseOperationType.MAINTENANCE, actualBody.getOperationType());
        assertEquals("analytics_db", actualBody.getDatabaseName());
        assertEquals(OperationStatus.IN_PROGRESS, actualBody.getOperationStatus());
        assertNull(actualBody.getDurationMs());  // Duration should be null for in-progress operations
        assertEquals(Arrays.asList("metrics", "events", "aggregates"), actualBody.getAffectedTables());
        assertNull(actualBody.getErrorMessage());
    }

    @Test
    void testAuthenticationFailureSecurityEvent() {
        // Test an authentication failure security event
        SecurityEvent securityBody = SecurityEvent.newBuilder()
            .setEventType(SecurityEventType.AUTHENTICATION_FAILURE)
            .setResource("/api/v1/login")
            .setThreatLevel(ThreatLevel.MEDIUM)
            .setClientIp("192.168.1.100")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(securityBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof SecurityEvent);
        
        SecurityEvent actualBody = (SecurityEvent) event.getBody();
        assertEquals(SecurityEventType.AUTHENTICATION_FAILURE, actualBody.getEventType());
        assertEquals("/api/v1/login", actualBody.getResource());
        assertEquals(ThreatLevel.MEDIUM, actualBody.getThreatLevel());
        assertEquals("192.168.1.100", actualBody.getClientIp());
        assertTrue(actualBody.getUserAgent().contains("Mozilla/5.0"));
    }

    @Test
    void testSuspiciousActivitySecurityEvent() {
        // Test a suspicious activity security event with critical threat level
        SecurityEvent securityBody = SecurityEvent.newBuilder()
            .setEventType(SecurityEventType.SUSPICIOUS_ACTIVITY)
            .setResource("/api/v1/admin/users")
            .setThreatLevel(ThreatLevel.CRITICAL)
            .setClientIp("10.0.0.50")
            .setUserAgent(null)  // Unknown or masked user agent
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(securityBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof SecurityEvent);
        
        SecurityEvent actualBody = (SecurityEvent) event.getBody();
        assertEquals(SecurityEventType.SUSPICIOUS_ACTIVITY, actualBody.getEventType());
        assertEquals("/api/v1/admin/users", actualBody.getResource());
        assertEquals(ThreatLevel.CRITICAL, actualBody.getThreatLevel());
        assertEquals("10.0.0.50", actualBody.getClientIp());
        assertNull(actualBody.getUserAgent());
    }

    @Test
    void testCertificateExpiryWarningEvent() {
        // Test a certificate expiry warning event
        SecurityEvent securityBody = SecurityEvent.newBuilder()
            .setEventType(SecurityEventType.CERTIFICATE_EXPIRY_WARNING)
            .setResource("api.example.com")
            .setThreatLevel(ThreatLevel.HIGH)
            .setClientIp(null)  // Not applicable for certificate events
            .setUserAgent(null)  // Not applicable for certificate events
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(securityBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof SecurityEvent);
        
        SecurityEvent actualBody = (SecurityEvent) event.getBody();
        assertEquals(SecurityEventType.CERTIFICATE_EXPIRY_WARNING, actualBody.getEventType());
        assertEquals("api.example.com", actualBody.getResource());
        assertEquals(ThreatLevel.HIGH, actualBody.getThreatLevel());
        assertNull(actualBody.getClientIp());
        assertNull(actualBody.getUserAgent());
    }

    @Test
    void testPerformanceThresholdExceededEvent() {
        // Test a performance threshold exceeded alert
        PerformanceAlert alertBody = PerformanceAlert.newBuilder()
            .setMetricName("response_time")
            .setCurrentValue(500.0)
            .setThresholdValue(200.0)
            .setAlertType(AlertType.THRESHOLD_EXCEEDED)
            .setMeasurementUnit("ms")
            .setDurationSeconds(300L)  // Condition persisted for 5 minutes
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(alertBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof PerformanceAlert);
        
        PerformanceAlert actualBody = (PerformanceAlert) event.getBody();
        assertEquals("response_time", actualBody.getMetricName());
        assertEquals(500.0, actualBody.getCurrentValue(), 0.001);
        assertEquals(200.0, actualBody.getThresholdValue(), 0.001);
        assertEquals(AlertType.THRESHOLD_EXCEEDED, actualBody.getAlertType());
        assertEquals("ms", actualBody.getMeasurementUnit());
        assertEquals(300L, actualBody.getDurationSeconds());
    }

    @Test
    void testPerformanceAnomalyDetectedEvent() {
        // Test an anomaly detection in memory usage
        PerformanceAlert alertBody = PerformanceAlert.newBuilder()
            .setMetricName("heap_memory_usage")
            .setCurrentValue(2048.0)
            .setThresholdValue(1536.0)
            .setAlertType(AlertType.ANOMALY_DETECTED)
            .setMeasurementUnit("MB")
            .setDurationSeconds(60L)  // Condition detected for 1 minute
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(alertBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof PerformanceAlert);
        
        PerformanceAlert actualBody = (PerformanceAlert) event.getBody();
        assertEquals("heap_memory_usage", actualBody.getMetricName());
        assertEquals(2048.0, actualBody.getCurrentValue(), 0.001);
        assertEquals(1536.0, actualBody.getThresholdValue(), 0.001);
        assertEquals(AlertType.ANOMALY_DETECTED, actualBody.getAlertType());
        assertEquals("MB", actualBody.getMeasurementUnit());
        assertEquals(60L, actualBody.getDurationSeconds());
    }

    @Test
    void testPerformanceThresholdRecoveredEvent() {
        // Test a recovery from high CPU usage
        PerformanceAlert alertBody = PerformanceAlert.newBuilder()
            .setMetricName("cpu_utilization")
            .setCurrentValue(75.0)
            .setThresholdValue(90.0)
            .setAlertType(AlertType.THRESHOLD_RECOVERED)
            .setMeasurementUnit("percent")
            .setDurationSeconds(null)  // No duration needed for recovery events
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(alertBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof PerformanceAlert);
        
        PerformanceAlert actualBody = (PerformanceAlert) event.getBody();
        assertEquals("cpu_utilization", actualBody.getMetricName());
        assertEquals(75.0, actualBody.getCurrentValue(), 0.001);
        assertEquals(90.0, actualBody.getThresholdValue(), 0.001);
        assertEquals(AlertType.THRESHOLD_RECOVERED, actualBody.getAlertType());
        assertEquals("percent", actualBody.getMeasurementUnit());
        assertNull(actualBody.getDurationSeconds());
    }

    @Test
    void testSuccessfulDeploymentEvent() {
        // Test a successful deployment using blue-green strategy
        DeploymentEvent deployBody = DeploymentEvent.newBuilder()
            .setDeploymentId("deploy-" + UUID.randomUUID().toString())
            .setDeploymentStage(DeploymentStage.COMPLETED)
            .setFromVersion("1.2.0")
            .setToVersion("1.3.0")
            .setDeploymentStrategy(DeploymentStrategy.BLUE_GREEN)
            .setRollbackReason(null)
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(deployBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof DeploymentEvent);
        
        DeploymentEvent actualBody = (DeploymentEvent) event.getBody();
        assertTrue(actualBody.getDeploymentId().startsWith("deploy-"));
        assertEquals(DeploymentStage.COMPLETED, actualBody.getDeploymentStage());
        assertEquals("1.2.0", actualBody.getFromVersion());
        assertEquals("1.3.0", actualBody.getToVersion());
        assertEquals(DeploymentStrategy.BLUE_GREEN, actualBody.getDeploymentStrategy());
        assertNull(actualBody.getRollbackReason());
    }

    @Test
    void testFailedDeploymentWithRollback() {
        // Test a failed deployment that triggered a rollback
        DeploymentEvent deployBody = DeploymentEvent.newBuilder()
            .setDeploymentId("deploy-" + UUID.randomUUID().toString())
            .setDeploymentStage(DeploymentStage.ROLLED_BACK)
            .setFromVersion("2.0.0")
            .setToVersion("2.1.0")
            .setDeploymentStrategy(DeploymentStrategy.CANARY)
            .setRollbackReason("Health check failures in canary instances")
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(deployBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof DeploymentEvent);
        
        DeploymentEvent actualBody = (DeploymentEvent) event.getBody();
        assertTrue(actualBody.getDeploymentId().startsWith("deploy-"));
        assertEquals(DeploymentStage.ROLLED_BACK, actualBody.getDeploymentStage());
        assertEquals("2.0.0", actualBody.getFromVersion());
        assertEquals("2.1.0", actualBody.getToVersion());
        assertEquals(DeploymentStrategy.CANARY, actualBody.getDeploymentStrategy());
        assertEquals("Health check failures in canary instances", actualBody.getRollbackReason());
    }

    @Test
    void testInitialDeploymentEvent() {
        // Test initial deployment with no previous version
        DeploymentEvent deployBody = DeploymentEvent.newBuilder()
            .setDeploymentId("deploy-" + UUID.randomUUID().toString())
            .setDeploymentStage(DeploymentStage.DEPLOYING)
            .setFromVersion(null)  // No previous version for initial deployment
            .setToVersion("1.0.0")
            .setDeploymentStrategy(DeploymentStrategy.RECREATE)
            .setRollbackReason(null)
            .build();

        ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
            .setHeader(testHeader)
            .setBody(deployBody)
            .build();

        assertNotNull(event);
        assertEquals(testHeader, event.getHeader());
        assertTrue(event.getBody() instanceof DeploymentEvent);
        
        DeploymentEvent actualBody = (DeploymentEvent) event.getBody();
        assertTrue(actualBody.getDeploymentId().startsWith("deploy-"));
        assertEquals(DeploymentStage.DEPLOYING, actualBody.getDeploymentStage());
        assertNull(actualBody.getFromVersion());
        assertEquals("1.0.0", actualBody.getToVersion());
        assertEquals(DeploymentStrategy.RECREATE, actualBody.getDeploymentStrategy());
        assertNull(actualBody.getRollbackReason());
    }
}
