# 04 - Criação/atualização de plano

Permite a um produtor definir o valor do seu plano. Se o usuário ainda não tem plano, ele é criado; se já tem, apenas o valor é atualizado.

## Endpoint

`POST /api/plans` · `Content-Type: application/json` · autenticado (JWT, `@Authenticated`)

O id do usuário (produtor) vem do `sub` do token (`SecurityContext`).

### Request (`CreatePlanRequestDTO`)

| Campo   | Tipo    | Regra                                                   |
|---------|---------|---------------------------------------------------------|
| `value` | decimal | obrigatório; mínimo `0.01`; até 2 casas decimais        |

### Resposta (`CreatePlanResponseDTO`)

| Campo   | Tipo    | Descrição                  |
|---------|---------|----------------------------|
| `id`    | number  | id do plano                |
| `value` | decimal | valor atual do plano       |

### Status

| Status | Quando                                                  | Corpo                                  |
|--------|---------------------------------------------------------|----------------------------------------|
| 200    | Plano criado ou atualizado                              | `CreatePlanResponseDTO`                |
| 400    | `value` ausente, abaixo de `0.01` ou com mais de 2 casas | tratado pela validação do Quarkus      |
| 401    | Token ausente ou inválido                               | tratado por `JwtAuthenticationFilter`  |
| 404    | Usuário inexistente ou com `deleted_at` preenchido      | `LoginResponseDTO` (`message`)         |

## Regras de negócio

1. `value` é obrigatório, no mínimo `0.01` e com no máximo 2 casas decimais.
2. O usuário deve existir e estar ativo (`UserRepository.findActiveById`), senão `Usuário inexistente` (404).
3. Se o usuário já tem plano (`PlanRepository.findByProducer`), o `value` e o `updated_at` são atualizados. Caso contrário, um novo plano é criado com `producer` = id do usuário.
4. Criação e atualização retornam o mesmo `200`: o service não conhece HTTP e não informa se criou ou atualizou.
5. Não é exigido `verified = true` para ter plano.

Toda a operação ocorre em um único `@Transactional`.

## Componentes

| Camada     | Classe                                                 | Responsabilidade                                                  |
|------------|--------------------------------------------------------|-------------------------------------------------------------------|
| resource   | `CreatePlanResource`                                   | Recebe o JSON e o id do token, delega ao service, devolve o DTO   |
| service    | `CreatePlanService`                                    | Valida usuário ativo, cria ou atualiza o plano (`@Transactional`) |
| repository | `PlanRepository`, `UserRepository`                     | `findByProducer`; `findActiveById`                                |
| model      | `PlanModel`                                            | Entidade de `plan`                                                |
| dto        | `CreatePlanRequestDTO`, `CreatePlanResponseDTO`        | Entrada (com validação) e saída                                   |
| exception  | `UserNotFoundException`, `UserNotFoundExceptionMapper` | Usuário inexistente → 404                                         |

## Banco

Tabela `plan` (migration V1): `id`, `producer` (FK `users`, indexado), `value` (`NUMERIC(19,2)`), `created_at`, `updated_at`. Nenhuma migration nova.

## Testes

Ainda não há `CreatePlanResourceTest`.

## Pendências

- Escrever os testes do recurso.
- `plan.producer` não tem `UNIQUE`: requisições simultâneas de um produtor sem plano podem criar dois planos. Uma migration `V7` com `UNIQUE (producer)` resolveria, após conferir se já há produtores com mais de um plano.
- Decidir se criar plano deve exigir `verified = true`, como nos posts.
- Decidir se a criação deve responder `201`, o que exigiria o service sinalizar criação vs. atualização.
