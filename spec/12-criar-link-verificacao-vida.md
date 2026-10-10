# 12 - Link de verificação de vida

Cria uma sessão de verificação (liveness) no provedor antifraude (Didit) para o usuário logado e registra a solicitação em `LIVENESS_REQUEST`.

## Endpoint

`POST /api/liveness` · autenticado (JWT, `@Authenticated`)

O id do usuário vem do `sub` do token (`SecurityContext`). Sem corpo.

### Resposta (`CreateLivenessLinkResponseDTO`)

| Campo             | Tipo   | Descrição                                  |
|-------------------|--------|--------------------------------------------|
| `id`              | number | id da solicitação (`liveness_request`)     |
| `verificationUrl` | string | link de verificação retornado pelo provedor |

### Status

| Status | Quando                                              | Corpo                     |
|--------|-----------------------------------------------------|---------------------------|
| 200    | Sessão criada e solicitação registrada              | `CreateLivenessLinkResponseDTO` |
| 400    | Usuário sem `document`, `personal_name` ou `birthdate` | `message`              |
| 401    | Token ausente ou inválido                           | `JwtAuthenticationFilter` |
| 404    | Usuário inexistente/inativo                         | `message`                 |
| 502    | Falha no provedor antifraude                        | `message`                 |

## Regras

- Dados enviados ao provedor vêm do usuário: `document`, `email`, `personal_name` (nome real) e `birthdate`.
- `personal_name` é dividido no primeiro espaço: `Rafael Luiz Pereira` → `first_name: Rafael`, `last_name: Luiz Pereira`.
- `birthdate` é enviada no formato `YYYY-MM-DD`.
- A solicitação só é persistida após o provedor criar a sessão; guarda `user_id` e `session_id`. `processed_at` fica nulo até a conclusão da verificação.

## Componentes

`CreateLivenessLinkResource` → `CreateLivenessLinkService` → `UserRepository.findActiveById`, `FraudPreventionProvider.createVerificationSession`, `LivenessRequestRepository`.

## Pendências

- Recebimento do resultado da verificação (webhook) e atualização de `processed_at`/`verified` não implementados.
- Sem testes automatizados.
