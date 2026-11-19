#!/bin/bash
# run-gym.sh — Inicia o backend e o frontend do gym-manager em paralelo.
# Rode ./setup.sh primeiro se ainda não tiver feito.
set -e

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

cleanup() {
  echo ""
  echo "Encerrando processos..."
  kill "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

echo "Iniciando backend (Spring Boot)..."
cd "$ROOT_DIR/gym-manager"
mvn spring-boot:run &
BACKEND_PID=$!

echo "Iniciando frontend (Vite)..."
cd "$ROOT_DIR/gym-manager/web"
npm run dev &
FRONTEND_PID=$!

echo ""
echo "Backend PID: $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo "Pressione Ctrl+C para encerrar ambos."

wait "$BACKEND_PID" "$FRONTEND_PID"
