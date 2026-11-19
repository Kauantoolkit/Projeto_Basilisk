
#!/bin/bash
# setup.sh — Prepara todas as dependências do projeto na ordem correta.
# Execute uma vez antes de rodar qualquer app.
set -e

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "[1/4] Limpando marcadores .lastUpdated stale do Maven..."
find ~/.m2/repository/com/basilisk -name "*.lastUpdated" -delete 2>/dev/null || true

echo "[2/4] Instalando bibliotecas Java no repositório local Maven..."
cd "$ROOT_DIR/basilisk-libs-java"
mvn clean install -q
echo "      basilisk-libs-java instalado com sucesso."

echo "[3/4] Compilando pacotes TypeScript (basilisk-libs)..."
cd "$ROOT_DIR/basilisk-libs"
pnpm install
pnpm build
echo "      basilisk-libs compilado com sucesso."

echo "[4/4] Instalando dependências do frontend (gym-manager/web)..."
cd "$ROOT_DIR/gym-manager/web"
npm install
echo "      gym-manager/web pronto."

echo ""
echo "Setup concluído! Use ./run-gym.sh para iniciar o app."
