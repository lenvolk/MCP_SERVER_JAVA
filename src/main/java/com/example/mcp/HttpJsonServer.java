package com.example.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple HTTP JSON API Server for MCP tools.
 * Uses Java's built-in HttpServer and Jackson for JSON handling.
 * Provides REST endpoints for all MCP tools.
 */
public class HttpJsonServer {
    private final int port;
    private final ObjectMapper mapper;
    private HttpServer server;

    public HttpJsonServer(int port) {
        this.port = port;
        this.mapper = new ObjectMapper();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Register endpoints
        server.createContext("/tools", this::handleListTools);
        server.createContext("/tools/add", this::handleAdd);
        server.createContext("/tools/multiply", this::handleMultiply);
        server.createContext("/tools/time", this::handleGetTime);
        server.createContext("/tools/greet", this::handleGreet);

        server.setExecutor(null); // Use default executor
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void handleListTools(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed. Use GET.");
            return;
        }

        ObjectNode response = mapper.createObjectNode();
        response.put("server", "mcp-server-java");
        response.put("version", "1.0.0");
        
        var toolsArray = response.putArray("tools");
        toolsArray.addObject()
            .put("name", "add")
            .put("description", "Add two numbers")
            .put("endpoint", "/tools/add")
            .put("method", "POST")
            .put("parameters", "{\"a\": number, \"b\": number}");
        
        toolsArray.addObject()
            .put("name", "multiply")
            .put("description", "Multiply two numbers")
            .put("endpoint", "/tools/multiply")
            .put("method", "POST")
            .put("parameters", "{\"x\": number, \"y\": number}");
        
        toolsArray.addObject()
            .put("name", "get_current_time")
            .put("description", "Get the current date and time")
            .put("endpoint", "/tools/time")
            .put("method", "GET");
        
        toolsArray.addObject()
            .put("name", "greet")
            .put("description", "Greet a person by name")
            .put("endpoint", "/tools/greet")
            .put("method", "POST")
            .put("parameters", "{\"name\": string}");

        sendJsonResponse(exchange, 200, response);
    }

    private void handleAdd(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed. Use POST.");
            return;
        }

        try {
            Map<String, Object> params = parseJsonBody(exchange);
            
            if (!params.containsKey("a") || !params.containsKey("b")) {
                sendError(exchange, 400, "Missing required parameters: a and b");
                return;
            }

            double a = getNumberParam(params, "a");
            double b = getNumberParam(params, "b");
            double result = a + b;

            ObjectNode response = mapper.createObjectNode();
            response.put("tool", "add");
            response.put("result", result);
            response.put("message", "The result is: " + result);

            sendJsonResponse(exchange, 200, response);

        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleMultiply(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed. Use POST.");
            return;
        }

        try {
            Map<String, Object> params = parseJsonBody(exchange);
            
            if (!params.containsKey("x") || !params.containsKey("y")) {
                sendError(exchange, 400, "Missing required parameters: x and y");
                return;
            }

            double x = getNumberParam(params, "x");
            double y = getNumberParam(params, "y");
            double result = x * y;

            ObjectNode response = mapper.createObjectNode();
            response.put("tool", "multiply");
            response.put("result", result);
            response.put("message", "The result is: " + result);

            sendJsonResponse(exchange, 200, response);

        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleGetTime(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed. Use GET.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        ObjectNode response = mapper.createObjectNode();
        response.put("tool", "get_current_time");
        response.put("time", formattedTime);
        response.put("message", "Current time: " + formattedTime);

        sendJsonResponse(exchange, 200, response);
    }

    private void handleGreet(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed. Use POST.");
            return;
        }

        try {
            Map<String, Object> params = parseJsonBody(exchange);
            
            if (!params.containsKey("name")) {
                sendError(exchange, 400, "Missing required parameter: name");
                return;
            }

            String name = params.get("name").toString();
            String greeting = "Hello " + name + " welcome to the Java MCP Server!";

            ObjectNode response = mapper.createObjectNode();
            response.put("tool", "greet");
            response.put("message", greeting);

            sendJsonResponse(exchange, 200, response);

        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private Map<String, Object> parseJsonBody(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = mapper.readValue(body, HashMap.class);
        return map;
    }

    private double getNumberParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, ObjectNode json) throws IOException {
        String response = mapper.writeValueAsString(json);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        ObjectNode error = mapper.createObjectNode();
        error.put("error", message);
        error.put("status", statusCode);
        sendJsonResponse(exchange, statusCode, error);
    }
}
