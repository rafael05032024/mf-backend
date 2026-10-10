# 09 - Número de assinaturas

Retorna quantas assinaturas vigentes (`expire_at` no futuro) o usuário logado possui, como assinante e como produtor.

## Endpoint

`GET /api/signatures/count` · autenticado (JWT, `@Authenticated`)

O id do usuário vem do `sub` do token (`SecurityContext`).

### Resposta (`GetSignatureCountResponseDTO`)

| Campo           | Tipo   | Descrição                                           |
|-----------------|--------|-----------------------------------------------------|
| `subscriptions` | number | assinaturas vigentes que o usuário fez (assinante)  |
| `subscribers`   | number | assinaturas vigentes que o usuário recebeu (produtor) |

### Status

| Status | Quando                                         | Corpo                          |
|--------|------------------------------------------------|--------------------------------|
| 200    | Contagens retornadas                           | `GetSignatureCountResponseDTO` |
| 401    | Token ausente ou inválido                      | tratado por `JwtAuthenticationFilter` |
| 404    | Usuário inexistente/inativo: `Usuário inexistente` | `LoginResponseDTO` (`message`) |

## Componentes

`GetSignatureCountResource` → `GetSignatureCountService` → `UserRepository.findActiveById`, `SignatureRepository.countActiveBySubscriber`, `SignatureRepository.countActiveByProducer`.

## Decisões e suposições

- O pedido ("número de assinatura do usuário") era ambíguo; foram retornadas as duas contagens. Ajustar se só uma for necessária.
- Só contam assinaturas vigentes; expiradas ficam de fora.

## Pendências

- Sem testes automatizados.
