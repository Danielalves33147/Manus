# Frontend - Sistema de Doações

## Tecnologias Utilizadas

- **React 18**
- **Vite** (bundler e servidor de desenvolvimento)
- **Tailwind CSS** (estilização)
- **shadcn/ui** (componentes de interface)
- **Lucide Icons** (ícones)
- **pnpm** (gerenciador de pacotes)

## Estrutura do Projeto

```
frontend/
├── public/              # Arquivos estáticos
├── src/
│   ├── components/ui/   # Componentes de interface (shadcn/ui)
│   ├── assets/         # Recursos estáticos (imagens, etc.)
│   ├── lib/            # Utilitários e bibliotecas
│   ├── App.jsx         # Componente principal
│   ├── App.css         # Estilos principais
│   ├── index.css       # Estilos globais
│   └── main.jsx        # Ponto de entrada
├── index.html          # Template HTML
├── package.json        # Dependências e scripts
└── vite.config.js      # Configuração do Vite
```

## Funcionalidades Implementadas

### Tela de Login
- Formulário de autenticação
- Validação de campos
- Design responsivo
- Credenciais de teste visíveis

### Dashboard Principal
- Sistema de abas para diferentes módulos
- Interface para gerenciamento de cestas básicas
- Placeholders para funcionalidades futuras

### Gerenciamento de Cestas Básicas
- Criação de novas cestas
- Listagem de cestas existentes
- Visualização de status das cestas
- Interface para edição (placeholder)

## Como Executar

1. Navegue até o diretório frontend:
   ```bash
   cd frontend
   ```

2. Instale as dependências (se necessário):
   ```bash
   pnpm install
   ```

3. Inicie o servidor de desenvolvimento:
   ```bash
   pnpm run dev --host
   ```

A aplicação estará disponível em `http://localhost:5175` (ou outra porta disponível).

## Integração com Backend

O frontend está configurado para se comunicar com o backend em `http://localhost:8080/api`. As principais integrações incluem:

- **Autenticação**: `POST /api/auth/login`
- **Cestas Básicas**: `GET/POST /api/cestas-basicas`

## Componentes Principais

### App.jsx
Componente principal que gerencia:
- Estado de autenticação
- Roteamento entre tela de login e dashboard
- Comunicação com a API

### Interface de Login
- Formulário com validação
- Tratamento de erros
- Armazenamento de token JWT

### Dashboard
- Sistema de abas
- Gerenciamento de estado local
- Interface responsiva

## Estilos e Design

- **Tailwind CSS** para estilização utilitária
- **shadcn/ui** para componentes consistentes
- **Design responsivo** para desktop e mobile
- **Tema claro** com possibilidade de extensão para tema escuro

