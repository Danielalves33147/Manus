# Documentação da API - Sistema de Doações

## Base URL
```
http://localhost:8080/api
```

## Autenticação

### Inicializar Usuário Administrador
```http
POST /auth/init
Content-Type: application/json
```

**Resposta de Sucesso:**
```
Status: 200 OK
Content: "Usuário administrador criado com sucesso"
```

### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Resposta de Sucesso:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

## Cestas Básicas

### Listar Todas as Cestas
```http
GET /cestas-basicas
Authorization: Bearer {token}
```

**Resposta:**
```json
[
  {
    "id": "uuid",
    "nomeCesta": "Cesta Básica 1",
    "dataMontagem": "2025-09-01",
    "status": "Em Montagem",
    "itens": []
  }
]
```

### Criar Nova Cesta
```http
POST /cestas-basicas
Authorization: Bearer {token}
Content-Type: application/json

{
  "nomeCesta": "Nova Cesta Básica"
}
```

### Buscar Cesta por ID
```http
GET /cestas-basicas/{id}
Authorization: Bearer {token}
```

### Buscar Cestas por Status
```http
GET /cestas-basicas/status/{status}
Authorization: Bearer {token}
```

Valores válidos para status:
- `Em Montagem`
- `Montada`
- `Distribuída`

### Buscar Cestas Disponíveis
```http
GET /cestas-basicas/disponiveis
Authorization: Bearer {token}
```

### Adicionar Item à Cesta
```http
POST /cestas-basicas/{cestaId}/itens
Authorization: Bearer {token}
Content-Type: application/json

{
  "alimentoEstoqueId": "uuid",
  "quantidade": 2.5
}
```

### Finalizar Montagem da Cesta
```http
PUT /cestas-basicas/{cestaId}/finalizar
Authorization: Bearer {token}
```

### Excluir Cesta
```http
DELETE /cestas-basicas/{id}
Authorization: Bearer {token}
```

### Estatísticas por Status
```http
GET /cestas-basicas/estatisticas/{status}
Authorization: Bearer {token}
```

**Resposta:**
```json
{
  "count": 5
}
```

## Códigos de Status

- `200 OK` - Sucesso
- `201 Created` - Recurso criado com sucesso
- `204 No Content` - Sucesso sem conteúdo (DELETE)
- `400 Bad Request` - Dados inválidos
- `401 Unauthorized` - Não autenticado
- `403 Forbidden` - Sem permissão
- `404 Not Found` - Recurso não encontrado
- `500 Internal Server Error` - Erro interno do servidor

## Autenticação JWT

Todos os endpoints protegidos requerem o header:
```
Authorization: Bearer {token}
```

O token JWT tem validade de 24 horas e deve ser renovado após expirar.

## Problemas Conhecidos

- **Erro 401**: Atualmente há um problema na configuração do Spring Security que causa erro 401 mesmo para endpoints que deveriam estar liberados.
- **CORS**: Pode ser necessário configurar CORS adequadamente para permitir requisições do frontend.

