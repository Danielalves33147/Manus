# Sistema de Gerenciamento de Doações

Este é um sistema completo para gerenciamento de doações de alimentos, com foco em cestas básicas. O projeto é dividido em duas partes principais: um backend desenvolvido em Java com Spring Boot e um frontend desenvolvido em React.

## Estrutura do Projeto

```
food-donation-system/
├── backend/          # Contém o projeto Spring Boot (food-donation-api)
├── frontend/         # Contém o projeto React (food-donation-frontend)
├── docs/             # Documentação geral do sistema (relatórios, etc.)
├── scripts/          # Scripts de automação (build, deploy, etc.)
└── README.md         # Este arquivo
```

## Como Iniciar

### Backend (Java Spring Boot)

1. Navegue até o diretório `backend`:
   ```bash
   cd backend
   ```
2. Compile e execute o projeto:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   O backend estará disponível em `http://localhost:8080/api`.

### Frontend (React)

1. Navegue até o diretório `frontend`:
   ```bash
   cd frontend
   ```
2. Instale as dependências e inicie o servidor de desenvolvimento:
   ```bash
   pnpm install
   pnpm run dev --host
   ```
   O frontend estará disponível em `http://localhost:5175` (ou outra porta disponível).

## Problemas Conhecidos

Atualmente, há um problema de autenticação no backend que impede o login de funcionar corretamente (retorna 401 Unauthorized). Isso precisa ser investigado e corrigido na configuração do Spring Security.

## Documentação

Consulte o diretório `docs/` para relatórios e outras documentações relevantes.


