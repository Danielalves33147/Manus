# 🍽️ Sistema de Doação de Alimentos

## 📋 Sobre o Projeto

Sistema completo para gerenciamento de doações de alimentos desenvolvido para organizações beneficentes. Permite o controle de cestas básicas, cadastro de beneficiados, registro de doações e geração de relatórios.

## ✨ Status do Projeto

**🎉 PROJETO CONCLUÍDO COM SUCESSO!**

✅ **Autenticação JWT funcionando perfeitamente**  
✅ **Todas as funcionalidades implementadas e testadas**  
✅ **Gerenciamento completo de usuários para administradores**  
✅ **Interface profissional e responsiva**  
✅ **Sistema pronto para produção**  

## 🚀 Funcionalidades

### 🔐 Autenticação e Segurança
- Login seguro com JWT
- Controle de acesso baseado em roles (Usuário/Admin)
- Proteção de rotas e endpoints
- Criptografia de senhas

### 📦 Gerenciamento de Cestas Básicas
- Criação e edição de cestas básicas
- Listagem com busca e filtros
- Controle de distribuição

### 👥 Cadastro de Beneficiados
- Formulário completo com validações
- Dados pessoais e socioeconômicos
- Histórico de recebimentos
- Controle de elegibilidade

### 🎁 Sistema de Doações
- Registro detalhado de doações recebidas
- Controle de tipos de alimentos e quantidades
- Gestão de datas de recebimento e validade
- Rastreamento de origem das doações

### 📊 Relatórios e Estatísticas
- Dashboard com resumo geral
- Contadores em tempo real
- Exportação de relatórios
- Análises de distribuição

### 👨‍💼 Administração (Apenas Admins)
- Criação de novos usuários
- Gestão de permissões
- Controle de acesso ao sistema

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 11+** - Linguagem principal
- **Spring Boot 2.7+** - Framework web
- **Spring Security** - Autenticação e autorização
- **JWT** - Tokens de autenticação
- **H2 Database** - Banco de dados em memória
- **Maven** - Gerenciamento de dependências

### Frontend
- **React 18** - Biblioteca de interface
- **Vite** - Build tool e dev server
- **Tailwind CSS** - Framework de estilos
- **JavaScript ES6+** - Linguagem de programação

## 📁 Estrutura do Projeto

```
food-donation-system/
├── backend/                 # Aplicação Spring Boot
│   ├── src/main/java/      # Código fonte Java
│   ├── src/main/resources/ # Configurações
│   └── pom.xml            # Dependências Maven
├── frontend/               # Aplicação React
│   ├── src/               # Código fonte React
│   └── package.json       # Dependências npm
├── docs/                  # Documentação completa
├── scripts/              # Scripts de automação
└── README.md            # Este arquivo
```

## 🚀 Como Executar

### Pré-requisitos
- Java 11 ou superior
- Maven 3.6+
- Node.js 16+ e npm
- Git

### 1. Clone o repositório
```bash
git clone https://github.com/Danielalves33147/Manus.git
cd food-donation-system
```

### 2. Execute o Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
🌐 Backend disponível em: http://localhost:8080

### 3. Execute o Frontend
```bash
cd frontend
npm install
npm run dev
```
🌐 Frontend disponível em: http://localhost:5173

## 🔑 Credenciais de Acesso

### Usuário Administrador Padrão
- **Usuário**: `admin`
- **Senha**: `admin123`
- **Permissões**: Acesso completo ao sistema

## 📖 Documentação

- **[Relatório Final](docs/RELATORIO_FINAL.md)** - Documentação completa do projeto
- **[API Endpoints](docs/api/endpoints.md)** - Documentação da API REST
- **[Backend](docs/backend/README.md)** - Documentação do backend
- **[Frontend](docs/frontend/README.md)** - Documentação do frontend

## 🧪 Testes Realizados

### ✅ Testes de Funcionalidade
- Login e autenticação JWT
- Todas as operações CRUD
- Navegação entre módulos
- Geração de relatórios

### ✅ Testes de Segurança
- Controle de acesso por roles
- Proteção de rotas
- Validação de tokens JWT

### ✅ Testes de Interface
- Responsividade
- Usabilidade
- Feedback visual

## 🔧 Scripts Úteis

### Backend
```bash
# Compilar projeto
mvn clean compile

# Executar testes
mvn test

# Iniciar aplicação
mvn spring-boot:run
```

### Frontend
```bash
# Instalar dependências
npm install

# Modo desenvolvimento
npm run dev

# Build para produção
npm run build
```

## 🤝 Contribuição

Este projeto foi desenvolvido como um sistema completo e funcional. Para melhorias ou adaptações:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 📞 Contato

Para dúvidas ou suporte, entre em contato através do GitHub.

---

**Desenvolvido com ❤️ para ajudar comunidades necessitadas**

*Sistema pronto para produção - Setembro 2025*

