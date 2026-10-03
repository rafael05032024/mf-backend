# Estrutura do Projeto

Pacote base: `com.fmarket` (`src/main/java/com/fmarket`). Stack: Quarkus (Jakarta REST).

```
com.fmarket
├── model/       # Entidades
├── repository/  # Manipulação das entidades
├── service/     # Regras de negócio
├── resource/    # Endpoints do projeto
├── provider/    # Serviços externos
└── dto/         # Interfaces de Req e Res
```

Pastas vazias são mantidas no Git com um arquivo `.keep`. Ele pode ser removido quando a pasta tiver arquivos reais.

## Fluxo de dependência

`resource` → `service` → `repository` → `model`

- `service` também pode usar `provider`.
- `resource` e `service` trocam dados via `dto`.
- Nunca pule camadas (ex.: `resource` acessando `repository` diretamente).

## O que colocar em cada pasta

### model
- Entidades que representam as tabelas do banco (mapeadas com as migrations em `src/main/resources/db/migration`).
- Enums e value objects ligados ao domínio.
- Apenas estado e mapeamento. Sem regra de negócio, sem acesso a dados e sem dependência de DTO.
- Nunca exponha entidades nos endpoints. Use `dto`.

### repository
- Classes de acesso e manipulação das entidades (consultas, persistência, atualização, remoção).
- Apenas operações de dados. Sem regra de negócio e sem validações de domínio.
- Não conhece `dto`, `resource` nem `provider`.
- Nome: `<Entidade>Repository`.

### service
- Regras de negócio, validações de domínio e orquestração entre repositories e providers.
- Controle transacional (`@Transactional`) fica aqui.
- Converte entidade ↔ DTO.
- Não conhece detalhes HTTP (`Response`, status codes, headers). Sinalize erros com exceções de negócio.
- Nome: `<Entidade>Service`.

### resource
- Endpoints REST (`@Path`, `@GET`, `@POST`...).
- Apenas recebe a requisição, valida a entrada (`@Valid`), delega ao `service` e devolve a resposta.
- Sem regra de negócio e sem acesso direto a `repository`.
- Sempre recebe e devolve `dto`, nunca entidades.
- Rotas sob o prefixo `/api`.
- Nome: `<Entidade>Resource`.

### provider
- Integração com serviços externos (ex.: API de pagamentos, e-mail, gateways).
- Encapsula clientes HTTP, autenticação, mapeamento e tratamento de erros do serviço externo.
- Isola o restante da aplicação do fornecedor: o `service` depende de uma interface, e a implementação concreta fica aqui.
- Nome: `<Servico>Provider`.

### dto
- Objetos de entrada (Request) e saída (Response) dos endpoints.
- Prefira `record`.
- Nome: `<Entidade>Request` e `<Entidade>Response`.
- Podem conter anotações de validação (`jakarta.validation`).
- Sem lógica de negócio.

## Outras pastas

- `health/`: health checks (ex.: `LivenessCheck`).
- `src/main/resources/db/migration/`: migrations do banco.
- `src/test/java/com/fmarket/`: testes, espelhando a estrutura de pacotes de `main`.
