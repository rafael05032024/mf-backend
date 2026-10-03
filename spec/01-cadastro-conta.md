# 01 - Cadastro de conta

Permite criar uma conta de usuário.

## Endpoint

`POST /api/accounts` · `Content-Type: application/json`

### Request (`CreateAccountRequestDTO`)

| Campo      | Tipo   | Regra                          |
|------------|--------|--------------------------------|
| `name`     | string | obrigatório (não vazio)        |
| `email`    | string | obrigatório, formato de e-mail |
| `profile`  | string | obrigatório (não vazio)        |
| `password` | string | obrigatório (não vazio)        |

```json
{"name": "Ana", "email": "ana@x.com", "profile": "ana", "password": "secret"}
```

### Respostas

| Status | Quando                                                          | Corpo                                  |
|--------|-----------------------------------------------------------------|----------------------------------------|
| 204    | Conta criada                                                    | sem corpo                              |
| 400    | Campo ausente/vazio ou e-mail com formato inválido              | erro padrão do Bean Validation         |
| 409    | E-mail ou profile já cadastrado                                 | `CreateAccountResponseDTO` (`message`) |

## Regras de negócio

1. Todos os campos são obrigatórios e o e-mail deve ter formato válido (`@Valid` no recurso).
2. `name`, `email` e `profile` são salvos sem espaços nas pontas.
3. Não pode existir outro usuário com o mesmo e-mail. Comparação sem diferenciar maiúsculas de minúsculas.
4. Não pode existir outro usuário com o mesmo profile. Comparação sem diferenciar maiúsculas de minúsculas.
5. A senha é gravada como hash BCrypt (`BcryptUtil`), nunca em texto puro.
6. O usuário é inserido na tabela `users` (`verified = false`; datas preenchidas pelo banco).

As checagens de unicidade consideram também usuários com `deleted_at` preenchido.

## Componentes

| Camada     | Classe                                       | Responsabilidade                                                                  |
|------------|----------------------------------------------|-----------------------------------------------------------------------------------|
| resource   | `CreateAccountResource`                      | Recebe e valida o request, delega ao service, devolve 204                         |
| service    | `CreateAccountService`                       | Checa duplicidade, gera o hash, persiste (`@Transactional`)                       |
| repository | `UserRepository`                             | `existsByEmail`, `existsByProfile` (Panache)                                      |
| model      | `UserModel`                                  | Entidade mapeada para a tabela `users`                                            |
| dto        | `CreateAccountRequestDTO`, `CreateAccountResponseDTO` | Entrada e saída (records)                                                |
| exception  | `BusinessConflictException`                  | Lançada pelo service em caso de duplicidade                                       |
| exception  | `BusinessConflictExceptionMapper`            | Converte a exceção em resposta 409                                                |

## Dependências adicionadas

- `quarkus-hibernate-validator`: validação dos DTOs.
- `quarkus-elytron-security-common`: hash BCrypt da senha.

## Testes

`CreateAccountResourceTest`: cria a conta (204), rejeita e-mail e profile duplicados (409) e rejeita entradas inválidas (400).

## Pendências

- A migration não tem índice `UNIQUE` em `email` nem `profile`; cadastros simultâneos podem passar pela checagem.

## Desenvolvimento com hot reload

`docker-compose stop app && docker-compose up app-dev db` sobe o serviço `app-dev` (`mvn quarkus:dev`) com recarga automática do código e debug remoto na porta 5005.
