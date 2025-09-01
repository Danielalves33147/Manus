# Backend - Sistema de Doações

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2**
- **Spring Security** (autenticação JWT)
- **Spring Data JPA** (persistência)
- **H2 Database** (banco em memória para desenvolvimento)
- **Maven** (gerenciamento de dependências)

## Estrutura do Projeto

```
backend/
├── src/main/java/com/church/fooddonation/
│   ├── entity/           # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── CestaBasica.java
│   │   ├── CestaBasicaItem.java
│   │   ├── AlimentoEstoque.java
│   │   ├── Beneficiado.java
│   │   ├── Doacao.java
│   │   └── Distribuicao.java
│   ├── repository/       # Repositórios de dados
│   ├── service/         # Lógica de negócio
│   ├── controller/      # Controllers REST
│   ├── security/        # Configurações de segurança
│   ├── dto/            # Objetos de transferência
│   └── config/         # Configurações gerais
├── src/main/resources/
│   └── application.yml  # Configurações da aplicação
└── pom.xml             # Configuração do Maven
```

## Endpoints Principais

### Autenticação
- `POST /api/auth/init` - Inicializar usuário administrador
- `POST /api/auth/login` - Realizar login

### Cestas Básicas
- `GET /api/cestas-basicas` - Listar todas as cestas
- `POST /api/cestas-basicas` - Criar nova cesta
- `GET /api/cestas-basicas/{id}` - Buscar cesta por ID
- `PUT /api/cestas-basicas/{id}/finalizar` - Finalizar montagem
- `DELETE /api/cestas-basicas/{id}` - Excluir cesta

## Como Executar

1. Navegue até o diretório backend:
   ```bash
   cd backend
   ```

2. Compile o projeto:
   ```bash
   mvn clean compile
   ```

3. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```

A aplicação estará disponível em `http://localhost:8080/api`.

## Problemas Conhecidos

- **Autenticação JWT**: Há um problema na configuração do Spring Security que impede o login de funcionar corretamente (erro 401).
- **CORS**: Pode ser necessário ajustar as configurações de CORS para permitir requisições do frontend.

## Configurações

### Banco de Dados
O projeto está configurado para usar H2 Database em memória. As tabelas são criadas automaticamente na inicialização.

### Segurança
- JWT com expiração de 24 horas
- Senha criptografada com BCrypt
- Usuário administrador padrão: `admin` / `admin123`

