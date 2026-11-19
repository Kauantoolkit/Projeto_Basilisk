# run-gym.ps1 — Inicia o backend e o frontend do gym-manager.
# Rode .\setup.ps1 primeiro se ainda não tiver feito.
$ROOT_DIR = $PSScriptRoot

# Garante que o Maven use o Java 21 do sistema
$javaPath = (Get-Command java -ErrorAction SilentlyContinue).Source
if ($javaPath) {
    $env:JAVA_HOME = (Resolve-Path (Join-Path (Split-Path $javaPath) "..")).Path
}

Write-Host "Usando JAVA_HOME: $env:JAVA_HOME"
Write-Host "Iniciando backend (Spring Boot)..."
$backend = Start-Process -PassThru -NoNewWindow powershell -ArgumentList "-NoProfile -Command `"Set-Location '$ROOT_DIR\gym-manager'; `$env:JAVA_HOME='$env:JAVA_HOME'; mvn spring-boot:run`""

Write-Host "Iniciando frontend (Vite)..."
$frontend = Start-Process -PassThru -NoNewWindow powershell -ArgumentList "-NoProfile -Command `"Set-Location '$ROOT_DIR\gym-manager\web'; npm run dev`""

Write-Host ""
Write-Host "Backend PID: $($backend.Id)"
Write-Host "Frontend PID: $($frontend.Id)"
Write-Host "Pressione Ctrl+C para encerrar."

try {
    Wait-Process -Id $backend.Id, $frontend.Id
} finally {
    Stop-Process -Id $backend.Id -Force -ErrorAction SilentlyContinue
    Stop-Process -Id $frontend.Id -Force -ErrorAction SilentlyContinue
}
