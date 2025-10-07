# Security Patterns

## Security Event Handling

### 1. Threat Level Assessment
```java
public enum ThreatLevel {
    LOW, MEDIUM, HIGH, CRITICAL;

    public static ThreatLevel assess(SecurityEvent event) {
        if (event.isAuthenticationFailure() && event.getFailureCount() > 5) {
            return CRITICAL;
        }
        // Additional assessment logic
        return MEDIUM;
    }
}
```

### 2. Data Sanitization
```java
public class SecuritySanitizer {
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[<>]", "")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&apos;");
    }
}
```

### 3. Audit Trail
```java
@Aspect
@Component
public class SecurityAuditAspect {
    @Around("@annotation(SecurityAudit)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) {
        SecurityEvent event = createAuditEvent(joinPoint);
        try {
            Object result = joinPoint.proceed();
            event.setSuccess(true);
            return result;
        } catch (Throwable t) {
            event.setSuccess(false);
            event.setError(t);
            throw t;
        } finally {
            auditLogger.log(event);
        }
    }
}
```

## Security Implementation Examples

### 1. Authentication Event
```java
SecurityEvent authEvent = SecurityEvent.builder()
    .header(createSecurityHeader())
    .type(SecurityEventType.AUTHENTICATION)
    .details(new AuthenticationDetails(username, success))
    .threatLevel(ThreatLevel.assess(event))
    .build();
```

### 2. Access Control
```java
@PreAuthorize("hasRole('ADMIN')")
@SecurityAudit
public void performSecurityOperation() {
    // Implementation
}
```

### 3. Encryption
```java
public class SecurityEncryption {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    
    public static String encrypt(String value, SecretKey key) {
        // Encryption implementation
    }
    
    public static String decrypt(String encrypted, SecretKey key) {
        // Decryption implementation
    }
}
```

## Security Testing Patterns

### 1. Authentication Test
```java
@Test
void whenInvalidCredentials_thenAuthenticationFails() {
    // Given
    String username = "testUser";
    String invalidPassword = "invalid";
    
    // When
    SecurityEvent event = authenticate(username, invalidPassword);
    
    // Then
    assertEquals(SecurityEventType.AUTHENTICATION_FAILURE, event.getType());
    assertEquals(ThreatLevel.MEDIUM, event.getThreatLevel());
}
```

### 2. Authorization Test
```java
@Test
void whenUnauthorized_thenAccessDenied() {
    // Given
    User user = createRegularUser();
    
    // When/Then
    assertThrows(AccessDeniedException.class, 
        () -> securityService.performAdminOperation(user));
}
```

## Compliance Requirements

### 1. Logging Requirements
- All security events must be logged
- Logs must include timestamp and user
- Sensitive data must be masked
- Retention policy must be followed

### 2. Audit Requirements
- All admin actions must be audited
- Audit trail must be immutable
- Audit records must be searchable
- Retention period must be configurable