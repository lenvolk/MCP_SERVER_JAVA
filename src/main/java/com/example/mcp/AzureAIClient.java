package com.example.mcp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Azure AI Foundry client for connecting to Azure OpenAI services.
 * Uses singleton pattern to maintain a single connection.
 */
public class AzureAIClient {
    private static final Logger logger = LoggerFactory.getLogger(AzureAIClient.class);
    
    private static AzureAIClient instance;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String endpoint;
    private final String deploymentName;
    private final String apiKey;
    
    // Azure OpenAI configuration
    private static final String ENDPOINT = "https://demo-ai-agent-project-resource.openai.azure.com";
    private static final String DEPLOYMENT = "gpt-4o-mini";
    private static final String API_VERSION = "2024-12-01-preview";
    private String apiVersion;
    
    private AzureAIClient() {
        logger.info("Initializing Azure AI Client...");
        
        // Get API key from environment variable
        this.apiKey = System.getenv("AZURE_OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException(
                "AZURE_OPENAI_API_KEY environment variable is not set. " +
                "Please set it with: $env:AZURE_OPENAI_API_KEY=\"your-api-key\""
            );
        }
        
        // Allow override of endpoint and deployment from environment variables
        String envEndpoint = System.getenv("AZURE_OPENAI_ENDPOINT");
        this.endpoint = (envEndpoint != null && !envEndpoint.isEmpty()) ? envEndpoint : ENDPOINT;
        
        String envDeployment = System.getenv("AZURE_OPENAI_DEPLOYMENT");
        this.deploymentName = (envDeployment != null && !envDeployment.isEmpty()) ? envDeployment : DEPLOYMENT;
        
        String envApiVersion = System.getenv("AZURE_OPENAI_API_VERSION");
        this.apiVersion = (envApiVersion != null && !envApiVersion.isEmpty()) ? envApiVersion : API_VERSION;
        
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
        
        this.mapper = new ObjectMapper();
        
        logger.info("Azure AI Client initialized successfully");
        logger.info("Endpoint: {}", endpoint);
        logger.info("Deployment: {}", deploymentName);
    }
    
    /**
     * Get singleton instance of AzureAIClient
     */
    public static synchronized AzureAIClient getInstance() {
        if (instance == null) {
            instance = new AzureAIClient();
        }
        return instance;
    }
    
    /**
     * Send a chat prompt to Azure AI Foundry and get response
     * 
     * @param prompt The user prompt to send
     * @param maxTokens Maximum tokens in response
     * @param temperature Temperature for response generation (0.0-1.0)
     * @return AI response text
     */
    public String chat(String prompt, Integer maxTokens, Double temperature) {
        try {
            logger.info("Sending chat request to Azure OpenAI");
            logger.info("Using deployment: {}", deploymentName);
            logger.info("Prompt: {}", prompt);
            logger.info("Max Tokens: {}, Temperature: {}", maxTokens, temperature);
            
            // Build request URL
            String url = String.format("%s/openai/deployments/%s/chat/completions?api-version=%s",
                endpoint, deploymentName, apiVersion);
            
            logger.info("Request URL: {}", url);
            
            // Build JSON request body
            ObjectNode requestBody = mapper.createObjectNode();
            ArrayNode messages = mapper.createArrayNode();
            ObjectNode message = mapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            
            requestBody.set("messages", messages);
            requestBody.put("max_completion_tokens", maxTokens);
            // o4-mini only supports temperature=1, so only set if it's 1.0
            if (temperature == 1.0) {
                requestBody.put("temperature", temperature);
            }
            
            String jsonBody = mapper.writeValueAsString(requestBody);
            logger.info("Request body: {}", jsonBody);
            
            // Build HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("api-key", apiKey)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
            
            logger.info("Sending HTTP POST request...");
            
            // Send request and get response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            logger.info("Received HTTP response with status: {}", response.statusCode());
            logger.info("Response body: {}", response.body());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Azure OpenAI API returned status " + response.statusCode() + 
                    ": " + response.body());
            }
            
            // Parse response JSON
            JsonNode responseJson = mapper.readTree(response.body());
            String content = responseJson
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
            
            logger.info("Received response from Azure OpenAI");
            logger.info("Response length: {} characters", content.length());
            
            return content;
            
        } catch (IOException | InterruptedException e) {
            logger.error("ERROR in AzureAIClient.chat()", e);
            throw new RuntimeException("Failed to get AI response: " + e.getMessage(), e);
        }
    }
    
    /**
     * Send a chat prompt with default parameters
     */
    public String chat(String prompt) {
        return chat(prompt, 500, 1.0);
    }
}
