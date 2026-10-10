# 08 - Listagem de transações da carteira

Retorna o saldo e as transações da carteira do usuário logado, da mais recente para a menos recente (`created_at` desc, desempate por `id` desc).

## Endpoint

`GET /api/wallet/transactions` · autenticado (JWT, `@Authenticated`)

### Resposta (`ListTransactionsResponseDTO`)

| Campo         | Tipo    | Descrição                          |
|---------------|---------|------------------------------------|
| `balance`     | decimal | saldo da carteira, em ft           |
| `transaction` | lista   | transações (`TransactionDTO`)      |

`TransactionDTO`: `id` (number), `type` (number: 1 = CREDIT, 2 = DEBIT), `value` (decimal, ft), `description` (string, pode ser `null`).

### Status

| Status | Quando                                             | Corpo                                 |
|--------|----------------------------------------------------|---------------------------------------|
| 200    | Listagem retornada (lista vazia se não há transações) | `ListTransactionsResponseDTO`      |
| 401    | Token ausente ou inválido                          | tratado por `JwtAuthenticationFilter` |
| 404    | Usuário inexistente/inativo: `Usuário inexistente` | `LoginResponseDTO` (`message`)        |

Carteira ausente é estado inválido (500).

## Componentes

`ListTransactionsResource` → `ListTransactionsService` → `UserRepository.findActiveById`, `WalletRepository.findByOwner`, `TransactionRepository.findByWalletId`.

## Pendências

- Sem paginação nem testes automatizados.
