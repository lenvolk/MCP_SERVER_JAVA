# Azure AI Agent Integration Setup

## Overview

The MCP Server Java now includes an AI Agent tool that connects to Azure AI Foundry (Azure OpenAI) to provide AI-powered responses.

## AI Agent Chat Request Flow
User Request
    ↓
HTTP Endpoint (/tools/ai_chat) or MCP Tool (ai_chat)
    ↓
ToolsProvider.getAIChatTool() - extracts prompt, max_tokens, temperature
    ↓
AzureAIClient.getInstance() - singleton ensures one connection
    ↓
AzureAIClient.chat(prompt, maxTokens, temperature)
    ↓
client.getChatCompletions(deploymentName, options)
    ↓
Azure OpenAI Endpoint: https://demo-ai-agent-project-resource.openai.azure.com
    ↓
Deployment Model: gpt-4o-java-agent-ai
    ↓
Response returned through the chain back to user


## Configuration

### Azure OpenAI Endpoint
- **Endpoint**: `https://demo-ai-agent-project-resource.openai.azure.com`
- **Deployment**: `gpt-4o-java-agent-ai`
- **Model**: GPT-4o

### Environment Variables

**Required:**
```bash
export AZURE_OPENAI_API_KEY="your-api-key-here"
```

**Optional (overrides defaults):**
```bash
export AZURE_OPENAI_ENDPOINT="https://demo-ai-agent-project-resource.openai.azure.com"
export AZURE_OPENAI_DEPLOYMENT="gpt-4o-java-agent-ai"
```

### Windows PowerShell
```powershell
$env:AZURE_OPENAI_API_KEY="your-api-key-here"
```

### Windows Command Prompt
```cmd
set AZURE_OPENAI_API_KEY=your-api-key-here
```

## Building the Project

After adding the Azure dependencies, rebuild the project:

```bash
mvn clean package
```

## Usage

### STDIO Mode (MCP Inspector / Claude Desktop)

```json
{
  "jsonrpc": "2.0",
  "method": "tools/call",
  "params": {
    "name": "ai_chat",
    "arguments": {
      "prompt": "Explain what Model Context Protocol is",
      "max_tokens": 500,
      "temperature": 0.7
    }
  }
}
```

### HTTP Mode

Start the server:
```bash
java -jar target/mcp-server-java-1.0.0.jar --http
```

Call the AI chat endpoint:
```bash
curl -X POST http://localhost:8080/tools/ai_chat \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "What is Azure AI Foundry?",
    "max_tokens": 300,
    "temperature": 0.7
  }'
```

Response format:
```json
{
  "tool": "ai_chat",
  "prompt": "What is Azure AI Foundry?",
  "response": "Azure AI Foundry is Microsoft's unified platform..."
}
```

## Tool Parameters

### `ai_chat` Tool

**Parameters:**
- `prompt` (string, required): The user prompt to send to the AI agent
- `max_tokens` (number, optional): Maximum tokens in response (default: 500)
- `temperature` (number, optional): Temperature for response generation 0.0-1.0 (default: 0.7)

**Example with defaults:**
```bash
curl -X POST http://localhost:8080/tools/ai_chat \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Hello, what can you help me with?"}'
```

## Testing

### 1. Test with curl (HTTP mode)
```bash
# Start server
java -jar target/mcp-server-java-1.0.0.jar --http

# In another terminal
curl -X POST http://localhost:8080/tools/ai_chat \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Tell me a joke about Java programming"}'
```

### 2. Test with MCP Inspector
```bash
npx @modelcontextprotocol/inspector java -jar target/mcp-server-java-1.0.0.jar
```

Then use the inspector UI to call the `ai_chat` tool.

### 3. Test with Claude Desktop

Add to your Claude Desktop config (`%APPDATA%\Claude\claude_desktop_config.json` on Windows):

```json
{
  "mcpServers": {
    "mcp-server-java": {
      "command": "java",
      "args": [
        "-jar",
        "C:\\path\\to\\target\\mcp-server-java-1.0.0.jar"
      ],
      "env": {
        "AZURE_OPENAI_API_KEY": "your-api-key-here"
      }
    }
  }
}
```

## Troubleshooting

### Error: "AZURE_OPENAI_API_KEY environment variable is not set"

**Solution**: Set the environment variable before running the server:
```bash
export AZURE_OPENAI_API_KEY="your-key"
java -jar target/mcp-server-java-1.0.0.jar --http
```

### Error: "Failed to get AI response"

**Possible causes:**
1. Invalid API key
2. Network connectivity issues
3. Azure OpenAI service is down
4. Deployment name doesn't exist

**Solution**: Check the logs for detailed error messages and verify your Azure OpenAI configuration.

### Rate Limiting

If you encounter rate limiting errors, consider:
1. Reducing the number of requests
2. Implementing retry logic with exponential backoff
3. Upgrading your Azure OpenAI quota

## Dependencies

The following dependencies were added to `pom.xml`:

```xml
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-ai-openai</artifactId>
    <version>1.0.0-beta.10</version>
</dependency>
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-core</artifactId>
    <version>1.49.0</version>
</dependency>
```

## Security Best Practices

1. **Never commit API keys** to version control
2. Use environment variables or Azure Key Vault for sensitive data
3. Rotate API keys regularly
4. Use managed identities when running in Azure
5. Restrict API key permissions to minimum required

## Next Steps

Consider adding:
- Token usage tracking and logging
- Response caching to reduce API calls
- Streaming responses for longer completions
- Support for system messages and conversation history
- Integration with Azure AI Content Safety
- Multi-model support (GPT-3.5, GPT-4, etc.)

## References

- [Azure OpenAI Service Documentation](https://learn.microsoft.com/en-us/azure/ai-services/openai/)
- [Azure AI SDK for Java](https://learn.microsoft.com/en-us/java/api/overview/azure/ai-openai-readme)
- [Model Context Protocol Specification](https://spec.modelcontextprotocol.io/)
