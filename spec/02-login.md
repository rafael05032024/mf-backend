# 02 - Login

Autentica um usuário pelo e-mail e senha e devolve um token JWT.

## Endpoint

`POST /api/login` · `Content-Type: application/json`

### Request (`LoginRequestDTO`)

| Campo      | Tipo   | Regra                          |
|------------|--------|--------------------------------|
| `email`    | string | obrigatório, formato de e-mail |
| `password` | string | obrigatório (não vazio)        |

```json
{"email": "ana@x.com", "password": "secret"}
```

### Respostas

| Status | Quando                                              | Corpo                                |
|--------|-----------------------------------------------------|--------------------------------------|
| 200    | Credenciais corretas                                | `LoginResponseDTO` (`token`)         |
| 400    | Campo ausente/vazio ou e-mail com formato inválido  | erro padrão do Bean Validation       |
| 401    | Senha diferente da gravada                          | `LoginResponseDTO` (`message`)       |
| 404    | Nenhum usuário com o e-mail informado               | `LoginResponseDTO` (`message`)       |

`LoginResponseDTO` omite do JSON o campo nulo: o sucesso traz só `token`, os erros só `message`.

## Regras de negócio

1. Os dois campos são obrigatórios e o e-mail deve ter formato válido (`@Valid` no recurso).
2. O usuário é buscado pelo e-mail, sem espaços nas pontas e sem diferenciar maiúsculas de minúsculas.
3. Usuário com `deleted_at` preenchido é tratado como inexistente.
4. Se não existir, retorna erro `Usuário inexistente` (404).
5. A senha recebida é comparada com o hash BCrypt gravado (`BcryptUtil.matches`). Se diferir, retorna erro `Senha inválida` (401).
6. Se for igual, gera um JWT assinado (HS256) com:
   - `sub`: id do usuário, para os recursos que exigirem usuário logado;
   - `iss`: `fmarket`;
   - expiração de 2 horas.

## Componentes

| Camada     | Classe                                              | Responsabilidade                                                  |
|------------|-----------------------------------------------------|-------------------------------------------------------------------|
| resource   | `LoginResource`                                     | Recebe e valida o request, delega ao service, devolve o token     |
| service    | `LoginService`                                      | Busca o usuário, confere a senha, gera o JWT                      |
| repository | `UserRepository`                                    | `findActiveByEmail` (Panache)                                     |
| dto        | `LoginRequestDTO`, `LoginResponseDTO`               | Entrada e saída (records)                                         |
| exception  | `UserNotFoundException`, `InvalidPasswordException` | Lançadas pelo service                                             |
| exception  | `UserNotFoundExceptionMapper`, `InvalidPasswordExceptionMapper` | Convertem as exceções em 404 e 401                    |

## Configuração

Em `application.yml`, sob `app.jwt`:

| Propriedade  | Valor                                  | Descrição                                       |
|--------------|----------------------------------------|-------------------------------------------------|
| `issuer`     | `fmarket`                              | Claim `iss` do token                            |
| `secret`     | variável `JWT_SECRET`                  | Segredo de assinatura (obrigatório em produção) |
| `expiration` | `PT2H`                                 | Validade do token                               |

Em `dev` e `test` há um segredo fixo, só para desenvolvimento.

## Dependências adicionadas

- `quarkus-smallrye-jwt-build`: geração e assinatura do JWT.

## Testes

`LoginResourceTest`: devolve o token com `sub` e validade de 2 horas (200), rejeita usuário inexistente (404), senha errada (401) e entradas inválidas (400).

## Pendências

- A validação do token nos recursos protegidos ainda não existe. Ela exigirá configurar a verificação com o mesmo segredo, ou migrar para um par de chaves.
- As mensagens distintas de 404 e 401 permitem descobrir quais e-mails estão cadastrados.
