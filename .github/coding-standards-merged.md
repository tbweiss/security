# Coding Standards and AI Assistant Guidelines

## Purpose
This document serves as a comprehensive guide for both developers and AI coding assistants (like Copilot) working in this repository.

## Code Style and General Conventions

### Java Code Style
- Use camelCase for variable and method names
- Use 4 spaces for indentation
- Add Javadoc comments to all public methods

### Development Environment
- Use Maven for all Java dependencies
- Target Java 21 for all Java code
- Ensure all projects are Git enabled

## Project Structure and Naming Conventions

### Maven Project Names
- Project names in the filesystem should use kebab-case (hyphenated)
  - Example: `security-events`, `event-spring-boot-starter`

### Java Package Names
1. Base Package Structure
   - Organization prefix: `com.tbw`
   - Product/workspace name: `security`
   - Project/module name: Convert from kebab-case to lowercase without hyphens
   
2. Package Naming Pattern
   ```
   com.tbw.<product>.<project>
   ```
   
3. Examples:
   - For project `security-events`:
     - Filesystem name: `security-events`
     - Package name: `com.tbw.security.securityevents`
   - For project `event-spring-boot-starter`:
     - Filesystem name: `event-spring-boot-starter`
     - Package name: `com.tbw.security.event.starter`

### Maven Artifact Naming
1. Group ID
   - Format: `com.tbw.security`

2. Artifact ID
   - Use the kebab-case project name
   - Example: `security-events`

3. Version
   - Follow semantic versioning (MAJOR.MINOR.PATCH)
   - Example: `0.0.1-SNAPSHOT`

### Avro Schema Conventions
1. Schema File Names
   - Use snake_case for .avsc files
   - Example: `application_lifecycle_event.avsc`

2. Avro Namespace Convention
   - Should match the Java package naming convention
   - Example: `com.tbw.security.securityevents`

3. Record Names
   - Use PascalCase (UpperCamelCase)
   - Example: `ApplicationLifecycleEvent`

### Generated Code Management
1. Source Tree Integration
   - Generated code MUST be committed to the source tree
   - Generated files should be placed in `src/main/java` following the package structure
   - Example: Avro generated classes should be in `src/main/java/com/tbw/security/securityevents/`

2. Build Configuration
   - Configure code generators to output directly to `src/main/java`
   - Example Avro plugin configuration:
     ```xml
     <configuration>
         <sourceDirectory>${project.basedir}/src/main/avro/</sourceDirectory>
         <outputDirectory>${project.basedir}/src/main/java/</outputDirectory>
     </configuration>
     ```

3. Version Control
   - Generated code MUST be committed to version control
   - Changes to generated code should be reviewed as part of the PR process
   - Generated code changes should be committed separately from manual code changes
   - Include a proper .gitignore file that excludes:
     - Log files
     - Build target directories
     - .vscode files
     - But does NOT exclude generated code

4. Documentation
   - Add a comment at the top of generated files indicating their source
   - Example: `// Generated from application_lifecycle_event.avsc schema`

## Testing Standards
- Write unit tests for all new features using JUnit 5
- Mock external dependencies in tests
- Place tests in corresponding test directories following the same package structure

## Documentation Requirements
- Update the README.md with any new features or breaking changes
- Clearly comment all code that is specific to adherence to security standards
- Reference security-standards-prompt.md for specific security requirements

## Example Configurations

### pom.xml
```xml
<groupId>com.tbw.security</groupId>
<artifactId>security-events</artifactId>
<version>0.0.1-SNAPSHOT</version>
```

### Avro Schema
```json
{
  "type": "record",
  "name": "ApplicationLifecycleEvent",
  "namespace": "com.tbw.security.events",
  "doc": "Schema for auditing application lifecycle events"
}
```

## Usage Guidelines
- Use this document as the primary reference for coding standards
- Refer to security-standards-prompt.md for specific security requirements
- Add new examples and update conventions as the project evolves
