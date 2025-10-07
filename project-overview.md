# Security Events Project Overview

## Project Structure

### security-events
- **Technology**: Apache Avro for schema definition and serialization
- **Purpose**: Define strongly-typed event structures
- **Key Features**:
  - Schema evolution support
  - Binary serialization format
  - Language-agnostic definitions
  - Built-in validation

### event-spring-boot-starter
- **Framework**: Spring Boot 3.5.2
- **Auto-configuration**: META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
- **Dependencies**:
  ```xml
  <dependencies>
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter</artifactId>
      </dependency>
      <dependency>
          <groupId>com.tbw.security</groupId>
          <artifactId>security-events</artifactId>
          <version>1.0.0-SNAPSHOT</version>
      </dependency>
  </dependencies>
  ```

### spring-boot-application
- **Java Version**: 21
- **Framework**: Spring Boot 3.5.2
- **Purpose**: Integration demonstration
- **Features**:
  - Event listener integration
  - Configuration examples
  - Logging demonstration

## Core Events (security-events)

### Event Types Implemented:
1. Application Lifecycle Events
   - **Application Startup**
     ```java
     ApplicationStartup.newBuilder()
         .setStartupTimeMs(1500L)
         .setJvmVersion("21")
         .setMemoryAllocatedMb(1024L)
         .setConfigurationProfile("dev")
         .setEnabledFeatures(Arrays.asList("feature1"))
         .build();
     ```
   - **Application Shutdown**
     ```java
     ApplicationShutdown.newBuilder()
         .setShutdownReason(ShutdownReason.GRACEFUL)
         .setUptimeSeconds(3600L)
         .setFinalMemoryUsageMb(2048L)
         .build();
     ```
   - **Configuration Changes**
     ```java
     ConfigurationChange.newBuilder()
         .setConfigKey("database.pool.size")
         .setOldValue("10")
         .setNewValue("20")
         .setChangeSource(ConfigSource.ADMIN_INTERFACE)
         .build();
     ```

2. Security Events
   - **Authentication Failures**
     ```java
     SecurityEvent.newBuilder()
         .setEventType(SecurityEventType.AUTHENTICATION_FAILURE)
         .setResource("/api/v1/login")
         .setThreatLevel(ThreatLevel.MEDIUM)
         .setClientIp("192.168.1.100")
         .build();
     ```
   - **Suspicious Activity Detection**
   - **Certificate Management**
     - Expiration warnings
     - Renewal tracking
     - Validation status

3. Performance Events
   - **Threshold Monitoring**
     ```java
     PerformanceAlert.newBuilder()
         .setMetricName("response_time")
         .setCurrentValue(500.0)
         .setThresholdValue(200.0)
         .setAlertType(AlertType.THRESHOLD_EXCEEDED)
         .setMeasurementUnit("ms")
         .build();
     ```
   - **Anomaly Detection**
     - Statistical analysis
     - Pattern recognition
     - Baseline comparison
   - **Recovery Tracking**
     - Resolution time
     - Root cause analysis
     - Impact assessment

4. Deployment Events
   - **Deployment Strategies**
     ```java
     DeploymentEvent.newBuilder()
         .setDeploymentId(UUID.randomUUID().toString())
         .setDeploymentStage(DeploymentStage.COMPLETED)
         .setFromVersion("1.2.0")
         .setToVersion("1.3.0")
         .setDeploymentStrategy(DeploymentStrategy.BLUE_GREEN)
         .build();
     ```
   - **Rollback Handling**
     - Automatic detection
     - Failure analysis
     - Recovery procedures
   - **Deployment Metrics**
     - Duration tracking
     - Success rates
     - Impact analysis

### Event Header Structure
```java
EventHeader
├── eventId (UUID)
├── eventVersion
├── timestamp (Instant)
├── correlationId
├── applicationName
├── environment
└── severity
```

## Spring Boot Starter

### Key Components:

1. **Auto-Configuration Architecture**
   ```java
   @Configuration
   @EnableConfigurationProperties(EventProperties.class)
   @AutoConfigureAfter(LoggingAutoConfiguration.class)
   public class EventAutoConfiguration {
       private final EventProperties properties;

       public EventAutoConfiguration(EventProperties properties) {
           this.properties = properties;
       }

       @Bean
       @ConditionalOnMissingBean
       @ConditionalOnProperty(prefix = "application.events", 
                            name = "enabled", 
                            matchIfMissing = true)
       public ApplicationLifecycleEventListener applicationLifecycleEventListener(
           Environment environment) {
           return new ApplicationLifecycleEventListener(environment);
       }
   }
   ```

   **Key Features:**
   - Conditional bean creation
   - Property-based configuration
   - Environment awareness
   - Logging integration
   - Bean lifecycle management

2. **Auto-Configuration Discovery**
   ```plaintext
   META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
   └── com.example.event.configuration.EventAutoConfiguration
   ```

3. **Bean Conditions**
   - `@ConditionalOnMissingBean`: Allows custom override
   - `@ConditionalOnProperty`: Property-based activation
   - `@AutoConfigureAfter`: Ensures proper initialization order
   - `@EnableConfigurationProperties`: Property binding

2. **Properties Configuration**
```java
@ConfigurationProperties(prefix = "application.events")
public class EventProperties {
    private boolean enabled = true;
    // Getters/Setters
}
```

3. **Event Listener**
- Captures Spring Boot lifecycle events
- Translates to security event format
- Provides detailed system metrics

## Testing Coverage

### Unit Tests Architecture

1. **Event Creation Tests**
   ```java
   @Test
   void testApplicationStartupEvent() {
       ApplicationStartup startupBody = ApplicationStartup.newBuilder()
           .setStartupTimeMs(1500L)
           .setJvmVersion("21")
           .setMemoryAllocatedMb(1024L)
           .build();

       ApplicationLifecycleEvent event = ApplicationLifecycleEvent.newBuilder()
           .setHeader(testHeader)
           .setBody(startupBody)
           .build();

       assertNotNull(event);
       assertTrue(event.getBody() instanceof ApplicationStartup);
       assertEquals(1500L, ((ApplicationStartup)event.getBody()).getStartupTimeMs());
   }
   ```

2. **Auto-Configuration Tests**
   ```java
   class EventAutoConfigurationTests {
       private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
           .withConfiguration(AutoConfigurations.of(EventAutoConfiguration.class));

       @Test
       void whenNoPropertiesSet_thenEventListenerIsCreated() {
           contextRunner.withUserConfiguration(TestConfig.class)
               .run(context -> {
                   assertThat(context)
                       .hasSingleBean(ApplicationLifecycleEventListener.class);
                   assertThat(context)
                       .hasSingleBean(EventProperties.class);
               });
       }
   }
   ```

3. **Test Categories**
   - **Unit Tests**
     - Event creation and validation
     - Property binding
     - Listener functionality
   - **Integration Tests**
     - Auto-configuration
     - Property resolution
     - Event handling
   - **Edge Cases**
     - Null handling
     - Invalid configurations
     - Error scenarios

4. **Test Coverage Goals**
   - Line Coverage: >80%
   - Branch Coverage: >70%
   - Method Coverage: >90%

### Test Examples
```java
@Test
void testApplicationStartupEvent() {
    ApplicationStartup startupBody = ApplicationStartup.newBuilder()
        .setStartupTimeMs(1500L)
        .setJvmVersion("11.0.12")
        ...
        .build();
    // Assertions
}
```

## Integration Features

### Spring Boot Integration
1. **Auto-Configuration System**
   ```java
   @SpringBootApplication
   public class Application {
       public static void main(String[] args) {
           SpringApplication.run(Application.class, args);
       }
   }
   ```
   - No manual configuration needed
   - Property-driven behavior
   - Environment-aware setup

2. **Conditional Bean Creation**
   ```java
   @ConditionalOnProperty(prefix = "application.events", 
                         name = "enabled", 
                         matchIfMissing = true)
   @ConditionalOnMissingBean
   ```
   - Smart defaults
   - Override capability
   - Environment detection

### Event Handling Architecture

1. **Lifecycle Event Capture**
   ```java
   @EventListener
   public void handleApplicationStarting(ApplicationStartingEvent event) {
       ApplicationStartup startupBody = ApplicationStartup.newBuilder()
           .setStartupTimeMs(System.currentTimeMillis())
           .setJvmVersion(System.getProperty("java.version"))
           .setMemoryAllocatedMb(Runtime.getRuntime().totalMemory() 
               / (1024 * 1024))
           .build();
       // Event processing
   }
   ```

2. **Metrics Collection**
   - JVM metrics
   - Memory usage
   - System properties
   - Application state

3. **Structured Logging**
   ```java
   logger.info("Application Starting: {}", lifecycleEvent);
   ```
   - JSON formatting
   - Correlation IDs
   - Timestamp precision
   - Context enrichment

4. **Error Handling**
   - Exception capture
   - Error categorization
   - Fallback strategies
   - Recovery procedures

## Configuration Options

### Application Properties
```properties
# Enable/Disable event logging
application.events.enabled=true

# Application identification
spring.application.name=my-app
spring.application.version=1.0.0
```

## Future Enhancements

### Potential Additions

1. **Additional Event Types**
   ```java
   public class NetworkEvent {
       private String protocol;
       private int port;
       private NetworkStatus status;
       private Map<String, String> metrics;
   }
   ```
   - Network monitoring
   - User activity tracking
   - Resource utilization
   - Infrastructure events

2. **Enhanced Configuration**
   ```properties
   # Advanced Configuration
   application.events.filter.include-types=SECURITY,PERFORMANCE
   application.events.severity.threshold=WARNING
   application.events.retention.days=30
   application.events.batch.size=100
   ```
   - Granular filtering
   - Custom severity definitions
   - Retention policies
   - Batch processing

3. **Integration Expansions**
   ```java
   @Configuration
   @ConditionalOnClass(KafkaTemplate.class)
   public class EventStreamingConfiguration {
       @Bean
       public EventPublisher kafkaEventPublisher(
           KafkaTemplate<String, ApplicationLifecycleEvent> kafka) {
           return new KafkaEventPublisher(kafka);
       }
   }
   ```
   
   **Message Queue Integration**
   - Apache Kafka support
   - RabbitMQ integration
   - AWS SQS compatibility
   
   **Metrics Platform Integration**
   - Prometheus metrics
   - Grafana dashboards
   - ELK stack integration
   
   **Observability Features**
   - Distributed tracing
   - Performance monitoring
   - Alert correlation

## Questions?

Contact: [Your Contact Information]
Repository: security (Owner: tbweiss)