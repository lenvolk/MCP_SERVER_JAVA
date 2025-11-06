# Deploy MCP Server Java to Azure Virtual Desktop
# This script should be run ON the AVD session host

param(
    [Parameter(Mandatory=$false)]
    [string]$InstallPath = "C:\Apps\MCP_Server_Java",
    
    [Parameter(Mandatory=$false)]
    [string]$DownloadUrl = "https://github.com/SE-RND/MCP_SERVER_JAVA/releases/download/v1.0.0/mcp-server-java-1.0.0.jar",
    
    [Parameter(Mandatory=$false)]
    [switch]$InstallAsService = $false
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "MCP Server Java - AVD Deployment Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check for Java installation
Write-Host "[1/5] Checking Java installation..." -ForegroundColor Yellow
$javaVersion = java -version 2>&1 | Select-Object -First 1
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Java is not installed!" -ForegroundColor Red
    Write-Host "Please install Java 17+ from: https://adoptium.net/" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Java found: $javaVersion" -ForegroundColor Green
Write-Host ""

# Create installation directory
Write-Host "[2/5] Creating installation directory..." -ForegroundColor Yellow
if (!(Test-Path $InstallPath)) {
    New-Item -ItemType Directory -Path $InstallPath -Force | Out-Null
    Write-Host "✅ Created: $InstallPath" -ForegroundColor Green
} else {
    Write-Host "✅ Directory exists: $InstallPath" -ForegroundColor Green
}
Write-Host ""

# Download JAR file
Write-Host "[3/5] Downloading JAR file..." -ForegroundColor Yellow
$jarPath = Join-Path $InstallPath "mcp-server-java-1.0.0.jar"

# Check if downloading from GitHub or copying locally
if ($DownloadUrl -like "http*") {
    try {
        Invoke-WebRequest -Uri $DownloadUrl -OutFile $jarPath -ErrorAction Stop
        Write-Host "✅ Downloaded from: $DownloadUrl" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed to download from URL" -ForegroundColor Red
        Write-Host "Error: $_" -ForegroundColor Red
        Write-Host "" -ForegroundColor Yellow
        Write-Host "Alternative: Copy JAR file manually to: $jarPath" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host "⚠️  No valid download URL provided" -ForegroundColor Yellow
    Write-Host "Please copy mcp-server-java-1.0.0.jar to: $jarPath" -ForegroundColor Yellow
}
Write-Host ""

# Create startup script
Write-Host "[4/5] Creating startup script..." -ForegroundColor Yellow
$startupScript = @"
@echo off
REM MCP Server Java - Startup Script
echo Starting MCP Server Java...
cd /d "$InstallPath"
java -jar mcp-server-java-1.0.0.jar --http --port 8080
pause
"@
$startupScript | Out-File -FilePath (Join-Path $InstallPath "start-mcp-server.bat") -Encoding ASCII
Write-Host "✅ Created startup script: start-mcp-server.bat" -ForegroundColor Green
Write-Host ""

# Optional: Install as Windows Service
if ($InstallAsService) {
    Write-Host "[5/5] Installing as Windows Service..." -ForegroundColor Yellow
    Write-Host "⚠️  This requires NSSM (Non-Sucking Service Manager)" -ForegroundColor Yellow
    Write-Host "Download from: https://nssm.cc/download" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "After downloading NSSM, run:" -ForegroundColor Yellow
    Write-Host "  nssm install MCPServerJava `"java`" `"-jar $jarPath --http --port 8080`"" -ForegroundColor Cyan
    Write-Host "  nssm set MCPServerJava AppDirectory `"$InstallPath`"" -ForegroundColor Cyan
    Write-Host "  nssm start MCPServerJava" -ForegroundColor Cyan
} else {
    Write-Host "[5/5] Service installation skipped (use -InstallAsService to enable)" -ForegroundColor Yellow
}
Write-Host ""

# Create desktop shortcut
$WshShell = New-Object -ComObject WScript.Shell
$Shortcut = $WshShell.CreateShortcut("$env:USERPROFILE\Desktop\MCP Server.lnk")
$Shortcut.TargetPath = Join-Path $InstallPath "start-mcp-server.bat"
$Shortcut.WorkingDirectory = $InstallPath
$Shortcut.Description = "MCP Server Java"
$Shortcut.Save()
Write-Host "✅ Created desktop shortcut" -ForegroundColor Green
Write-Host ""

# Final instructions
Write-Host "========================================" -ForegroundColor Green
Write-Host "✅ Installation Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "📍 Installation Location: $InstallPath" -ForegroundColor Cyan
Write-Host ""
Write-Host "🚀 To start the server:" -ForegroundColor Yellow
Write-Host "   Option 1: Double-click the desktop shortcut 'MCP Server'" -ForegroundColor White
Write-Host "   Option 2: Run: $InstallPath\start-mcp-server.bat" -ForegroundColor White
Write-Host "   Option 3: java -jar `"$jarPath`" --http" -ForegroundColor White
Write-Host ""
Write-Host "🌐 Access the server at:" -ForegroundColor Yellow
Write-Host "   http://localhost:8080/tools" -ForegroundColor Cyan
Write-Host ""
Write-Host "📚 API Endpoints:" -ForegroundColor Yellow
Write-Host "   GET  /tools         - List all tools" -ForegroundColor White
Write-Host "   POST /tools/add     - Add two numbers" -ForegroundColor White
Write-Host "   POST /tools/multiply - Multiply two numbers" -ForegroundColor White
Write-Host "   GET  /tools/time    - Get current time" -ForegroundColor White
Write-Host "   POST /tools/greet   - Greet by name" -ForegroundColor White
Write-Host ""
