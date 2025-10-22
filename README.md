# MCP Server Java

A Model Context Protocol (MCP) server implementation in Java, demonstrating the core MCP features including tools, resources, and prompts.

## Features

This MCP server provides:

### Tools
- **add**: Add two numbers together
- **multiply**: Multiply two numbers
- **get_current_time**: Get the current server time

### Resources
- **server-info**: Information about the MCP server
- **documentation**: Server documentation and usage examples

### Prompts
- **math_helper**: Get help with mathematical calculations
- **current_time**: Get the current time

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Building the Server

Build the project using Maven:

```bash
mvn clean package
```

This will create an executable JAR file: `target/mcp-server-java-1.0.0.jar`

## Running the Server

### Standalone (STDIO)

Run the server directly:

```bash
java -jar target/mcp-server-java-1.0.0.jar
```

### With Claude Desktop

Add the server to your Claude Desktop configuration:

**macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`
**Windows**: `%APPDATA%\Claude\claude_desktop_config.json`

```json
{
  "mcpServers": {
    "mcp-server-java": {
      "command": "java",
      "args": [
        "-jar",
        "C:\\absolute\\path\\to\\target\\mcp-server-java-1.0.0.jar"
      ]
    }
  }
}
```

Replace the path with the absolute path to your JAR file.

### With VS Code

The server is already configured in `.vscode/mcp.json`. After building:

1. Open VS Code
2. Press `Cmd+Shift+P` (Mac) or `Ctrl+Shift+P` (Windows)
3. Select "MCP: Add server..."
4. The server will be available to connect

## Development

### Project Structure

```
mcp-server-java/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── mcp/
│                       ├── Main.java              # Entry point
│                       ├── ToolsProvider.java     # Tool implementations
│                       ├── ResourcesProvider.java # Resource implementations
│                       └── PromptsProvider.java   # Prompt implementations
├── .vscode/
│   └── mcp.json                  # VS Code MCP configuration
├── pom.xml                       # Maven configuration
└── README.md                     # This file
```

### Adding New Tools

To add a new tool:

1. Open `src/main/java/com/example/mcp/ToolsProvider.java`
2. Add the tool definition in `registerTools()`
3. Implement the tool logic in the switch statement

Example:

```java
// In the list tools handler
Tool myTool = new Tool();
myTool.setName("my_tool");
myTool.setDescription("Description of what my tool does");
// ... set input schema
tools.add(myTool);

// In the call tool handler
case "my_tool":
    // Your implementation here
    break;
```

### Adding New Resources

To add a new resource:

1. Open `src/main/java/com/example/mcp/ResourcesProvider.java`
2. Add the resource in `registerResources()`
3. Implement the read handler for your resource URI

### Testing with MCP Inspector

You can test your server using the MCP Inspector:

```bash
npx @modelcontextprotocol/inspector java -jar target/mcp-server-java-1.0.0.jar
```

## Debugging

The server uses SLF4J for logging. Logs are written to stderr and will appear in your terminal or the client's log viewer.

To enable verbose logging, you can adjust the logging level in your slf4j configuration.

## Learn More

- [Model Context Protocol Documentation](https://modelcontextprotocol.io)
- [MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk)
- [MCP Specification](https://spec.modelcontextprotocol.io)

## License

MIT License - See LICENSE file for details
