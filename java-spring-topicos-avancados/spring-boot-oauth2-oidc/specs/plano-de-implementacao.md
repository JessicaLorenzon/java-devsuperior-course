# Plano de implementação — Evolução do dsbooks-as

Partindo da baseline mínima (Authorization Server in-memory), esta aula evolui o projeto em cinco passos incrementais, cada um introduzindo uma única ideia nova.

---

## Passo 1 — Externalizar valores hardcoded para `application.properties`

**Objetivo:** preparar o terreno removendo o issuer URL hardcoded de dentro do código Java e movendo-o para `application.properties`, lido via `@Value` (ou `@ConfigurationProperties`, conforme preferência didática). Aproveita-se também o passo para ajustar o perfil padrão e algumas configurações de servidor.

**O que sai do código e vai para o `application.properties`:**

- Issuer URL do Authorization Server (hoje `"http://localhost:9000"` em `AuthorizationServerSettings`)

> **Importante:** os demais valores hardcoded de `RegisteredClient` (`client-id`, `client-secret`, os dois `redirect-uri` e o `post-logout-redirect-uri`) **NÃO** serão externalizados neste passo. Todos eles vão ser movidos para o banco H2 no Passo 4, junto com o restante dos dados de clients.

**Outros ajustes neste passo:**

- Perfil padrão de desenvolvimento e testes muda de `dev` para `test`.
- Define-se um nome próprio para o cookie de sessão (`AS_DSBOOKS_SESSION`) — útil para distinguir a sessão do AS de outras aplicações que rodem na mesma origem durante o desenvolvimento.
- Desliga-se o `spring.jpa.open-in-view` (boa prática que vai ficar pré-configurada antes do JPA entrar em cena no Passo 3).
- A porta do servidor passa a também ser configurável por variável de ambiente (`SERVER_PORT`).

**Estado final esperado de `application.properties`:**

```properties
spring.profiles.active=${APP_PROFILE:test}

spring.jpa.open-in-view=false

server.port=${SERVER_PORT:9000}
server.servlet.session.cookie.name=AS_DSBOOKS_SESSION

app.security.issuer=${APP_ISSUER_URL:http://localhost:9000}
```

**Reorganização da collection do Postman:**

O Passo 1 também reorganiza a parte de testes manuais no Postman, separando configuração (environment) do conteúdo (collection):

- **Variáveis** (antes hardcoded dentro da collection) saem da collection e passam a ser definidas em um **Postman environment** chamado `dsbooks-dev`, salvo em `dsbooks-dev.postman_environment.json` na raiz do projeto. Variáveis do environment:

  | Variável                    | Valor                                          |
  |-----------------------------|------------------------------------------------|
  | `as-host`                   | `http://localhost:9000`                        |
  | `rs-host`                   | `http://localhost:8080`                        |
  | `client-id`                 | `manual-client`                                |
  | `client-secret`             | `my-client-secret`                             |
  | `scopes`                    | `openid profile email`                         |
  | `redirect-uri`              | `https://oauth.pstmn.io/v1/callback`           |
  | `post-logout-redirect-uri`  | `http://localhost:3000/api/post-logout`        |
  | `code-verifier`             | `dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk`  |
  | `state`                     | `xyz`                                          |

- **Estrutura da collection** muda: as duas pastas antigas `1. Discovery & JWK` e `2. Fluxo Authorization Code` (e o folder `3. OIDC` que continha apenas UserInfo) são **substituídas por uma única pasta `Auth`** com 4 endpoints, todos `GET`:

  - `GET OIDC Discovery Document`
  - `GET OAuth2 AS Metadata`
  - `GET JWK Set (public RSA key)`
  - `GET UserInfo`

- **Obtenção de token** deixa de ser feita por requests individuais dentro da collection. Em vez disso, o fluxo Authorization Code com PKCE é configurado **na aba Authorization da própria collection**, usando as variáveis do environment `dsbooks-dev`. O usuário clica em **Get New Access Token** e o Postman cuida de toda a dança do fluxo.

- **Autenticação dos requests:**
  - Os 3 endpoints públicos (Discovery, AS Metadata, JWK Set) usam **No Auth**.
  - O endpoint protegido `GET UserInfo` usa **Inherit auth from parent**, ou seja, usa o token que estiver salvo no nível da collection.

Para que o "Get New Access Token" funcione já ao final do Passo 1, o bean `RegisteredClientRepository` (in-memory) é ajustado para registrar um único client com os mesmos valores do environment `dsbooks-dev`: `clientId = manual-client`, `clientSecret = {noop}my-client-secret`, scopes `openid`, `profile`, `email`, `redirectUri = https://oauth.pstmn.io/v1/callback` e `postLogoutRedirectUri = http://localhost:3000/api/post-logout`.

**Por que primeiro:** nenhum aluno deveria precisar editar código Java para apontar o AS para outro ambiente. Esse é o lesson menos denso da aula e introduz o aluno ao padrão de configuração externalizada que será reutilizado em todos os passos seguintes. A reorganização do Postman também já posiciona a collection no formato definitivo, sem precisar refazê-la depois.

**Entrega esperada:**

- O app continua se comportando exatamente igual, mas `AuthorizationServerConfig` não contém mais o literal do issuer URL.
- `application.properties` está no estado final mostrado acima.
- Existe um arquivo `dsbooks-dev.postman_environment.json` na raiz do projeto.
- A collection `dsbooks-as.postman_collection.json` não tem mais variáveis na raiz, tem OAuth2 configurado no nível da collection, e contém apenas a pasta `Auth` com os 4 endpoints `GET` listados acima.

---

## Passo 2 — Formulário de login customizado com Thymeleaf

**Objetivo:** substituir o `formLogin(Customizer.withDefaults())` por uma página de login própria, em Thymeleaf, com layout dark mode.

**Requisitos:**

- Template Thymeleaf para a página de login
- Título visível na página: **"Login com DSBooks"**
- CSS puro, bem organizado, usando **variáveis CSS** (`:root { --color-bg: ...; --space-md: ...; ... }`) para cores e tamanhos
- Layout dark mode
- Atributo no campo `password` para que **nenhum valor prévio do navegador apareça** quando o usuário clicar no input (autocomplete desabilitado / `autocomplete="new-password"` ou equivalente)
- Configuração do Spring Security apontando para a página customizada (`.formLogin(form -> form.loginPage("/login").permitAll())`)

**Por que neste momento:** é puramente UI, não tem acoplamento com a infraestrutura de usuários, e dá ao aluno uma "vitória visual" antes dos passos mais densos de persistência.

**Entrega esperada:** ao acessar uma rota protegida, o navegador é redirecionado para a página de login customizada, com o título "Login com DSBooks" e visual dark mode, e o login funciona normalmente com os usuários in-memory existentes (`maria@example.com` / `alex@example.com`).

---

## Passo 3 — Remover `InMemoryUserDetailsManager` e persistir usuários no H2

**Objetivo:** trocar a implementação in-memory de usuários por usuários persistentes em banco H2 (via JPA), e já configurar o **H2 web console** como ferramenta de inspeção do banco em desenvolvimento.

**Dependências a adicionar no `pom.xml`:**

- `spring-boot-starter-data-jpa`
- `com.h2database:h2` (scope `runtime`)
- `org.springframework.boot:spring-boot-h2console`

> **Importante:** no Spring Boot 4 o `H2ConsoleAutoConfiguration` foi extraído de `spring-boot-autoconfigure` para um módulo próprio (`spring-boot-h2console`). Sem essa dependência, `spring.h2.console.enabled=true` é silenciosamente ignorada e o console nunca é registrado.

**O que entra no projeto:**

- Entidade `UserEntity` (tabela **`tb_user`**), implementando `UserDetails`. Modelo simples neste passo:
  - `id` (PK, `Long`, autoincremental)
  - `username` (unique, not null)
  - `password` (not null)
  - **Sem** atributo `authorities`. A implementação de `getAuthorities()` retorna `List.of()` direto — neste passo o sistema não trabalha com authorities/roles.

- `UserRepository extends JpaRepository<UserEntity, Long>` com método `findByUsername`.

- `UserService` (`@Service`) implementando `UserDetailsService`. Spring Security auto-pega esse bean como o `UserDetailsService` ativo do `DaoAuthenticationProvider`.

- `DatabaseSeeder` (`CommandLineRunner`) que insere maria e alex com a senha literal `{noop}12345678`.

  > **Por que seed programático em vez de `import.sql`?** Não é por causa da senha — `{noop}12345678` cabe perfeitamente em SQL. A motivação é antecipatória: **passos futuros vão criptografar campos sensíveis do usuário** (email, etc.) via JPA AttributeConverter. Como o ciphertext muda a cada execução (vetor de inicialização aleatório), não dá para hardcodar valores em `import.sql`. O seed precisa rodar em código Java, com os converters JPA ativos. Já adotamos `CommandLineRunner` desde já para evitar retrabalho depois.

- `application-test.properties` ganha conteúdo completo (ver abaixo).

**Ajustes de `defaultSecurityFilterChain` para o H2 console funcionar:**

O console é servido por um servlet separado, **fora do `DispatcherServlet`**. Isso quebra duas suposições do Spring Security 7:

1. `requestMatchers(String...)` resolve para um `MvcRequestMatcher`, que só casa rotas servidas pelo dispatcher MVC. **Solução:** usar `PathPatternRequestMatcher.pathPattern("/h2-console/**")` explicitamente para essa rota.
2. O console envia POST sem CSRF token e usa `<iframe>`. **Solução:** `csrf().ignoringRequestMatchers(...)` e `headers().frameOptions(frame -> frame.sameOrigin())`.

> **Armadilha histórica:** `AntPathRequestMatcher`, padrão em projetos Spring Security 5/6, **foi removido no Spring Security 7**. O substituto é `PathPatternRequestMatcher` (em `org.springframework.security.web.servlet.util.matcher`).

**O que sai do projeto:**

- Bean `userDetailsService` (in-memory) e `InMemoryUserDetailsManager` — removidos de `AuthorizationServerConfig`.

**O que continua em `AuthorizationServerConfig`:**

- O bean `PasswordEncoder` (delegating) **permanece**. Continua sendo necessário para o `DaoAuthenticationProvider` validar senhas — inclusive as marcadas com `{noop}`, já que o `DelegatingPasswordEncoder` lê o prefixo e delega para o encoder apropriado.

**Conteúdo final esperado de `application-test.properties`:**

```properties
# H2 in-memory database connection
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# H2 web console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Log SQL statements to the console
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Allow import.sql to use multi-line statements (delimited by ; instead of newline)
spring.jpa.properties.hibernate.hbm2ddl.import_files_sql_extractor=org.hibernate.tool.schema.internal.script.MultiLineSqlScriptExtractor
```

**Por que neste momento:** é o primeiro passo que mexe em infra de dados. O aluno aprende JPA + `UserDetailsService` customizado, sem precisar pensar em mudança de modelo ainda — isso vem só no Passo 5. O H2 console entra junto porque é a ferramenta natural para inspecionar o banco daqui em diante.

**Entrega esperada:**

- Login continua funcionando com `maria@example.com` / `alex@example.com` (senha `12345678`), agora servido pelo H2 via JPA.
- `http://localhost:9000/h2-console/` abre o console. JDBC URL `jdbc:h2:mem:testdb`, user `sa`, senha vazia. `SELECT * FROM tb_user` mostra os dois registros seedados, com password `{noop}12345678`.

---

## Passo 4 — Cadastro de clients no banco (entidade `OAuthClient`)

**Objetivo:** mover o cadastro dos clients OAuth2 do código (`InMemoryRegisteredClientRepository`) para o banco H2, usando uma entidade própria. Isso elimina o último ponto de hardcode pendente do Passo 1 e permite cadastrar múltiplos clients representando atores distintos do ecossistema dsbooks.

**O que entra no projeto:**

- **Entidade `OAuthClient`** (tabela **`tb_oauth_client`**) representando um `RegisteredClient` do Spring Authorization Server. Campos:
  - `id` (PK, `String`)
  - `clientId` (unique, not null), `clientSecret` (nullable para public clients), `clientName` (not null)
  - Coleções armazenadas como **strings separadas por vírgula** (decisão deliberada para manter o esquema simples — a alternativa seria tabelas filhas):
    - `redirectUris` (not null, length 1000)
    - `postLogoutRedirectUris` (nullable, length 1000)
    - `scopes` (not null, length 500) — ex.: `"openid,profile,email"`
    - `authorizationGrantTypes` (not null, length 200) — ex.: `"authorization_code,refresh_token"`
    - `clientAuthenticationMethods` (not null, length 200) — ex.: `"client_secret_basic,client_secret_post"`
  - `accessTokenTtlSeconds` / `refreshTokenTtlSeconds` (`long`) — TTLs em segundos.
  - `requireConsent` (`boolean`) — `false` para apps first-party.

- **Repository `OAuthClientRepository`** (`JpaRepository<OAuthClient, String>`) com `findByClientId`.

- **`CustomRegisteredClientRepository`** (em `services/oauth2/`, anotado `@Component`) implementando `RegisteredClientRepository` do Spring AS. Faz a tradução bidirecional `OAuthClient ⇄ RegisteredClient` — split/join nas vírgulas para as coleções, `Duration.ofSeconds(...)` para os TTLs, e `ClientSettings.builder().requireAuthorizationConsent(...)`.

- **Seed via `import.sql`** (não via `CommandLineRunner` — clients não têm campos sensíveis a serem encriptados, então SQL é suficiente; aproveita o `MultiLineSqlScriptExtractor` configurado no Passo 3). Três clients first-party:
  - **`my-client-id`** (id `00000000-0000-0000-0000-000000000001`) — aplicação Next.js de catálogo de livros (a ser implementada). Authorization Code + Refresh Token, scopes `openid,profile,email`, callback `http://localhost:3000/api/auth/callback/dsbooks-as`, TTL access curto (60s) para didaticamente exercitar o refresh, refresh 30 dias, sem consent.
  - **`my-backend`** (id `00000000-0000-0000-0000-000000000010`) — backend Spring de catálogo de livros (a ser implementado). Client Credentials apenas, scope `openid`, **somente** `client_secret_basic` (padrão canônico para m2m), TTL 500s, sem redirect.
  - **`manual-client`** (id `00000000-0000-0000-0000-000000000099`) — client de testes manuais no Postman. Authorization Code + Refresh Token, scopes `openid,profile,email`, callback `https://oauth.pstmn.io/v1/callback`, TTL access 1h, sem consent. **Não tem `client_credentials`** — para testar fluxo m2m, o Postman usa as credenciais do `my-backend` separadamente (ver atualizações na collection abaixo).

**Por que três clients separados:** representam três atores reais com requisitos distintos:

- Cada client tem seu próprio `redirect_uri` legítimo. Manter `manual-client` separado evita whitelisting do callback do Postman em um client de produção (`my-client-id`) — vetor de ataque clássico.
- `my-backend` (Client Credentials) reflete chamadas que o backend faz **sem usuário** — introspecção/revogação de tokens, jobs internos, m2m com outros serviços.
- A separação fica clara em log/auditoria via `aud`: `my-client-id` = tráfego de usuário real; `manual-client` = teste manual; `my-backend` = m2m.

**Atualizações no Postman** (acompanham este passo):

- Environment `dsbooks-dev` ganha **três novas variáveis** para o fluxo m2m (assim o desenvolvedor não precisa editar `client-id`/`client-secret` ao alternar entre fluxos):
  - `backend-client-id = my-backend`
  - `backend-client-secret = my-client-secret` (`type: secret`)
  - `backend-access-token` (vazio inicialmente — preenchido pelo script da request abaixo)

- Collection ganha nova requisição **`POST Token (Client Credentials)`** dentro da pasta `Auth`:
  - `POST {{as-host}}/oauth2/token`
  - Auth: **HTTP Basic** com `username = {{backend-client-id}}`, `password = {{backend-client-secret}}` (porque `my-backend` aceita só `client_secret_basic`).
  - Body (form-urlencoded): `grant_type=client_credentials`, `scope=openid`.
  - Test script salva `access_token` da resposta em `backend-access-token` no environment.
  - Para usar o token em uma request protegida estilo m2m, o desenvolvedor sobrescreve a Authorization da request específica para `Bearer {{backend-access-token}}`.

**O que sai do projeto:**

- Bean `registeredClientRepository()` (in-memory) e o `RegisteredClient` hardcoded de `manual-client` — removidos de `AuthorizationServerConfig`. Substituídos pelos três clients no `import.sql`.
- 7 imports da Spring Authorization Server (`AuthorizationGrantType`, `ClientAuthenticationMethod`, `OidcScopes`, `InMemoryRegisteredClientRepository`, `RegisteredClient`, `RegisteredClientRepository`, `ClientSettings`) — não usados mais em `AuthorizationServerConfig`.

**O que continua em `AuthorizationServerConfig`:**

- `PasswordEncoder` (validação de senhas de usuário).
- Os dois `SecurityFilterChain` (AS endpoints + default).
- `JWKSource`, `JwtDecoder`, `AuthorizationServerSettings`.

**Entrega esperada:**

- `mvnw test` verde — contexto sobe carregando o `CustomRegisteredClientRepository` e os 3 clients do banco.
- `SELECT * FROM tb_oauth_client` no H2 console mostra as 3 linhas.
- `manual-client` continua respondendo ao "Get New Access Token" da collection (Authorization Code + PKCE).
- A nova requisição `POST Token (Client Credentials)` retorna HTTP 200 com JWT cujo payload tem `sub: my-backend`, `aud: my-backend`, `iss: http://localhost:9000`, `scope: ["openid"]`.

**Por que neste momento:** com usuários já no banco (Passo 3), trazer os clients para o banco é a continuação natural — e prepara o terreno para os próximos projetos da trilha (a app Next de catálogo e o backend Spring de catálogo) já terem seus clients prontos para uso.

**Entrega esperada:** o AS levanta normalmente, agora carregando os três clients do banco. O `manual-client` permite reproduzir o fluxo de Authorization Code do Postman exatamente como antes.

---

## Passo 5 — Redesign do usuário conforme OIDC

**Objetivo:** redesenhar o modelo de usuário para refletir os scopes do OIDC, e garantir que **email e qualquer outra informação sensível NÃO transitem nem pelo access token nem pelo ID token** — só são expostos via `/userinfo` com Bearer token.

**Novo modelo de `UserEntity`:**

- **Campos internos do banco:**
  - `id` (PK, `Long`, autoincremental)
  - `password` (not null)

- **Scope `openid`:**
  - `sub` (not null, valor UUID — substitui o uso anterior de `email` como `sub`)

- **Scope `profile`:**
  - `name`
  - `picture`

- **Scope `email`:**
  - `email` (not null)
  - `email_verified` (not null, valor padrão `false`)

**Mudanças complementares:**

- O `UserDetailsService` customizado passa a usar o `email` como identificador de login (campo do formulário), mas o `sub` exposto nos tokens é o UUID, **não** o email.
- Configurar um `OAuth2TokenCustomizer<JwtEncodingContext>` (ou equivalente) para garantir que o ID token e o access token contenham apenas o `sub` (UUID) e os claims estritamente necessários — sem `email`, `name`, `picture`.
- Os claims `email`, `email_verified`, `name`, `picture` ficam disponíveis apenas no endpoint `/userinfo`, retornados conforme os scopes aprovados pelo cliente.
- Seed inicial atualizado para gerar UUIDs e popular os novos campos.

**Por que este é o último passo da aula:** consolida o modelo OIDC correto e corrige uma **violação de LGPD/GDPR** que existia desde a baseline — onde o `sub` era o próprio email do usuário, e dados pessoais transitavam em tokens que circulam por sistemas intermediários.

**Entrega esperada:**

- Usuário faz login com email/senha.
- O ID token e o access token contêm `sub` = UUID, sem dados pessoais.
- Uma chamada `GET /userinfo` com Bearer token retorna `sub`, `name`, `picture`, `email`, `email_verified` conforme os scopes (`openid`, `profile`, `email`) aprovados.
