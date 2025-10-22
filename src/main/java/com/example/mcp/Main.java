package com.example.mcp;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.json.McpJsonMapper;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the MCP Server.
 * Supports both STDIO and HTTP/SSE transports.
 * 
 * Usage:
 *   java -jar mcp-server-java.jar           # Runs with STDIO transport
 *   java -jar mcp-server-java.jar --http    # Runs with HTTP transport on port 8080
 *   java -jar mcp-server-java.jar --http --port 3000  # Runs with HTTP on port 3000
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int DEFAULT_HTTP_PORT = 8080;

    public static void main(String[] args) {
        try {
            boolean useHttp = false;
            int port = DEFAULT_HTTP_PORT;
            
            // Parse command line arguments
            for (int i = 0; i < args.length; i++) {
                if ("--http".equals(args[i])) {
                    useHttp = true;
                } else if ("--port".equals(args[i]) && i + 1 < args.length) {
                    port = Integer.parseInt(args[i + 1]);
                    i++;
                }
            }

            if (useHttp) {
                startHttpServer(port);
            } else {
                startStdioServer();
            }

        } catch (Exception e) {
            logger.error("Failed to start MCP Server", e);
            System.exit(1);
        }
    }

    private static void startStdioServer() throws InterruptedException {
        logger.info("Starting MCP Server with STDIO transport...");

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
                        ToolsProvider.getGreetingTool()
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

            logger.info("MCP Server started successfully with STDIO transport");

            // The server runs indefinitely (until process is terminated)
            // Keep the main thread alive
            Thread.currentThread().join();
    }

    private static void startHttpServer(int port) throws Exception {
        logger.info("Starting MCP Server with HTTP/SSE transport on port {}...", port);

        // Create HTTP/SSE transport provider
        HttpServletSseServerTransportProvider transportProvider = 
            HttpServletSseServerTransportProvider.builder()
                .messageEndpoint("/message")
                .sseEndpoint("/sse")
                .build();

        // Build and configure the server
        McpSyncServer mcpServer = McpServer.sync(transportProvider)
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
                    ToolsProvider.getGreetingTool()
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

        // Create and start Jetty server
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Register the MCP servlet
        ServletHolder servletHolder = new ServletHolder(transportProvider);
        context.addServlet(servletHolder, "/*");

        server.start();
        
        logger.info("MCP Server started successfully on http://localhost:{}", port);
        logger.info("SSE endpoint: http://localhost:{}/sse", port);
        logger.info("Message endpoint: http://localhost:{}/message", port);
        logger.info("Health check: http://localhost:{}/health", port);

        // Keep server running
        server.join();
    }
}
