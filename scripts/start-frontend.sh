#!/bin/bash

# Script para iniciar o frontend do Sistema de Doações

echo "=== Iniciando Frontend do Sistema de Doações ==="
echo "Navegando para o diretório frontend..."

cd "$(dirname "$0")/../frontend" || exit 1

echo "Verificando dependências..."
if [ ! -d "node_modules" ]; then
    echo "Instalando dependências..."
    pnpm install
fi

echo "Iniciando o servidor de desenvolvimento..."
pnpm run dev --host

