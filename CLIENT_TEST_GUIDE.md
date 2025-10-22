# MCP Client Test Guide

Since the MCP Java SDK 0.14.1 is server-only and doesn't include a client library, here are the recommended ways to test your MCP server:

## Option 1: Use MCP Inspector (Recommended)

The MCP Inspector is the official tool for testing MCP servers:

```bash
npx @modelcontextprotocol/inspector java -jar target/mcp-server-java-1.0.0.jar
```

This will:
1. Start your MCP server
2. Open a web interface where you can:
   - See all available tools, resources, and prompts
   - Test each tool with custom inputs
   - View responses in real-time
   - Inspect JSON-RPC messages

## Option 2: Use Claude Desktop

Add the server to your Claude Desktop configuration:

**Windows**: `%APPDATA%\Claude\claude_desktop_config.json`

```json
{
  "mcpServers": {
    "mcp-server-java": {
      "command": "java",
      "args": ["-jar", "C:\\path\\to\\target\\mcp-server-java-1.0.0.jar"]
    }
  }
}
```

Then use Claude to interact with your tools naturally.

## Option 3: Manual Testing with JSON-RPC

You can manually test by sending JSON-RPC messages via STDIO:

### Start the server:
```bash
java -jar target/mcp-server-java-1.0.0.jar
```

### Send JSON-RPC requests (example with PowerShell):

```powershell
# Initialize
'{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","clientInfo":{"name":"test","version":"1.0.0"},"capabilities":{}}}' | java -jar target/mcp-server-java-1.0.0.jar

# List tools
'{"jsonrpc":"2.0","id":2,"method":"tools/list"}' | java -jar target/mcp-server-java-1.0.0.jar

# Call add tool
'{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"add","arguments":{"a":15,"b":27}}}' | java -jar target/mcp-server-java-1.0.0.jar
```

## Available Tools to Test

Your server provides these tools:

1. **add** - Add two numbers
   ```json
   {"name":"add","arguments":{"a":15,"b":27}}
   ```

2. **multiply** - Multiply two numbers
   ```json
   {"name":"multiply","arguments":{"x":6,"y":7}}
   ```

3. **get_current_time** - Get current server time
   ```json
   {"name":"get_current_time","arguments":{}}
   ```

4. **greet** - Greet someone by name
   ```json
   {"name":"greet","arguments":{"name":"Alice"}}
   ```

## Expected Results

### Add Tool
Input: `{"a": 15, "b": 27}`
Output: `"The result is: 42.0"`

### Multiply Tool
Input: `{"x": 6, "y": 7}`
Output: `"The result is: 42.0"`

### Get Current Time
Input: `{}`
Output: `"Current time: 2025-10-22T13:45:30.123"`

### Greet Tool
Input: `{"name": "Alice"}`
Output: `"Hello Alice welcome to the Java MCP Server!"`

## Why No Java Client?

The MCP Java SDK 0.14.1 is designed for **server implementation only**. Most MCP clients are implemented in:
- **JavaScript/TypeScript** - Official MCP SDK has full client support
- **Python** - Community implementations available
- **CLI tools** - Like the MCP Inspector

If you need programmatic testing, consider:
1. Using the MCP Inspector (JavaScript-based)
2. Writing tests in Node.js with the official MCP TypeScript SDK
3. Creating integration tests that launch the server process and verify behavior
