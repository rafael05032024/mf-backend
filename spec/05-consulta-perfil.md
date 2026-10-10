# 05 - Consulta de perfil

Retorna os dados públicos de um perfil, seus posts, o valor do plano e contadores de mídia. A busca é feita pelo `profile` (nome de usuário).

## Endpoint

`GET /api/profiles/{profile}` · público (sem `@Authenticated`); o header `Authorization: Bearer <jwt>` é opcional e só alimenta o campo `signed`

### Resposta (`GetProfileResponseDTO`)

| Campo         | Tipo    | Descrição                                                    |
|---------------|---------|--------------------------------------------------------------|
| `name`        | string  | nome do usuário                                              |
| `profile`     | string  | perfil                                                       |
| `description` | string  | descrição                                                    |
| `tiktok`      | string  | usuário no TikTok                                            |
| `instagram`   | string  | usuário no Instagram                                         |
| `verified`    | boolean | publicador verificado                                        |
| `thumb`       | string  | foto de perfil                                               |
| `cover_photo` | string  | foto de capa                                                 |
| `posts`       | lista   | posts do usuário, mais recentes primeiro (`PostDTO`)         |
| `plan_value`  | decimal | valor do plano; `null` se o usuário não tem plano            |
| `counters`    | objeto  | contadores de mídia (`CountersDTO`)                          |
| `signed`      | boolean | `true` se o usuário logado é o dono do perfil ou tem assinatura ativa com ele; `false` se anônimo ou sem assinatura ativa |

`PostDTO`:

| Campo        | Tipo    | Descrição                                                              |
|--------------|---------|------------------------------------------------------------------------|
| `id`         | long    | id do post                                                             |
| `content`    | string  | nome do arquivo; **vazio (`""`) quando o post é privado (mídia paga)** |
| `type`       | string  | `image` ou `video`                                                     |
| `description`| string  | descrição do post                                                      |
| `is_private` | boolean | `true` para mídia paga                                                 |

`CountersDTO` (considera todos os posts do usuário, públicos e privados):

| Campo            | Tipo | Descrição                    |
|------------------|------|------------------------------|
| `private_midias` | long | total de mídias privadas     |
| `images`         | long | total de imagens             |
| `videos`         | long | total de vídeos              |

### Status

| Status | Quando                                              | Corpo                          |
|--------|-----------------------------------------------------|--------------------------------|
| 200    | Perfil encontrado                                   | `GetProfileResponseDTO`        |
| 404    | Perfil inexistente ou com `deleted_at` preenchido   | `LoginResponseDTO` (`message`) |

## Regras de negócio

1. O perfil é buscado sem diferenciar maiúsculas de minúsculas (`UserRepository.findActiveByProfile`); o valor recebido é aparado nas pontas.
2. Usuário excluído (`deleted_at` preenchido) é tratado como inexistente: `Perfil inexistente` (404).
3. Posts vêm de `user_post` pelo `owner`, ordenados por `created_at` decrescente, sem paginação.
4. Em posts com `is_private = true` o `content` é devolvido vazio, para não expor o nome do arquivo da mídia paga.
5. `plan_value` vem do plano do produtor (`PlanRepository.findByProducer`), ou `null` se não houver.
6. Os contadores são calculados por `count` no banco (`UserPostRepository`).
7. `signed` é `true` quando o usuário logado consulta o próprio perfil; caso contrário usa `SignatureRepository.existsActive` (`expire_at` no futuro). Sem `Authorization` = anônimo (`false`); token presente porém inválido = 401.

## Componentes

| Camada     | Classe                                                              | Responsabilidade                                          |
|------------|---------------------------------------------------------------------|-----------------------------------------------------------|
| resource   | `GetProfileResource`                                                | Recebe o `profile` no path, delega ao service             |
| service    | `GetProfileService`                                                 | Busca usuário, posts, plano e contadores; monta o DTO     |
| repository | `UserRepository`, `UserPostRepository`, `PlanRepository`            | `findActiveByProfile`; `findByOwner`, `countPrivateByOwner`, `countByOwnerAndType`; `findByProducer` |
| dto        | `GetProfileResponseDTO` (`PostDTO`, `CountersDTO`)                  | Saída                                                     |
| exception  | `UserNotFoundException` / `UserNotFoundExceptionMapper`             | 404                                                       |

## Banco

Sem migration nova. Usa `users`, `user_post` e `plan`.

## Testes

Ainda não há `GetProfileResourceTest`.

## Pendências

- Escrever os testes do recurso.
- `/api/midia/{q}` continua público: quem souber o nome de um arquivo privado ainda acessa a mídia. Esconder o `content` na consulta de perfil não protege o blob.
- Lista de posts sem paginação.
