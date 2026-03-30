# System Prompt — Agentes IA em projetos Basilisk

Você está trabalhando em um projeto da **Basilisk Software House**.

---

## REGRA DE OURO

**NUNCA escreva código diretamente no projeto sem antes consultar as bibliotecas Basilisk.**
Isso vale para componentes, hooks, utilitários, schemas, clientes HTTP e lógica de autenticação.

---

## Fluxo obrigatório: BUSCAR → AVALIAR → COMUNICAR → AGIR

### Passo 1 — BUSCAR no `components.json`

Leia o arquivo `basilisk-libs/components.json` na raiz do monorepo.

**Como fazer a busca:**
1. Identifique palavras-chave do que você precisa construir (ex: "tabela de usuários com loading", "campo de CPF com máscara", "notificação de sucesso").
2. Procure nos campos `tags` de cada entry por correspondências, mesmo parciais.
3. Se encontrar candidatos, leia os campos `description`, `whenToUse` e `whenNotToUse` para confirmar o match.
4. Em caso de dúvida, leia o arquivo no campo `path` para inspecionar os tipos e props.

**O que você encontrará:**
- `@basilisk/ui` — componentes visuais (Button, Input, Select, Modal, Card, Badge, DataTable, Toast)
- `@basilisk/hooks` — hooks React (useDebounce, usePagination, useLocalStorage, useMediaQuery, useClickOutside, useAsync)
- `@basilisk/utils` — funções puras (cn, formatCurrency, validateCPF, maskPhone, slugify, etc.)
- `@basilisk/schemas` — schemas Zod (userSchema, loginSchema, addressSchema, paginationSchema, etc.)
- `@basilisk/api` — cliente HTTP (createApiClient, withRetry)
- `@basilisk/auth` — autenticação (AuthProvider, useAuth, tokenManager)

---

### Passo 2 — AVALIAR o resultado da busca

Use a tabela abaixo para classificar o que você encontrou:

| Resultado da busca | Classificação | Ação no Passo 4 |
|--------------------|---------------|-----------------|
| Existe e cobre 100% da necessidade | **USO DIRETO** | Importe e use |
| Existe mas falta 1-2 props/variantes simples | **EXTENSÃO** | Adicione à biblioteca |
| Existe mas a necessidade é diferente o suficiente | **CRIAÇÃO LOCAL** | Crie no projeto |
| Não existe e seria reutilizável em outros projetos | **NOVA LIB** | Crie na biblioteca |
| Não existe e é muito específico deste projeto | **CRIAÇÃO LOCAL** | Crie no projeto |

**Critérios para decidir entre NOVA LIB vs CRIAÇÃO LOCAL:**

| Pergunta | Resposta SIM → | Resposta NÃO → |
|----------|----------------|-----------------|
| Este código pode ser usado em 2+ projetos futuros? | NOVA LIB | CRIAÇÃO LOCAL |
| É um padrão de UI/UX genérico (botão, campo, tabela)? | NOVA LIB | CRIAÇÃO LOCAL |
| É lógica pura sem dependência de contexto do projeto? | NOVA LIB | CRIAÇÃO LOCAL |
| Depende de dados, regras de negócio ou APIs deste projeto? | CRIAÇÃO LOCAL | NOVA LIB |
| É específico demais para ser configurável via props? | CRIAÇÃO LOCAL | NOVA LIB |

---

### Passo 3 — COMUNICAR ao desenvolvedor antes de agir

**Nunca crie ou altere código sem antes comunicar sua classificação.**

Apresente ao desenvolvedor:

```
📦 Análise de componente: [nome do que você precisa]

Classificação: [USO DIRETO | EXTENSÃO | NOVA LIB | CRIAÇÃO LOCAL]

[Se USO DIRETO]:
  ✅ Encontrado: [Nome] em @basilisk/[pacote]
  Atende: [descreva o que atende]
  Import: import { Nome } from '@basilisk/pacote'

[Se EXTENSÃO]:
  ⚙️  Encontrado parcialmente: [Nome] em @basilisk/[pacote]
  Atende: [o que já funciona]
  Falta: [o que precisa ser adicionado — seja específico]
  Proposta: adicionar prop [X] com comportamento [Y]
  Impacto: nenhuma quebra de API existente

[Se NOVA LIB]:
  🆕 Não encontrado. Proposta: criar @basilisk/[pacote]/[NomeComponente]
  Justificativa de reusabilidade: [por que este componente serve outros projetos]
  Interface proposta:
    interface NomeComponenteProps { ... }

[Se CRIAÇÃO LOCAL]:
  📁 Não encontrado. Será criado localmente em:
  [projeto]/components/[NomeComponente]/
  Justificativa de especificidade: [por que não vai para a lib]
```

**Aguarde confirmação do desenvolvedor antes de prosseguir.**

---

### Passo 4 — AGIR conforme a classificação

#### USO DIRETO
```typescript
// Simplesmente importe e use
import { Button } from '@basilisk/ui';
import { useDebounce } from '@basilisk/hooks';
```

---

#### EXTENSÃO de componente existente

1. Edite o arquivo do componente em `basilisk-libs/packages/[pacote]/src/[Componente]/`
2. Adicione a nova prop/variante mantendo retrocompatibilidade (sempre com valor default)
3. Atualize `[Componente].types.ts` com a nova prop documentada via JSDoc
4. Atualize a entrada correspondente em `components.json` (campo props e description)
5. Crie um changeset: `pnpm changeset` → selecione o pacote → descreva a mudança
6. Commit: `feat(@basilisk/ui): add [propName] prop to [Component]`

---

#### NOVA LIB — Criar componente na biblioteca

**Estrutura obrigatória:**
```
packages/[pacote]/src/NomeComponente/
├── NomeComponente.tsx          # forwardRef + CVA para variantes
├── NomeComponente.types.ts     # interface de props com JSDoc em cada prop
├── NomeComponente.test.tsx     # testes cobrindo: render, variantes, acessibilidade
└── index.ts                    # export { NomeComponente } from './NomeComponente'
                                # export type { NomeComponenteProps } from './NomeComponente.types'
```

**Após criar o componente:**
1. Adicione o export no `packages/[pacote]/src/index.ts`
2. Adicione entrada completa em `components.json` (name, description, whenToUse, whenNotToUse, tags, props, example)
3. Crie um changeset: `pnpm changeset`
4. Commit: `feat(@basilisk/ui): add NomeComponente`

**Padrões obrigatórios:**
- Todo componente visual usa `forwardRef`
- Variantes via CVA (class-variance-authority), não ternários inline
- Acessibilidade: `aria-*` adequados, suporte a teclado, `role` quando necessário
- Sem estilos hardcoded — use classes Tailwind e o preset `@basilisk/config/tailwind`
- Props de extensão: sempre aceite `className` e `...rest` (spread de atributos HTML nativos)

---

#### CRIAÇÃO LOCAL — Componente específico do projeto

```
[projeto]/src/components/[NomeComponente]/
├── NomeComponente.tsx
└── index.ts
```

- Use os componentes da biblioteca como base sempre que possível
- Não precisa de Storybook nem changeset
- Comente no topo do arquivo por que é local: `// Específico para [contexto] — não está na lib pois [motivo]`

---

## Checklist de qualidade antes de qualquer entrega

**Para código na biblioteca:**
- [ ] TypeScript compila sem erros (`pnpm exec tsc --noEmit`)
- [ ] ESLint sem warnings (`pnpm lint`)
- [ ] Todos os testes passam (`pnpm test`)
- [ ] Coverage ≥ 80%
- [ ] Export adicionado no `src/index.ts` do pacote
- [ ] Entrada adicionada/atualizada no `components.json`
- [ ] Changeset criado

**Para código local:**
- [ ] TypeScript sem erros
- [ ] Sem import de código local de outros projetos

---

## Imports de referência rápida

```typescript
import { Button, Input, Select, Modal, Card, Badge, DataTable, ToastProvider, useToast } from '@basilisk/ui';
import { useDebounce, usePagination, useLocalStorage, useMediaQuery, useIsMobile, useClickOutside, useAsync } from '@basilisk/hooks';
import { cn, formatCurrency, formatDate, validateCPF, validateCNPJ, maskCPF, maskCNPJ, maskPhone, maskCEP, slugify, truncate } from '@basilisk/utils';
import { userSchema, createUserSchema, addressSchema, loginSchema, registerSchema, paginationQuerySchema, type User, type CreateUser, type LoginInput } from '@basilisk/schemas';
import { createApiClient, withRetry, type ApiError } from '@basilisk/api';
import { AuthProvider, useAuth, tokenManager, type AuthUser } from '@basilisk/auth';
```

---

## Convenções de commit

```
feat:     nova funcionalidade
fix:      correção de bug
docs:     documentação
refactor: refatoração sem mudança de comportamento
test:     adição/correção de testes
chore:    manutenção (deps, config, scripts)

# Com escopo:
feat(@basilisk/ui): add Tooltip component
fix(@basilisk/hooks): fix useLocalStorage SSR hydration
```
