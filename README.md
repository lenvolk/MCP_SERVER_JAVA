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

Run the server directly:

🚀 Usage
## Start HTTP Server:
java -jar target/mcp-server-java-1.0.0.jar --http


```bash
java -jar target/mcp-server-java-1.0.0.jar 
```

## Run Java Client:
Working Solution
Step 1: Start the HTTP Server (in one terminal)
java -jar target/mcp-server-java-1.0.0.jar --http  (need http server to be running first)

Step 2: Run the Java Client (in another terminal)
mvn exec:java -Dexec.mainClass=com.example.mcp.client.McpJavaClient


## Testing out the tools :

<br>HTTP JSON API Server started successfully!
<br>API Endpoints:
 <br> http://localhost:8080/tools         - List available tools
<br>  http://localhost:8080/tools/add     - Add two numbers
<br>  http://localhost:8080/tools/multiply - Multiply two numbers
<br>  http://localhost:8080/tools/time    - Get current time
<br>  http://localhost:8080/tools/greet   - Greet by name

<br><br><br>
# List all tools
curl -X GET http://localhost:8080/tools

# Add two numbers
curl -X POST http://localhost:8080/tools/add -H "Content-Type: application/json" -d '{"a":"7","b":"51"}'

# Multiply two numbers  
curl -X POST http://localhost:8080/tools/multiply -H "Content-Type: application/json" -d '{"x":"7","y":"70"}'

# Get current time
curl -X GET http://localhost:8080/tools/time

# Greet someone
curl -X POST http://localhost:8080/tools/greet -H "Content-Type: application/json" -d '{"name":"Steve"}'

🚀 Usage
## Start HTTP Server:
java -jar target/mcp-server-java-1.0.0.jar --http

## Start STDIO Server:
java -jar target/mcp-server-java-1.0.0.jar

## nxp command to test over web page
npx @modelcontextprotocol/inspector java -jar target/mcp-server-java-1.0.0.jar


## git hub integration
<br>in command prompt in git directory:
<br>git status
<br>On branch main
<br>nothing to commit, working tree clean
<br>PS C:\Users\stethompson\Microsoft\MCP_Server_Java> git init
<br>Reinitialized existing Git repository in C:/Users/stethompson/Microsoft/MCP_Server_Java/.git/
<br>PS C:\Users\stethompson\Microsoft\MCP_Server_Java> git add .
<br>PS C:\Users\stethompson\Microsoft\MCP_Server_Java> git remote add origin https://github.com/SteveThompson_msftcae/MCP_SERVER_JAVA.git
<br>PS C:\Users\stethompson\Microsoft\MCP_Server_Java> git branch -M main
<br>PS C:\Users\stethompson\Microsoft\MCP_Server_Java> git push -u origin main
<br>info: please complete authentication in your browser...
<br>fatal: The request is not supported
<br>Username for 'https://github.com':
(authenticated with code)
<br>PS C:\Users\stethompson\Microsoft\MCP_Server_Java> git push -u origin main
<br>Enumerating objects: 48, done.
<br>Counting objects: 100% (48/48), done.
<br>Delta compression using up to 8 threads
<br>Compressing objects: 100% (31/31), done.
<br>Writing objects: 100% (48/48), 23.38 KiB | 1.80 MiB/s, done.
<br>Total 48 (delta 8), reused 0 (delta 0), pack-reused 0 (from 0)
<br>remote: Resolving deltas: 100% (8/8), done.
<br>To https://github.com/SteveThompson_msftcae/MCP_SERVER_JAVA.git
<br> * [new branch]      main -> main
<br>branch 'main' set up to track 'origin/main'.





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
