package com.example.mcp;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Provides resource implementations for the MCP server.
 */
public class ResourcesProvider {
    private static final Logger logger = LoggerFactory.getLogger(ResourcesProvider.class);

    public static McpServerFeatures.SyncResourceSpecification getServerInfoResource() {
        McpSchema.Resource resource = new McpSchema.Resource(
            "resource://server-info",
            "Server Information",
            "Information about this MCP server",
            "text/plain",
            null
        );

        return new McpServerFeatures.SyncResourceSpecification(resource, (exchange, request) -> {
            logger.info("Resource 'server-info' requested");
            String info = """
                MCP Server Java Implementation
                Version: 1.0.0
                Features:
                - Tools: Mathematical operations
                - Resources: Server information and documentation
                - Prompts: Helper prompts for common tasks
                """;
            return new McpSchema.ReadResourceResult(
                List.of(new McpSchema.TextResourceContents(
                    "resource://server-info",
                    "text/plain",
                    info
                ))
            );
        });
    }

    public static McpServerFeatures.SyncResourceSpecification getDocumentationResource() {
        McpSchema.Resource resource = new McpSchema.Resource(
            "resource://documentation",
            "Documentation",
            "MCP Server documentation",
            "text/markdown",
            null
        );

        return new McpServerFeatures.SyncResourceSpecification(resource, (exchange, request) -> {
            logger.info("Resource 'documentation' requested");
            String docs = """
                # MCP Server Documentation
                
                ## Available Tools
                - **add**: Add two numbers
                - **multiply**: Multiply two numbers
                - **get_current_time**: Get the current server time
                
                ## Available Resources
                - **server-info**: Information about this server
                - **documentation**: This documentation
                
                ## Available Prompts
                - **math_helper**: Helper for mathematical operations
                - **current_time**: Get information about current time
                """;
            return new McpSchema.ReadResourceResult(
                List.of(new McpSchema.TextResourceContents(
                    "resource://documentation",
                    "text/markdown",
                    docs
                ))
            );
        });
    }
}
