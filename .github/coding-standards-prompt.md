# Example Prompt File for AI Coding Assistant

## Purpose
This file provides example prompts and instructions to guide AI coding assistants (like Copilot) in this repository.

## Prompts

### 1. Code Style
- Always use camelCase for variable and method names.
- Use 4 spaces for indentation.
- Add Javadoc comments to all public methods.

### 2. Dependency Management
- Use Maven for all Java dependencies.
- Target Java 21 for all Java code.

### 3. Security
- Refer to the security-standards-prompt.md file.

### 4. Testing
- Write unit tests for all new features using JUnit 5.
- Mock external dependencies in tests.

### 5. Documentation
- Update the README.md with any new features or breaking changes.
- Clearly comment all code that is specific to adherance to standards as defined in security-standards-prompt.md.


### 5. Project Structure
- For Java Projects
    - Use standard Maven Java Archetype for new projects. 
    - Ensure that the project is Git enabled.
    - Always include a .gitignore file that excludes
        - Log files
        - Build target directories
        - .vscode files
        - Do not exclude generated code
    - Maven Artifact Coordinate Naming Conventions
        - For groupId, use the organization internet domain name (reversed) followed by the current workspace name.
        - For artifactId, use the specified project name retaining any hyphens if present.
    - Java Package Naming Conventions
        - For package name, use the organization internet domain name (reversed) followed by the current workspace name followed by the current project name. For example, for an organization internet domain name of tbw.com and a workspace called myWorkspace and a project called my-project, the package name would be com.tbw.myworkspace.myproject.
    - Avro Namespace Naming Conventions
        - Use the same naming conventions as for Java packages above.


## Usage
- Reference this file when writing prompts for the AI assistant.
- Add new examples as project conventions evolve.
