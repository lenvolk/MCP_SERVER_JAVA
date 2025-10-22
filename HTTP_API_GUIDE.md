# MCP Server Java - HTTP JSON API Guide

This MCP server now supports **two modes**:

1. **STDIO Mode** - For MCP Inspector, Claude Desktop, and other MCP clients
2. **HTTP JSON API Mode** - For Java clients and simple HTTP requests

---

## 🚀 Quick Start

### STDIO Mode (Default - for MCP Inspector/Claude)

```bash
java -jar target/mcp-server-java-1.0.0.jar
```

### HTTP JSON API Mode (for Java clients)

```bash
java -jar target/mcp-server-java-1.0.0.jar --http
```

Or with a custom port:

```bash
java -jar target/mcp-server-java-1.0.0.jar --http --port 3000
```

---

## 📋 HTTP JSON API Endpoints

When running in HTTP mode, the server exposes the following REST endpoints:

### List All Tools
```bash
GET http://localhost:8080/tools
```

**Response:**
```json
{
  "server": "mcp-server-java",
  "version": "1.0.0",
  "tools": [
    {
      "name": "add",
      "description": "Add two numbers",
      "endpoint": "/tools/add",
      "method": "POST",
      "parameters": "{\"a\": number, \"b\": number}"
    },
    ...
  ]
}
```

### Add Two Numbers
```bash
POST http://localhost:8080/tools/add
Content-Type: application/json

{
  "a": 5,
  "b": 3
}
```

**Response:**
```json
{
  "tool": "add",
  "result": 8.0,
  "message": "The result is: 8.0"
}
```

### Multiply Two Numbers
```bash
POST http://localhost:8080/tools/multiply
Content-Type: application/json

{
  "x": 4,
  "y": 7
}
```

**Response:**
```json
{
  "tool": "multiply",
  "result": 28.0,
  "message": "The result is: 28.0"
}
```

### Get Current Time
```bash
GET http://localhost:8080/tools/time
```

**Response:**
```json
{
  "tool": "get_current_time",
  "time": "2025-10-22T14:30:45.123",
  "message": "Current time: 2025-10-22T14:30:45.123"
}
```

### Greet by Name
```bash
POST http://localhost:8080/tools/greet
Content-Type: application/json

{
  "name": "Alice"
}
```

**Response:**
```json
{
  "tool": "greet",
  "message": "Hello Alice welcome to the Java MCP Server!"
}
```

---

## ☕ Java Client Usage

### Using the Provided Java Client

1. **Start the server in HTTP mode:**
   ```bash
   java -jar target/mcp-server-java-1.0.0.jar --http
   ```

2. **Run the Java client:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.mcp.client.McpJavaClient"
   ```

### Using the Client in Your Own Java Code

```java
import com.example.mcp.client.McpJavaClient;
import com.fasterxml.jackson.databind.JsonNode;

public class MyApp {
    public static void main(String[] args) throws Exception {
        McpJavaClient client = new McpJavaClient("http://localhost:8080");
        
        // List available tools
        JsonNode tools = client.listTools();
        System.out.println(tools.toPrettyString());
        
        // Call the add tool
        JsonNode result = client.add(10, 5);
        System.out.println("Result: " + result.get("result").asDouble());
        
        // Call the multiply tool
        JsonNode product = client.multiply(6, 7);
        System.out.println("Product: " + product.get("result").asDouble());
        
        // Get current time
        JsonNode time = client.getCurrentTime();
        System.out.println("Time: " + time.get("time").asText());
        
        // Greet someone
        JsonNode greeting = client.greet("Bob");
        System.out.println("Greeting: " + greeting.get("message").asText());
    }
}
```

---

## 🧪 Testing with cURL

### List Tools
```bash
curl http://localhost:8080/tools
```

### Add Numbers
```bash
curl -X POST http://localhost:8080/tools/add \
  -H "Content-Type: application/json" \
  -d "{\"a\": 15, \"b\": 25}"
```

### Multiply Numbers
```bash
curl -X POST http://localhost:8080/tools/multiply \
  -H "Content-Type: application/json" \
  -d "{\"x\": 8, \"y\": 9}"
```

### Get Current Time
```bash
curl http://localhost:8080/tools/time
```

### Greet
```bash
curl -X POST http://localhost:8080/tools/greet \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Charlie\"}"
```

---

## 🛠️ Testing with MCP Inspector (STDIO Mode)

```bash
npx @modelcontextprotocol/inspector java -jar target/mcp-server-java-1.0.0.jar
```

Then open the browser URL shown in the output.

---

## 🔧 Integration with Claude Desktop

Add to `%APPDATA%\Claude\claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "mcp-server-java": {
      "command": "java",
      "args": [
        "-jar",
        "C:\\path\\to\\target\\mcp-server-java-1.0.0.jar"
      ]
    }
  }
}
```

---

## 📦 Building from Source

```bash
mvn clean package
```

The executable JAR will be created at `target/mcp-server-java-1.0.0.jar`.

---

## 🎯 Key Features

- ✅ **Dual Mode Operation**: STDIO for MCP clients, HTTP for Java/REST clients
- ✅ **Jackson JSON Processing**: Robust JSON serialization/deserialization
- ✅ **Simple HTTP API**: Uses Java's built-in HttpServer (no external dependencies)
- ✅ **CORS Enabled**: Access-Control-Allow-Origin header included
- ✅ **Type-Safe**: Proper parameter validation and error handling
- ✅ **Easy Integration**: Simple REST API for any HTTP client

---

## 🐛 Troubleshooting

### Port Already in Use
If port 8080 is already in use, specify a different port:
```bash
java -jar target/mcp-server-java-1.0.0.jar --http --port 9090
```

### Connection Refused
Make sure the server is running in HTTP mode before connecting:
```bash
# Start server first
java -jar target/mcp-server-java-1.0.0.jar --http

# Then in another terminal, run the client
mvn exec:java -Dexec.mainClass="com.example.mcp.client.McpJavaClient"
```

### JSON Parsing Errors
Ensure your JSON payload is properly formatted:
```json
{
  "a": 5,
  "b": 3
}
```

---

## 📄 License

This project demonstrates MCP server implementation with dual transport support (STDIO and HTTP).
