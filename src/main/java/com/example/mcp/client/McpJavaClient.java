package com.example.mcp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Simple Java client for connecting to the MCP Server HTTP JSON API.
 * Demonstrates how to call all available tools using standard Java HTTP client.
 * 
 * Usage:
 *   First start the server in HTTP mode:
 *     java -jar target/mcp-server-java-1.0.0.jar --http
 *   
 *   Then run this client:
 *     mvn exec:java -Dexec.mainClass="com.example.mcp.client.McpJavaClient"
 */
public class McpJavaClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public McpJavaClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    /**
     * List all available tools
     */
    public JsonNode listTools() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tools"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body());
    }

    /**
     * Call the add tool to add two numbers
     */
    public JsonNode add(double a, double b) throws IOException, InterruptedException {
        ObjectNode params = mapper.createObjectNode();
        params.put("a", a);
        params.put("b", b);

        return callTool("/tools/add", params);
    }

    /**
     * Call the multiply tool to multiply two numbers
     */
    public JsonNode multiply(double x, double y) throws IOException, InterruptedException {
        ObjectNode params = mapper.createObjectNode();
        params.put("x", x);
        params.put("y", y);

        return callTool("/tools/multiply", params);
    }

    /**
     * Call the get_current_time tool
     */
    public JsonNode getCurrentTime() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tools/time"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body());
    }

    /**
     * Call the greet tool to greet someone by name
     */
    public JsonNode greet(String name) throws IOException, InterruptedException {
        ObjectNode params = mapper.createObjectNode();
        params.put("name", name);

        return callTool("/tools/greet", params);
    }

    /**
     * Helper method to call a tool with JSON parameters
     */
    private JsonNode callTool(String endpoint, ObjectNode params) throws IOException, InterruptedException {
        String jsonBody = mapper.writeValueAsString(params);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body());
    }

    /**
     * Main method to demonstrate all tool calls
     */
    public static void main(String[] args) {
        try {
            // Default to localhost:8080, or use first argument as base URL
            String baseUrl = args.length > 0 ? args[0] : "http://localhost:8080";
            
            System.out.println("=== MCP Java Client Demo ===");
            System.out.println("Connecting to: " + baseUrl);
            System.out.println();

            McpJavaClient client = new McpJavaClient(baseUrl);

            // List all available tools
            System.out.println("1. Listing all tools:");
            JsonNode tools = client.listTools();
            System.out.println(tools.toPrettyString());
            System.out.println();

            // Test add tool
            System.out.println("2. Testing add(5, 3):");
            JsonNode addResult = client.add(5, 3);
            System.out.println(addResult.toPrettyString());
            System.out.println();

            // Test multiply tool
            System.out.println("3. Testing multiply(4, 7):");
            JsonNode multiplyResult = client.multiply(4, 7);
            System.out.println(multiplyResult.toPrettyString());
            System.out.println();

            // Test get current time tool
            System.out.println("4. Testing getCurrentTime():");
            JsonNode timeResult = client.getCurrentTime();
            System.out.println(timeResult.toPrettyString());
            System.out.println();

            // Test greet tool
            System.out.println("5. Testing greet(\"Alice\"):");
            JsonNode greetResult = client.greet("Alice");
            System.out.println(greetResult.toPrettyString());
            System.out.println();

            System.out.println("=== All tests completed successfully! ===");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
