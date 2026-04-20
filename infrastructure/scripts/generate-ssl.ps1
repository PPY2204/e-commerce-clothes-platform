# Generate Self-Signed SSL Certificates for Local Development (Nginx)
$certDir = "./infrastructure/nginx/certs"
if (!(Test-Path $certDir)) { New-Item -ItemType Directory -Path $certDir }

# Check if openssl is available
$certDir = "./infrastructure/nginx/certs"
if (!(Test-Path $certDir)) { New-Item -ItemType Directory -Path $certDir }

# Possible paths for OpenSSL in Git for Windows
$opensslPaths = @(
    "openssl", # If already in PATH
    "E:\MyApp\Git\usr\bin\openssl.exe",
    "C:\Program Files\Git\usr\bin\openssl.exe",
    "C:\Program Files (x86)\Git\usr\bin\openssl.exe"
)

$opensslCmd = $null
foreach ($path in $opensslPaths) {
    if (Get-Command $path -ErrorAction SilentlyContinue) {
        $opensslCmd = $path
        break
    }
}

if ($opensslCmd) {
    & $opensslCmd req -x509 -nodes -days 365 -newkey rsa:2048 `
      -keyout "$certDir/nginx.key" `
      -out "$certDir/nginx.crt" `
      -subj "/C=VN/ST=HCM/L=HCM/O=YamateeClub/OU=IT/CN=localhost"
    Write-Host "SSL Certificates generated successfully in $certDir" -ForegroundColor Green
} else {
    Write-Host "OpenSSL not found in standard paths. Please ensure Git for Windows is installed." -ForegroundColor Red
}
