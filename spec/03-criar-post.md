# 03 - Criação de post

Permite a um publicador verificado criar um post com uma mídia (foto ou vídeo). A mídia vai para o Azure Blob Storage.

## Endpoint

`POST /api/posts` · `Content-Type: multipart/form-data` · autenticado (JWT, `@Authenticated`)

O id do usuário vem do `sub` do token (`SecurityContext`).

### Request (`CreatePostRequestDTO`)

| Campo         | Tipo    | Regra                                                      |
|---------------|---------|------------------------------------------------------------|
| `midia`       | arquivo | obrigatório, não vazio; foto ou vídeo nos formatos aceitos |
| `is_private`  | string  | obrigatório; somente `true` ou `false` (mídia paga)        |
| `description` | string  | opcional (pode ser vazio); até 255 caracteres              |

### Respostas

| Status | Quando                                                                                   | Corpo                                    |
|--------|------------------------------------------------------------------------------------------|------------------------------------------|
| 204    | Post criado                                                                              | sem corpo                                |
| 400    | Mídia ausente/vazia, formato inválido, acima do tamanho, `is_private` inválido, descrição longa | `UpdateAccountResponseDTO` (`message`) |
| 401    | Token ausente ou inválido                                                                | tratado por `JwtAuthenticationFilter`    |
| 403    | Usuário com `verified = false`                                                           | `CreateAccountResponseDTO` (`message`)   |
| 404    | Usuário inexistente ou com `deleted_at` preenchido                                       | `LoginResponseDTO` (`message`)           |

## Regras de negócio

1. `midia` é obrigatória e não pode estar vazia.
2. `is_private` é obrigatório e só aceita `true` ou `false` (sem diferenciar maiúsculas de minúsculas).
3. `description` pode ser vazia ou ausente (gravada como string vazia); é salva sem espaços nas pontas.
4. O tipo é definido pelo content-type do arquivo:

   | Tipo (`PostType`) | Content-types                                       | Tamanho máximo |
   |-------------------|-----------------------------------------------------|----------------|
   | `IMAGE`           | `image/jpeg`, `image/png`, `image/webp`             | 5 MB           |
   | `VIDEO`           | `video/mp4`, `video/webm`, `video/quicktime`        | 20 MB          |

   O limite de vídeo acompanha `quarkus.http.limits.max-body-size` (20M).
5. O usuário deve existir e estar ativo (`UserRepository.findActiveById`), senão `Usuário inexistente` (404).
6. O usuário deve ter `verified = true`, senão erro 403: `Você não pode postar conteúdo porque não possui uma conta de publicador verificada`.
7. Só depois das validações o arquivo é enviado ao Azure (`BlobStorageProvider.store`) com o nome `<uuid>.<extensão>`.
8. O post é gravado em `user_post` com `owner` = id do usuário, `content` = nome do arquivo no Azure, `type` conforme a regra 4, `is_private` e `description`.

Upload e gravação ocorrem no mesmo `@Transactional`. Se a gravação falhar depois do upload, o blob fica órfão (não há remoção compensatória).

## Componentes

| Camada     | Classe                                                              | Responsabilidade                                                           |
|------------|---------------------------------------------------------------------|----------------------------------------------------------------------------|
| resource   | `CreatePostResource`                                                | Recebe o multipart e o id do token, delega ao service, devolve 204         |
| service    | `CreatePostService`                                                 | Valida, checa usuário verificado, envia a mídia e persiste (`@Transactional`) |
| repository | `UserPostRepository`, `UserRepository`                              | Persistência do post; `findActiveById`                                     |
| provider   | `BlobStorageProvider` (`AzureBlobStorageProvider`)                  | Upload da mídia no Azure                                                   |
| model      | `UserPost`, `PostType`                                              | Entidade de `user_post` e enum `IMAGE`/`VIDEO` (gravado como ordinal: 0/1) |
| dto        | `CreatePostRequestDTO`, `CreatePostResponseDTO`                     | Entrada; saída (o service a devolve, mas o resource não a expõe)           |
| exception  | `InvalidImageException`, `UserNotFoundException`, `UnverifiedPublisherException` | Lançadas pelo service                                         |
| exception  | `InvalidImageExceptionMapper`, `UserNotFoundExceptionMapper`, `UnverifiedPublisherExceptionMapper` | Convertem as exceções em 400, 404 e 403 |

## Banco

Tabela `user_post` (migrations V1 e V6): `id`, `owner` (FK `users`), `type`, `content` (varchar 255), `is_private`, `description` (varchar 255), `created_at`, `updated_at`. Nenhuma migration nova.

## Testes

Ainda não há `CreatePostResourceTest`.

## Pendências

- Escrever os testes do recurso.
- Mensagens de validação de mídia reutilizam `InvalidImageException`, cujo nome fala em imagem.
- `CreatePostResponseDTO` está sem uso no resource desde a mudança para 204.
- O `type` é gravado como ordinal; reordenar `PostType` quebraria os dados existentes.
