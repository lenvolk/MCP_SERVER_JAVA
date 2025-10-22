# MCP Server Java - HTTP/SSE Transport Guide

## Overview

The MCP Server now supports **two transport modes**:

1. **STDIO Transport** (default) - For MCP Inspector and command-line tools
2. **HTTP/SSE Transport** - For web-based clients and URL health checks

## Running the Server

### STDIO Mode (Default)
```bash
java -jar target/mcp-server-java-1.0.0.jar
```

### HTTP Mode
```bash
java -jar target/mcp-server-java-1.0.0.jar --http
```

This starts the server on **http://localhost:8080**

###Custom Port
```bash
java -jar target/mcp-server-java-1.0.0.jar --http --port 3000
```

## HTTP Endpoints

When running in HTTP mode, the following endpoints are available:

### SSE Endpoint (Server-to-Client Events)
```
GET http://localhost:8080/sse
```
Establishes a Server-Sent Events connection for receiving messages from the server.

### Message Endpoint (Client-to-Server Messages)
```
POST http://localhost:8080/message?sessionId=<session-id>
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "method": "tools/list",
  "id": 1
}
```
Sends JSON-RPC messages to the server.

### Health Check (Coming Soon)
```
GET http://localhost:8080/health
```

## Testing HTTP Mode

### Using curl

1. **Start SSE connection** (in one terminal):
```bash
curl -N -H "Accept: text/event-stream" http://localhost:8080/sse
```

You'll receive an `endpoint` event with the message URL.

2. **Send a request** (in another terminal):
```bash
curl -X POST "http://localhost:8080/message?sessionId=<session-id>" \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tools/list",
    "id": 1
  }'
```

### Using MCP Inspector with HTTP

```bash
npx @modelcontextprotocol/inspector http://localhost:8080
```

## Building

After making changes, rebuild with:
```bash
mvn clean package
```

**Note**: Stop any running server instances before rebuilding!

## Architecture

The HTTP transport uses:
- **Jetty 12** - Embedded HTTP server
- **Server-Sent Events (SSE)** - For server-to-client streaming
- **HTTP POST** - For client-to-server messages
- **Session Management** - Each SSE connection gets a unique session ID

## Advantages of HTTP Mode

✅ **URL-based health checks** - Monitor server with HTTP GET requests  
✅ **Web browser access** - Connect from any HTTP client  
✅ **Load balancer compatibility** - Works behind reverse proxies  
✅ **Firewall friendly** - Uses standard HTTP ports  
✅ **Debug friendly** - Inspect traffic with browser dev tools  

## Troubleshooting

### Port Already in Use
```
Error: Address already in use
```
**Solution**: Use a different port with `--port 3001` or stop the conflicting process.

### Build Fails "Failed to delete JAR"
**Solution**: Stop all running instances of the server before building.

### SSE Connection Drops
**Solution**: Some proxies/firewalls may timeout SSE connections. Configure keep-alive or use STDIO mode.

## Next Steps

- Add `/health` endpoint for monitoring
- Add CORS support for browser clients
- Add authentication/authorization
- Add metrics endpoint
- Add WebSocket transport option
