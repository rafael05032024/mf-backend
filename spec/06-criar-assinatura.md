# 06 - Criação de assinatura

Permite a um usuário logado (assinante) assinar um produtor, pagando o valor do plano em footcoins (ft; R$ 1,00 = 5 ft). A plataforma retém 10% do valor. Renovação ainda não é tratada.

## Endpoint

`POST /api/signatures` · `Content-Type: application/json` · autenticado (JWT, `@Authenticated`)

O id do assinante vem do `sub` do token (`SecurityContext`).

### Request (`CreateSignatureRequestDTO`)

| Campo      | Tipo   | Regra                          |
|------------|--------|--------------------------------|
| `producer` | string | obrigatório (não vazio); `profile` do usuário produtor, sem o `@` |

### Resposta (`CreateSignatureResponseDTO`)

| Campo        | Tipo      | Descrição                       |
|--------------|-----------|---------------------------------|
| `id`         | number    | id da assinatura                |
| `producer`   | number    | id do produtor                  |
| `subscriber` | number    | id do assinante (usuário logado)|
| `expireAt`   | timestamp | data de expiração da assinatura |

### Status

| Status | Quando                                                        | Corpo                                 |
|--------|---------------------------------------------------------------|---------------------------------------|
| 200    | Assinatura criada                                             | `CreateSignatureResponseDTO`          |
| 400    | `producer` ausente ou vazio                                          | tratado pela validação do Quarkus     |
| 400    | Assinante e produtor são o mesmo usuário                      | `CreateAccountResponseDTO` (`message`) |
| 401    | Token ausente ou inválido                                     | tratado por `JwtAuthenticationFilter` |
| 402    | Saldo do assinante menor que o valor do plano: `Saldo insuficiente` | `CreateAccountResponseDTO` (`message`) |
| 403    | Produtor com `deleted_at` preenchido: `Usuário inativo`       | `CreateAccountResponseDTO` (`message`) |
| 403    | Produtor não verificado: `Produtor não é verificado`          | `CreateAccountResponseDTO` (`message`) |
| 404    | Assinante inexistente/inativo: `Usuário inexistente`          | `LoginResponseDTO` (`message`)        |
| 404    | Produtor inexistente: `Produtor inexistente`                  | `LoginResponseDTO` (`message`)        |
| 404    | Produtor sem plano: `Produtor não possui plano`               | `LoginResponseDTO` (`message`)        |
| 409    | Já existe assinatura vigente do assinante para o produtor     | `CreateAccountResponseDTO` (`message`) |

## Regras de negócio

Avaliadas nesta ordem, a primeira que falhar encerra a requisição:

1. O assinante deve existir e estar ativo (`UserRepository.findActiveById`), senão `Usuário inexistente` (404).
2. O produtor deve existir, buscado pelo `profile` sem diferenciar maiúsculas de minúsculas (`UserRepository.findByProfile`, que inclui usuários inativos), senão `Produtor inexistente` (404).
3. O assinante não pode ser o próprio produtor (mesmo id), senão `Você não pode assinar a si mesmo` (400).
4. O produtor deve estar ativo (`deleted_at` nulo), senão `Usuário inativo` (403).
5. O produtor deve estar verificado (`verified = true`), senão `Produtor não é verificado` (403). Um produtor inativo e não verificado recebe o erro de inativo.
6. O produtor deve ter plano (`PlanRepository.findByProducer`), senão `Produtor não possui plano` (404).
7. As carteiras do assinante e do produtor são travadas (`WalletRepository.findByOwnerForUpdate`, lock pessimista), sempre na ordem do menor id de usuário, para evitar deadlock. O lock também serializa requisições concorrentes do mesmo assinante.
8. Não pode existir assinatura do assinante para o produtor com `expire_at` no futuro (`SignatureRepository.existsActive`), senão `Você já possui uma assinatura vigente para este produtor` (409). Assinaturas expiradas não bloqueiam uma nova.
9. O saldo da carteira do assinante deve ser maior ou igual ao valor do plano, senão `Saldo insuficiente` (402).
10. Cobrança (valores em ft):
    - o valor do plano é debitado da carteira do assinante e registrado em `transaction` com `type = 2` (`DEBIT`) na carteira do assinante, com `description` = `Assinatura do perfil @{profile do produtor}`;
    - o produtor recebe `plano - 10%` (`plano × 0,90`, arredondado a 2 casas, `HALF_UP`), creditado na carteira do produtor e registrado em `transaction` com `type = 1` (`CREDIT`), com `description` = `Assinatura de @{profile do assinante}`. Ex.: plano de 500 ft → produtor recebe 450 ft; a plataforma retém 50 ft (R$ 10,00). A retenção não é registrada em nenhuma tabela.
11. A assinatura é criada com `expire_at` = agora + 30 dias (`SIGNATURE_DURATION_DAYS`).
12. Duas notificações são criadas (`notification`):
    - assinante: `Assinatura de @{profile do produtor} confirmada por 30 dias`;
    - produtor: `@{profile do assinante} acabou de assinar seu perfil!!`.

A carteira de cada usuário é criada no cadastro da conta; se não existir, a requisição falha com erro 500.

Toda a operação ocorre em um único `@Transactional`.

## Componentes

| Camada     | Classe                                                       | Responsabilidade                                                   |
|------------|--------------------------------------------------------------|--------------------------------------------------------------------|
| resource   | `CreateSignatureResource`                                    | Recebe o JSON e o id do token, delega ao service, devolve o DTO    |
| service    | `CreateSignatureService`                                     | Aplica as regras acima e cria a assinatura (`@Transactional`)      |
| repository | `SignatureRepository`, `UserRepository`, `PlanRepository`, `WalletRepository`, `TransactionRepository`, `NotificationRepository` | `existsActive`; `findActiveById`, `findByProfile`; `findByProducer`; `findByOwnerForUpdate`; persistência |
| model      | `SignatureModel`, `TransactionModel`, `NotificationModel`    | Entidades de `signature`, `transaction` e `notification`           |
| model      | `TransactionType`, `TransactionTypeConverter`                | `CREDIT` = 1, `DEBIT` = 2 gravados como inteiro                    |
| dto        | `CreateSignatureRequestDTO`, `CreateSignatureResponseDTO`    | Entrada (com validação) e saída                                    |
| exception  | `SelfSignatureException` + mapper                            | Auto-assinatura → 400                                              |
| exception  | `InactiveUserException` + mapper                             | Produtor inativo → 403                                             |
| exception  | `UnverifiedPublisherException` + mapper (existente)          | Produtor não verificado → 403                                      |
| exception  | `BusinessConflictException` + mapper (existente)             | Assinatura vigente → 409                                           |
| exception  | `UserNotFoundException` + mapper (existente)                 | Usuário/produtor inexistente ou produtor sem plano → 404           |
| exception  | `InsufficientBalanceException` + mapper                      | Saldo insuficiente → 402                                           |

## Decisões e suposições

Pontos que não vieram da especificação original e foram assumidos na implementação. Confirmar ou ajustar:

- **Identificação do produtor:** o request recebe o `profile` (string), não o id. O `profile` é único sem diferenciar maiúsculas de minúsculas (validado no cadastro). O response continua devolvendo o id do produtor.
- **Nomes:** `CreateSignatureRequestDTO`/`CreateSignatureResponseDTO` (o pedido original escrevia "Signaure"). Os models seguem o padrão do projeto: `SignatureModel`, `TransactionModel`, `NotificationModel`.
- **Unidade:** o `value` do plano (spec 04) e o `balance` da carteira são tratados como footcoins (ft). Nenhuma conversão para R$ é feita no código.
- **Comissão:** 10% do valor do plano (`PLATFORM_FEE_RATE`), calculada como `plano × 0,10` e descontada do crédito do produtor. Equivale à fórmula `plano - ((plano / 5) × 0,10) × 5`.
- **Duração:** 30 dias fixos (`SIGNATURE_DURATION_DAYS`), usados também no texto da notificação.
- **Texto da notificação do produtor:** `seu perfil`; o pedido original dizia "se perfil", tratado como erro de digitação.
- **Status HTTP:** 403 para produtor inativo ou não verificado (segue o mapper de produtor não verificado já existente), 400 para auto-assinatura, 409 para assinatura vigente, 402 para saldo insuficiente.
- **Plano obrigatório:** produtor sem plano retorna 404 (`Produtor não possui plano`), reaproveitando `UserNotFoundException`.
- **Concorrência:** o lock pessimista das carteiras serializa requisições do mesmo assinante, o que torna confiável a checagem de assinatura vigente e do saldo. Não há restrição no banco para isso.
- **Carteira ausente:** tratada como estado inválido (500), pois a carteira é criada no cadastro da conta.
- **Auto-assinatura e duplicidade:** auto-assinatura é bloqueada; assinar de novo o mesmo produtor só é permitido depois do vencimento da assinatura anterior.

## Banco

Tabelas usadas (migration V1): `signature`, `transaction`, `notification`, `wallet`, `plan`.

Migration `V7__increase_notification_text_length.sql`: `notification.text` passa de `VARCHAR(50)` para `VARCHAR(255)`, porque o texto da notificação com o profile não cabia em 50 caracteres.

## Testes

Ainda não há `CreateSignatureResourceTest`.

## Pendências

- Escrever os testes do recurso (nenhum teste foi escrito nem executado para esta funcionalidade; só foi verificado que o projeto compila).
- Confirmar a duração da assinatura (30 dias) e o status `402` para saldo insuficiente.
- Registrar a comissão de 10%: hoje ela não vai para nenhuma carteira nem transação.
- Renovação de assinatura ainda não é tratada.
- Decidir se o response deve incluir o valor cobrado e o saldo restante.
