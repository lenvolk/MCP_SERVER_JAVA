# MCP Server Java - GitHub Copilot Instructions

This project is a Model Context Protocol (MCP) server implementation in Java.

## Project Context

This is an MCP server that demonstrates the core features of the Model Context Protocol:
- **Tools**: Functions that can be called by AI models (add, multiply, get_current_time)
- **Resources**: Data sources that provide context (server info, documentation)
- **Prompts**: Pre-defined templates for common tasks

## Key Technologies

- **Java 17+**: Modern Java with LTS support
- **Maven**: Build and dependency management
- **MCP Java SDK**: Official SDK from `io.modelcontextprotocol.sdk`
- **Jackson**: JSON processing
- **SLF4J**: Logging framework

## Code Style Guidelines

1. Use clear, descriptive variable and method names
2. Add JavaDoc comments for public classes and methods
3. Follow Java naming conventions (camelCase for methods, PascalCase for classes)
4. Use proper exception handling with meaningful error messages
5. Log important operations using SLF4J logger

## MCP-Specific Guidelines

### Adding Tools
- Define tool schema with proper JSON Schema types
- Validate input parameters before processing
- Return results as `ToolContent` (typically `TextContent`)
- Handle errors gracefully and return error messages to the client

### Adding Resources
- Use URI format: `resource://resource-name`
- Provide clear names and descriptions
- Support appropriate MIME types (text/plain, text/markdown, application/json)
- Return content as `ResourceContents` objects

### Adding Prompts
- Define clear, user-friendly prompt names
- Document required and optional arguments
- Return structured `PromptMessage` objects
- Include helpful context in prompt content

## Testing

- Build with: `mvn clean package`
- Run with: `java -jar target/mcp-server-java-1.0.0.jar`
- Test with MCP Inspector: `npx @modelcontextprotocol/inspector java -jar target/mcp-server-java-1.0.0.jar`

## Documentation References

- [MCP Specification](https://spec.modelcontextprotocol.io)
- [MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk)
- [MCP Documentation](https://modelcontextprotocol.io)
