# 11 - Solicitação de recarga

Gera um QR Code Pix no Asaas para o usuário logado recarregar a carteira e registra a solicitação em `RECHARGE_REQUEST`.

## Endpoint

`POST /api/wallet/recharges` · autenticado (JWT, `@Authenticated`)

O id do usuário vem do `sub` do token (`SecurityContext`).

### Requisição (`CreateRechargeRequestDTO`)

| Campo   | Tipo    | Regra                          |
|---------|---------|--------------------------------|
| `value` | decimal | obrigatório, entre 15.00 e 150.00 (R$) |

### Resposta (`CreateRechargeResponseDTO`)

| Campo         | Tipo   | Descrição                                |
|---------------|--------|------------------------------------------|
| `id`          | number | id da solicitação (`recharge_request`)   |
| `qrCodeImage` | string | imagem do QR Code em base64 (`encodedImage` do Asaas) |
| `payload`     | string | Pix copia e cola (`payload` do Asaas)    |

### Status

| Status | Quando                                   | Corpo                      |
|--------|------------------------------------------|----------------------------|
| 200    | QR Code gerado e solicitação registrada  | `CreateRechargeResponseDTO` |
| 400    | Valor fora do intervalo ou ausente       | `message`                  |
| 401    | Token ausente ou inválido                | `JwtAuthenticationFilter`  |
| 404    | Usuário inexistente/inativo              | `message`                  |
| 502    | Falha no gateway de pagamento            | `message`                  |

## Regras

- Valor em reais, normalizado para 2 casas; expiração do QR Code em 30 minutos.
- A solicitação só é persistida após o gateway gerar o QR Code. Guarda `user_id`, `value`, `reference` (id do QR Code retornado pelo Asaas, migration V10), `expired_at` (momento em que o QR Code expira, migration V11) e `created_at`; `processed_at` fica nulo até a confirmação do pagamento.

## Componentes

`CreateRechargeResource` → `CreateRechargeService` → `UserRepository.findActiveById`, `PaymentGatewayProvider.generatePixQrCode`, `RechargeRequestRepository`.

## Pendências

- Confirmação do pagamento (webhook) e crédito na carteira ainda não implementados.
- Sem testes automatizados.
