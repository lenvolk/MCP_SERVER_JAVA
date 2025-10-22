package com.example.mcp;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Provides prompt implementations for the MCP server.
 */
public class PromptsProvider {
    private static final Logger logger = LoggerFactory.getLogger(PromptsProvider.class);

    public static McpServerFeatures.SyncPromptSpecification getMathHelperPrompt() {
        McpSchema.Prompt prompt = new McpSchema.Prompt(
            "math_helper",
            "Mathematical Operation Helper",
            List.of(new McpSchema.PromptArgument("operation", "The mathematical operation to perform", true))
        );

        return new McpServerFeatures.SyncPromptSpecification(prompt, (exchange, request) -> {
            logger.info("Prompt 'math_helper' requested");
            Map<String, Object> arguments = request.arguments();
            String operation = (String) arguments.getOrDefault("operation", "add");
            String content = String.format(
                "I need help with the %s operation. Please guide me through using the appropriate tool.",
                operation
            );
            return new McpSchema.GetPromptResult(
                "Mathematical Operation Helper",
                List.of(new McpSchema.PromptMessage(
                    McpSchema.Role.USER,
                    new McpSchema.TextContent(content)
                ))
            );
        });
    }

    public static McpServerFeatures.SyncPromptSpecification getCurrentTimePrompt() {
        McpSchema.Prompt prompt = new McpSchema.Prompt(
            "current_time",
            "Current Time Information",
            List.of()
        );

        return new McpServerFeatures.SyncPromptSpecification(prompt, (exchange, request) -> {
            logger.info("Prompt 'current_time' requested");
            String content = "What is the current time? Please use the get_current_time tool to find out.";
            return new McpSchema.GetPromptResult(
                "Current Time Information",
                List.of(new McpSchema.PromptMessage(
                    McpSchema.Role.USER,
                    new McpSchema.TextContent(content)
                ))
            );
        });
    }
}
