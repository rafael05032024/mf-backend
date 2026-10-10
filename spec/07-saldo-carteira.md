# 07 - Saldo da carteira

Retorna o saldo em footcoins (ft) da carteira do usuário logado.

## Endpoint

`GET /api/wallet/balance` · autenticado (JWT, `@Authenticated`)

O id do usuário vem do `sub` do token (`SecurityContext`).

### Resposta (`GetWalletBalanceResponseDTO`)

| Campo     | Tipo    | Descrição                  |
|-----------|---------|----------------------------|
| `balance` | decimal | saldo da carteira, em ft   |

### Status

| Status | Quando                                         | Corpo                          |
|--------|------------------------------------------------|--------------------------------|
| 200    | Saldo retornado                                | `GetWalletBalanceResponseDTO`  |
| 401    | Token ausente ou inválido                      | tratado por `JwtAuthenticationFilter` |
| 404    | Usuário inexistente/inativo: `Usuário inexistente` | `LoginResponseDTO` (`message`) |

Carteira ausente é estado inválido (500), pois é criada no cadastro da conta.

## Componentes

`GetWalletBalanceResource` → `GetWalletBalanceService` → `UserRepository.findActiveById`, `WalletRepository.findByOwner`.

## Pendências

- Sem testes automatizados.
