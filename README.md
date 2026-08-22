# Basilisk

Monorepo do ecossistema **Basilisk** — bibliotecas reutilizáveis que extraio dos meus projetos para não reescrever as mesmas coisas (auditoria, segurança, permissões, UI, autenticação). Reúne libs Java e TypeScript, uma API de referência e apps de demonstração que consomem as libs.

---

## Conteúdo

```
basilisk-libs-java/     bibliotecas Java (Maven multi-módulo)
basilisk-libs/          bibliotecas TypeScript (monorepo)
basilisk-api/           API Spring Boot de referência
gym-manager/            app demo (academia) consumindo as libs
petshop-demo/           app demo (petshop)
Basilisk_docs/          documentação
```

### Bibliotecas Java (`basilisk-libs-java`)

12 módulos, cada um resolvendo uma preocupação transversal:

`core` · `audit` · `security` · `permission` · `user` · `tenant` (multi-tenancy) · `finance` · `notification` · `email` · `storage` · `status` · `web`

### Bibliotecas TypeScript (`basilisk-libs`)

`api` · `auth` · `config` · `hooks` · `schemas` · `ui` · `utils`

Componentes de UI, autenticação, hooks de React, schemas de validação e utilitários compartilhados entre frontends.

---

## Ideia

Em vez de copiar código entre projetos, o que é genérico e reutilizável vira um módulo do Basilisk. Os apps demo (`gym-manager`, `petshop-demo`) existem para exercitar as libs num cenário real — backend em Spring consumindo os módulos Java, frontend consumindo os pacotes TypeScript.

---

## Stack

**Java** Spring Boot · Maven multi-módulo
**TypeScript** monorepo de pacotes (UI, auth, hooks, schemas, utils)
**Demos** Spring Boot + frontend

## Contexto

Projeto pessoal contínuo — a base de código que reaproveito nos meus outros sistemas.
