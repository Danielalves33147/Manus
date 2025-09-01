#!/bin/bash

# Script para iniciar o backend do Sistema de Doações

echo "=== Iniciando Backend do Sistema de Doações ==="
echo "Navegando para o diretório backend..."

cd "$(dirname "$0")/../backend" || exit 1

echo "Compilando o projeto..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "Compilação bem-sucedida! Iniciando o servidor..."
    mvn spring-boot:run
else
    echo "Erro na compilação. Verifique os logs acima."
    exit 1
fi

