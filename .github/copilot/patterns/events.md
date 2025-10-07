# Event Patterns

## Event Structure
All events in this project follow these patterns:

### 1. Base Event Structure
```java
public class BaseEvent {
    private final EventHeader header;
    private final Object body;

    protected BaseEvent(EventHeader header, Object body) {
        this.header = validateHeader(header);
        this.body = validateBody(body);
    }

    // Getters, no setters for immutability
}
```

### 2. Builder Pattern
```java
public static class Builder<T extends Builder<T>> {
    protected EventHeader header;
    protected Object body;

    @SuppressWarnings("unchecked")
    public T header(EventHeader header) {
        this.header = header;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T body(Object body) {
        this.body = body;
        return (T) this;
    }
}
```

### 3. Validation Pattern
```java
protected void validate() {
    validateHeader();
    validateBody();
    validateTypeSpecific();
}

private void validateHeader() {
    Objects.requireNonNull(header, "Header must not be null");
    Objects.requireNonNull(header.getEventId(), "Event ID must not be null");
    Objects.requireNonNull(header.getTimestamp(), "Timestamp must not be null");
}
```

## Event Testing Pattern
```java
@Test
void testEventCreation() {
    // Given
    EventHeader header = createTestHeader();
    EventBody body = createTestBody();

    // When
    Event event = Event.builder()
        .header(header)
        .body(body)
        .build();

    // Then
    assertNotNull(event);
    assertEquals(header, event.getHeader());
    assertEquals(body, event.getBody());
}
```

## Event Usage Examples

### 1. Security Event
```java
SecurityEvent event = SecurityEvent.builder()
    .header(createStandardHeader())
    .eventType(SecurityEventType.AUTHENTICATION_FAILURE)
    .resource("/api/login")
    .threatLevel(ThreatLevel.HIGH)
    .build();
```

### 2. Application Event
```java
ApplicationLifecycleEvent event = ApplicationLifecycleEvent.builder()
    .header(createStandardHeader())
    .eventType(ApplicationEventType.STARTUP)
    .startupTimeMs(System.currentTimeMillis())
    .build();
```