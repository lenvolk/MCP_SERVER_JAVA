# Test HTTP/SSE Transport for MCP Server
Write-Host "=== Testing MCP Server HTTP Transport ===" -ForegroundColor Cyan

# Test 1: Check if server is running
Write-Host "`n[1] Testing basic connectivity..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/sse" -Method Head -TimeoutSec 2 -ErrorAction Stop
    Write-Host "✓ Server is responding on port 8080" -ForegroundColor Green
} catch {
    Write-Host "✗ Server connectivity test (expected to fail for SSE HEAD request)" -ForegroundColor Gray
}

# Test 2: Test SSE endpoint (should return event stream)
Write-Host "`n[2] Testing SSE endpoint..." -ForegroundColor Yellow
Write-Host "Starting SSE connection (will run for 3 seconds)..." -ForegroundColor Gray

$job = Start-Job -ScriptBlock {
    try {
        $request = [System.Net.HttpWebRequest]::Create("http://localhost:8080/sse")
        $request.Accept = "text/event-stream"
        $request.Method = "GET"
        $request.Timeout = 3000
        
        $response = $request.GetResponse()
        $stream = $response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        
        $output = ""
        while ($reader.Peek() -ge 0) {
            $line = $reader.ReadLine()
            $output += $line + "`n"
            if ($output.Length -gt 1000) { break }
        }
        
        $reader.Close()
        return $output
    } catch {
        return "Error: $_"
    }
}

Wait-Job $job -Timeout 3 | Out-Null
$sseOutput = Receive-Job $job
Remove-Job $job -Force

if ($sseOutput -match "event:|data:") {
    Write-Host "✓ SSE endpoint is working!" -ForegroundColor Green
    Write-Host "  Received SSE events:" -ForegroundColor Gray
    $sseOutput -split "`n" | Select-Object -First 5 | ForEach-Object {
        Write-Host "    $_" -ForegroundColor DarkGray
    }
} else {
    Write-Host "✗ SSE endpoint response:" -ForegroundColor Yellow
    Write-Host "  $sseOutput" -ForegroundColor DarkGray
}

# Test 3: Test message endpoint (should accept POST)
Write-Host "`n[3] Testing message endpoint..." -ForegroundColor Yellow
Write-Host "(Note: This will fail without a valid sessionId, which is expected)" -ForegroundColor Gray

try {
    $body = @{
        jsonrpc = "2.0"
        method = "tools/list"
        id = 1
    } | ConvertTo-Json

    $response = Invoke-WebRequest `
        -Uri "http://localhost:8080/message?sessionId=test-session" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body `
        -TimeoutSec 2
    
    Write-Host "✓ Message endpoint accepted POST request" -ForegroundColor Green
    Write-Host "  Status: $($response.StatusCode)" -ForegroundColor Gray
    Write-Host "  Response: $($response.Content.Substring(0, [Math]::Min(200, $response.Content.Length)))..." -ForegroundColor DarkGray
} catch {
    Write-Host "✗ Message endpoint response: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host "`n=== Test Summary ===" -ForegroundColor Cyan
Write-Host "Server is running at: http://localhost:8080" -ForegroundColor White
Write-Host "SSE Endpoint: http://localhost:8080/sse" -ForegroundColor White
Write-Host "Message Endpoint: http://localhost:8080/message" -ForegroundColor White
Write-Host "`nTo test with MCP Inspector, run:" -ForegroundColor Cyan
Write-Host '  npx @modelcontextprotocol/inspector http://localhost:8080' -ForegroundColor White
