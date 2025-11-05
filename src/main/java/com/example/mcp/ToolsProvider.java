package com.example.mcp;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.json.McpJsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Provides tool implementations for the MCP server.
 */
public class ToolsProvider {
    private static final Logger logger = LoggerFactory.getLogger(ToolsProvider.class);

    public static McpServerFeatures.SyncToolSpecification getAddTool() {
        McpJsonMapper mapper = McpJsonMapper.getDefault();
        
        String schemaJson = """
                {
                    "type": "object",
                    "properties": {
                        "a": {
                            "type": "number",
                            "description": "First number"
                        },
                        "b": {
                            "type": "number",
                            "description": "Second number"
                        }
                    },
                    "required": ["a", "b"]
                }
                """;

        McpSchema.Tool tool = McpSchema.Tool.builder()
            .name("add")
            .description("Add two numbers")
            .inputSchema(mapper, schemaJson)
            .build();

        return new McpServerFeatures.SyncToolSpecification(tool, null, (exchange, request) -> {
            logger.info("Tool 'add' called");
            Map<String, Object> arguments = request.arguments();
            double aVal = ((Number) arguments.get("a")).doubleValue();
            double bVal = ((Number) arguments.get("b")).doubleValue();
            double result = aVal + bVal;
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("The result is: " + result)),
                false
            );
        });
    }

    public static McpServerFeatures.SyncToolSpecification getMultiplyTool() {
        McpJsonMapper mapper = McpJsonMapper.getDefault();
        
        String schemaJson = """
                {
                    "type": "object",
                    "properties": {
                        "x": {
                            "type": "number",
                            "description": "First number"
                        },
                        "y": {
                            "type": "number",
                            "description": "Second number"
                        }
                    },
                    "required": ["x", "y"]
                }
                """;

        McpSchema.Tool tool = McpSchema.Tool.builder()
            .name("multiply")
            .description("Multiply two numbers")
            .inputSchema(mapper, schemaJson)
            .build();

        return new McpServerFeatures.SyncToolSpecification(tool, null, (exchange, request) -> {
            logger.info("Tool 'multiply' called");
            Map<String, Object> arguments = request.arguments();
            double xVal = ((Number) arguments.get("x")).doubleValue();
            double yVal = ((Number) arguments.get("y")).doubleValue();
            double result = xVal * yVal;
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("The result is: " + result)),
                false
            );
        });
    }

    public static McpServerFeatures.SyncToolSpecification getCurrentTimeTool() {
        McpJsonMapper mapper = McpJsonMapper.getDefault();
        
        String schemaJson = """
                {
                    "type": "object",
                    "properties": {}
                }
                """;

        McpSchema.Tool tool = McpSchema.Tool.builder()
            .name("get_current_time")
            .description("Get the current time")
            .inputSchema(mapper, schemaJson)
            .build();

        return new McpServerFeatures.SyncToolSpecification(tool, null, (exchange, request) -> {
            logger.info("Tool 'get_current_time' called");
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Current time: " + currentTime)),
                false
            );
        });
    }

    public static McpServerFeatures.SyncToolSpecification getGreetingTool() {
        McpJsonMapper mapper = McpJsonMapper.getDefault();
        
        String schemaJson = """
                {
                    "type": "object",
                    "properties": {
                        "name": {
                            "type": "string",
                            "description": "The name of the person to greet"
                        }
                    },
                    "required": ["name"]
                }
                """;

        McpSchema.Tool tool = McpSchema.Tool.builder()
            .name("greet")
            .description("Greet a person by name with a welcome message")
            .inputSchema(mapper, schemaJson)
            .build();

        return new McpServerFeatures.SyncToolSpecification(tool, null, (exchange, request) -> {
            logger.info("Tool 'greet' called");
            Map<String, Object> arguments = request.arguments();
            String name = (String) arguments.get("name");
            String greeting = "Hello " + name + " welcome to the Java MCP Server!";
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(greeting)),
                false
            );
        });
    }
}
