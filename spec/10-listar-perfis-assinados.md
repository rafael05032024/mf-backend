# 10 - Listagem de perfis assinados

Lista os produtores com assinatura vigente (`expire_at` no futuro) do usuário logado.

## Endpoint

`GET /api/signatures` · autenticado (JWT, `@Authenticated`)

O id do assinante vem do `sub` do token (`SecurityContext`).

### Resposta (`List<ListSignedProfilesResponseDTO>`)

| Campo       | Tipo      | Descrição                          |
|-------------|-----------|------------------------------------|
| `profile`   | string    | `profile` do produtor              |
| `thumb`     | string    | foto de perfil do produtor (pode ser nulo) |
| `expire_at` | timestamp | expiração da assinatura            |

Ordenada por `expire_at` crescente. Sem assinaturas, devolve `[]`.

### Status

| Status | Quando                                         | Corpo                          |
|--------|------------------------------------------------|--------------------------------|
| 200    | Lista retornada                                | lista de `ListSignedProfilesResponseDTO` |
| 401    | Token ausente ou inválido                      | tratado por `JwtAuthenticationFilter` |
| 404    | Usuário inexistente/inativo: `Usuário inexistente` | `LoginResponseDTO` (`message`) |

## Componentes

`ListSignedProfilesResource` → `ListSignedProfilesService` → `UserRepository.findActiveById`, `UserRepository.findByIds`, `SignatureRepository.listActiveBySubscriber`.

## Decisões e suposições

- Só assinaturas vigentes; expiradas ficam de fora.
- Produtores inativos continuam na lista, pois a assinatura segue vigente.

## Pendências

- Sem testes automatizados.
