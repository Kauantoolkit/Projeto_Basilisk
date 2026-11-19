# setup.ps1 — Prepara todas as dependências do projeto na ordem correta.
# Execute uma vez antes de rodar qualquer app.
$ErrorActionPreference = "Stop"

$ROOT_DIR = $PSScriptRoot

# Verifica JDK 21+
Write-Host "Verificando versão do Java..."
$javaOutput = $null
try {
    $javaOutput = (& java -version 2>&1) -join " "
} catch {}
$javaVersion = if ($javaOutput -match 'version "(\d+)') { [int]$Matches[1] } else { 0 }
if ($javaVersion -lt 21) {
    Write-Host ""
    Write-Host "ERRO: Java 21+ é necessário. Versão detectada: $javaVersion" -ForegroundColor Red
    Write-Host "Instale o JDK 21 em: https://adoptium.net/" -ForegroundColor Yellow
    Write-Host "Após instalar, configure JAVA_HOME para o diretório do JDK 21." -ForegroundColor Yellow
    exit 1
}
Write-Host "      Java $javaVersion detectado. OK."

Write-Host "[1/4] Limpando marcadores .lastUpdated stale do Maven..."
$basiliskM2 = "$env:USERPROFILE\.m2\repository\com\basilisk"
if (Test-Path $basiliskM2) {
    Get-ChildItem -Path $basiliskM2 -Recurse -Filter "*.lastUpdated" | Remove-Item -Force
    Write-Host "      Marcadores removidos."
} else {
    Write-Host "      Nenhum encontrado."
}

Write-Host "[2/4] Instalando bibliotecas Java no repositório local Maven..."
Set-Location "$ROOT_DIR\basilisk-libs-java"
mvn clean install -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO: mvn clean install falhou." -ForegroundColor Red
    exit 1
}
Write-Host "      basilisk-libs-java instalado com sucesso."

Write-Host "[3/4] Compilando pacotes TypeScript (basilisk-libs)..."
Set-Location "$ROOT_DIR\basilisk-libs"
$env:CI = "true"
pnpm install --no-frozen-lockfile
if ($LASTEXITCODE -ne 0) { Write-Host "ERRO: pnpm install falhou." -ForegroundColor Red; exit 1 }
pnpm build
if ($LASTEXITCODE -ne 0) { Write-Host "ERRO: pnpm build falhou." -ForegroundColor Red; exit 1 }
Write-Host "      basilisk-libs compilado com sucesso."

Write-Host "[4/4] Instalando dependências do frontend (gym-manager/web)..."
Set-Location "$ROOT_DIR\gym-manager\web"
npm install
if ($LASTEXITCODE -ne 0) { Write-Host "ERRO: npm install falhou." -ForegroundColor Red; exit 1 }
Write-Host "      gym-manager/web pronto."

Set-Location $ROOT_DIR
Write-Host ""
Write-Host "Setup concluído! Use .\run-gym.ps1 para iniciar o app."
