# Security Standards Prompt File

## Purpose
This file provides security-specific standards and guidelines for AI coding assistants (like Copilot) in this repository.

## Security Standards

### 1. Sensitive Data
- Never log or expose sensitive information (passwords, secrets, API keys, personal data).
- Use environment variables for secrets and configuration, not hardcoded values.

### 2. Input Validation
- Always validate and sanitize user input to prevent injection attacks (SQL, command, etc.).
- Use parameterized queries for all database access.

### 3. Authentication & Authorization
- Require authentication for all sensitive operations.
- Check user permissions before performing actions.

### 4. Dependency Management
- Use only trusted dependencies from official sources.
- Keep dependencies up to date to avoid known vulnerabilities.
- For frameworks suchas Spring Boot, use the latest GA release.

### 5. Secure Communication
- Use HTTPS/TLS for all network communication.
- Never transmit sensitive data over unencrypted channels.

### 6. Error Handling
- Do not expose stack traces or internal error details to end users.
- Log errors securely for internal review only.

## Usage
- Reference this file when writing prompts or reviewing code for security compliance.
- Update as new security standards or threats emerge.
