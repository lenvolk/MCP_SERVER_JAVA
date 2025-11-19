package com.example.mcp;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

/**
 * Main entry point for the MCP Server.
 * Supports both STDIO (for MCP Inspector/Claude) and HTTP JSON API (for Java clients).
 * 
 * Usage:
 *   java -jar mcp-server-java.jar              # STDIO mode (default)
 *   java -jar mcp-server-java.jar --http       # HTTP JSON API mode on port 8080
 *   java -jar mcp-server-java.jar --http --port 3000  # Custom port
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Check if HTTP mode is requested
            boolean httpMode = false;
            int port = 8080;
            
            for (int i = 0; i < args.length; i++) {
                if ("--http".equals(args[i])) {
                    httpMode = true;
                } else if ("--port".equals(args[i]) && i + 1 < args.length) {
                    port = Integer.parseInt(args[i + 1]);
                    i++;
                }
            }
            
            if (httpMode) {
                startHttpServer(port);
            } else {
                startStdioServer();
            }

        } catch (Exception e) {
            System.err.println("Failed to start MCP Server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void startStdioServer() throws InterruptedException {
        System.err.println("Starting MCP Server with STDIO transport...");

        // Create stdio transport provider
        StdioServerTransportProvider transportProvider = 
            new StdioServerTransportProvider(McpJsonMapper.getDefault());

        // Build and configure the server with tools, resources, and prompts
        McpSyncServer server = McpServer.sync(transportProvider)
                .serverInfo("mcp-server-java", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                    .tools(true)
                    .resources(true, false)
                    .prompts(true)
                    .build())
                .tools(
                    ToolsProvider.getAddTool(),
                    ToolsProvider.getMultiplyTool(),
                    ToolsProvider.getCurrentTimeTool(),
                    ToolsProvider.getGreetingTool(),
                    ToolsProvider.getAIChatTool()
                )
                .resources(
                    ResourcesProvider.getServerInfoResource(),
                    ResourcesProvider.getDocumentationResource()
                )
                .prompts(
                    PromptsProvider.getMathHelperPrompt(),
                    PromptsProvider.getCurrentTimePrompt()
                )
                .build();

        System.err.println("MCP Server started successfully and ready to accept requests");

        // Keep the main thread alive - server runs indefinitely
        Thread.currentThread().join();
    }
    
    private static void startHttpServer(int port) throws Exception {
        System.out.println("Starting HTTP JSON API Server on port " + port + "...");
        
        HttpJsonServer httpServer = new HttpJsonServer(port);
        httpServer.start();
        
        System.out.println("HTTP JSON API Server started successfully!");
        System.out.println("API Endpoints:");
        System.out.println("  http://localhost:" + port + "/tools         - List available tools");
        System.out.println("  http://localhost:" + port + "/tools/add     - Add two numbers");
        System.out.println("  http://localhost:" + port + "/tools/multiply - Multiply two numbers");
        System.out.println("  http://localhost:" + port + "/tools/time    - Get current time");
        System.out.println("  http://localhost:" + port + "/tools/greet   - Greet by name");
        System.out.println("  http://localhost:" + port + "/tools/ai_chat - Chat with AI agent");
        System.out.println("\nPress Ctrl+C to stop the server");
        
        // Keep server running
        Thread.currentThread().join();
    }
}
