# Deploying MCP Server Java to Azure Virtual Desktop

This guide explains how to deploy the MCP Server Java application to Azure Virtual Desktop (AVD) using VS Code tools.

## Prerequisites

- Azure Virtual Desktop environment set up
- VS Code installed locally
- Access to AVD session host (RDP or SSH)
- Java 17+ installed on AVD VMs

## Deployment Methods

### Method 1: VS Code Remote-SSH (Recommended)

**Best for:** Direct deployment and development on AVD

**Steps:**

1. **Install VS Code Extensions:**
   - Remote - SSH (`ms-vscode-remote.remote-ssh`)
   - Azure Virtual Machines (`ms-azuretools.vscode-azurevirtualmachines`)

2. **Enable SSH on AVD Session Host:**
   ```powershell
   # Run on AVD VM as Administrator
   Add-WindowsCapability -Online -Name OpenSSH.Server~~~~0.0.1.0
   Start-Service sshd
   Set-Service -Name sshd -StartupType 'Automatic'
   
   # Configure firewall
   New-NetFirewallRule -Name sshd -DisplayName 'OpenSSH Server (sshd)' -Enabled True -Direction Inbound -Protocol TCP -Action Allow -LocalPort 22
   ```

3. **Connect from VS Code:**
   - Press `Ctrl+Shift+P` → "Remote-SSH: Connect to Host..."
   - Enter: `username@avd-vm-hostname-or-ip`
   - Authenticate with your credentials
   
4. **Deploy the Application:**
   - Once connected, open VS Code terminal
   - Create directory: `mkdir C:\Apps\MCP_Server_Java`
   - Use VS Code file explorer to drag-drop the JAR file to the remote location
   - Or use SCP from local terminal:
     ```powershell
     scp target/mcp-server-java-1.0.0.jar username@avd-vm:C:/Apps/MCP_Server_Java/
     ```

5. **Run on AVD:**
   ```powershell
   cd C:\Apps\MCP_Server_Java
   java -jar mcp-server-java-1.0.0.jar --http --port 8080
   ```

---

### Method 2: Azure Storage Bridge

**Best for:** Large-scale deployment across multiple AVD session hosts

**Steps:**

1. **Upload to Azure Storage (from VS Code):**
   - Install Azure Storage extension
   - Right-click on `target/mcp-server-java-1.0.0.jar`
   - Select "Upload to Azure Storage"
   - Choose your storage account/container

2. **Generate SAS URL:**
   - In Azure Portal, navigate to your blob
   - Generate SAS token with read permissions
   - Copy the SAS URL

3. **Deploy to AVD:**
   - RDP into AVD session host
   - Run the provided `deploy-to-avd.ps1` script:
     ```powershell
     .\deploy-to-avd.ps1 -DownloadUrl "https://yourstorageaccount.blob.core.windows.net/container/mcp-server-java-1.0.0.jar?sas-token"
     ```

---

### Method 3: GitHub Release + Script

**Best for:** Version-controlled deployments

**Steps:**

1. **Create GitHub Release:**
   - Go to https://github.com/SE-RND/MCP_SERVER_JAVA/releases
   - Click "Create a new release"
   - Tag: `v1.0.0`
   - Upload `target/mcp-server-java-1.0.0.jar` as release asset
   - Publish release

2. **Deploy to AVD:**
   - Copy `deploy-to-avd.ps1` to AVD session host
   - Update the `$DownloadUrl` in the script to your GitHub release URL
   - Run: `.\deploy-to-avd.ps1`

---

### Method 4: RDP + Manual Copy

**Best for:** Simple single-server deployment

**Steps:**

1. **Connect to AVD:**
   - Open Remote Desktop Connection
   - Connect to your AVD session host

2. **Enable Local Drive Sharing:**
   - In RDP, click "Show Options"
   - Go to "Local Resources" → "Local devices and resources" → "More..."
   - Check "Drives" to share your local drives
   - Connect

3. **Copy Files:**
   - On AVD desktop, access `\\tsclient\C\Users\stethompson\Microsoft\MCP_Server_Java\target\`
   - Copy `mcp-server-java-1.0.0.jar` to `C:\Apps\MCP_Server_Java\`

4. **Create Startup Script:**
   ```batch
   @echo off
   cd C:\Apps\MCP_Server_Java
   java -jar mcp-server-java-1.0.0.jar --http --port 8080
   pause
   ```

---

## Post-Deployment Configuration

### Run as Windows Service (Optional)

For production, run as a Windows service using NSSM:

1. **Download NSSM:**
   - Visit: https://nssm.cc/download
   - Extract to `C:\Tools\nssm\`

2. **Install Service:**
   ```powershell
   cd C:\Tools\nssm\win64
   .\nssm install MCPServerJava "java" "-jar C:\Apps\MCP_Server_Java\mcp-server-java-1.0.0.jar --http --port 8080"
   .\nssm set MCPServerJava AppDirectory "C:\Apps\MCP_Server_Java"
   .\nssm set MCPServerJava DisplayName "MCP Server Java"
   .\nssm set MCPServerJava Description "Model Context Protocol Server in Java"
   .\nssm set MCPServerJava Start SERVICE_AUTO_START
   .\nssm start MCPServerJava
   ```

3. **Manage Service:**
   ```powershell
   # Check status
   nssm status MCPServerJava
   
   # Stop service
   nssm stop MCPServerJava
   
   # Restart service
   nssm restart MCPServerJava
   
   # Remove service
   nssm remove MCPServerJava confirm
   ```

---

### Configure Firewall

If accessing from other VMs in AVD host pool:

```powershell
# Allow inbound traffic on port 8080
New-NetFirewallRule -DisplayName "MCP Server Java" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
```

---

### Configure for Multiple Users

For multi-session AVD, deploy to a shared location:

```powershell
# Install to shared location
$SharedPath = "C:\Program Files\MCP_Server_Java"
New-Item -ItemType Directory -Path $SharedPath -Force

# Copy JAR
Copy-Item "mcp-server-java-1.0.0.jar" $SharedPath

# Grant read/execute permissions
icacls $SharedPath /grant "Users:(OI)(CI)RX" /T
```

---

## Testing the Deployment

### From AVD Session Host:

```powershell
# Test locally
curl http://localhost:8080/tools

# Test all endpoints
curl http://localhost:8080/tools/time
curl -X POST http://localhost:8080/tools/add -H "Content-Type: application/json" -d '{\"a\":\"5\",\"b\":\"3\"}'
```

### From Another AVD VM:

```powershell
# Replace <session-host-ip> with actual IP
curl http://<session-host-ip>:8080/tools
```

---

## Troubleshooting

### Issue: Java not found
**Solution:** Install Java 17+:
```powershell
# Using winget
winget install EclipseAdoptium.Temurin.17.JDK

# Or download from: https://adoptium.net/
```

### Issue: Port 8080 already in use
**Solution:** Use a different port:
```powershell
java -jar mcp-server-java-1.0.0.jar --http --port 9090
```

### Issue: Cannot access from other VMs
**Solution:** Check firewall and binding:
- Ensure server binds to 0.0.0.0 (not 127.0.0.1)
- Configure Windows Firewall
- Check NSG rules in Azure

### Issue: Service doesn't start
**Solution:** Check NSSM logs:
```powershell
# View service logs
Get-EventLog -LogName Application -Source "nssm" -Newest 10

# Or check NSSM stdout/stderr
type C:\Apps\MCP_Server_Java\service.log
```

---

## Security Considerations

⚠️ **Important:** This application currently has no authentication!

For production AVD deployments:

1. **Add Authentication:**
   - Implement API key authentication
   - Use Azure AD integration
   - Add request validation

2. **Network Security:**
   - Bind to internal IP only
   - Use Azure Private Link
   - Configure NSG rules

3. **HTTPS:**
   - Use reverse proxy (IIS/nginx) with SSL certificate
   - Or implement HTTPS in Java application

4. **Monitoring:**
   - Enable Application Insights
   - Configure Windows Event Logging
   - Set up alerts

---

## Next Steps

- [ ] Test all endpoints on AVD
- [ ] Configure as Windows service
- [ ] Set up monitoring
- [ ] Implement authentication
- [ ] Configure backup/restore
- [ ] Document for end users

For more information, see the main [README.md](README.md)
