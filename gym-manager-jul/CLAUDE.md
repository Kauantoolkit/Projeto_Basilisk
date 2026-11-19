# CLAUDE.md — GYM-MANAGER

O workflow generico (memoria, tasks, planejamento, validacao, commit, etc.) esta em
`~/.claude/CLAUDE.md`. As regras de uso do basilisk estao em:
- `~/.claude/basilisk-libs.md` (JS/TS -- **ler antes de tocar no frontend**)
- `~/.claude/basilisk-java.md` (Java -- **ler antes de tocar no backend**)

**REGRA:** antes de implementar qualquer feature neste projeto, ler os catálogos do
basilisk para saber o que ja existe. As regras R1-R8+ de cada um sao OBRIGATORIAS aqui.

---

## IDENTIDADE DO PROJETO

- **Prefixo de task:** `GYM` (ex: `GYM-001`)
- **Stack:** Spring Boot 3.3.5 (Java 21) + Vite/React 18 (TypeScript)
- **Backend:** `gym-manager/` (raiz Maven)
- **Frontend:** `gym-manager/web/`
- **Banco:** PostgreSQL
- **Infra:** Docker Compose (db + backend + frontend/nginx)

---

## ESCOPOS DE MEMORIA

```
memory-back/tasks/GYM-XXX/
memory-front/tasks/GYM-XXX/
memory-back/shared/decisions/
memory-front/shared/decisions/
```

---

## COMANDOS DE BUILD (validacao -- regra 37)

| Tipo | Comando | Criterio |
|------|---------|----------|
| Backend | `mvn clean compile -DskipTests` | zero erros |
| Frontend | `cd web && npm run build` | zero erros (tsc + vite) |
| Docker | `docker compose up --build` | 3 containers saudaveis |

---

## O QUE ESTE PROJETO USA DO BASILISK

### Frontend (basilisk-libs JS/TS)
| Package | Uso |
|---------|-----|
| `@basilisk/api` | `createApiClient()` com `tokenManager` -- `src/lib/api.ts` |
| `@basilisk/auth` | `AuthProvider`, `useAuth()`, `ProtectedRoute`, `tokenManager` |
| `@basilisk/ui` | `Button`, `Input`, `Select`, `Modal`, `Card`, `Badge`, `ToastProvider`, `useToast()` |
| `@basilisk/utils` | `formatCurrency()`, `formatDate()`, `maskCPF()`, `maskPhone()`, `cn()` |
| `@basilisk/hooks` | `useDebounce()` |
| `@basilisk/schemas` | (disponivel, usar quando aplicavel) |

### Backend (basilisk-libs-java)
| Modulo | Uso |
|--------|-----|
| `basilisk-core` | `ApiResponse.ok()`, `BusinessException`, `GlobalExceptionHandler` |
| `basilisk-security` | `JwtService`, `JwtAuthFilter` |
| `basilisk-permission` | (disponivel para RBAC) |
| `basilisk-finance` | `EntryStatus` para pagamentos |

---

## REGRAS ESPECIFICAS DO GYM-MANAGER

### Mutations com Toast (regra R1 do basilisk-libs)
Toda mutation neste projeto DEVE seguir este padrao:

```tsx
const mutation = useMutation(() => service.action(data), {
  onSuccess: () => {
    success('Mensagem de sucesso')
    qc.invalidateQueries('queryKey')
    onClose?.()
  },
  onError: (err: any) => {
    error(err?.response?.data?.message ?? 'Mensagem de erro fallback')
  },
})
```

### Endpoints seguem ApiResponse
Todos os controllers retornam `ApiResponse.ok(data)` -- nunca `ApiResponse.success()`.

### Mapeamento de auth
O backend retorna `AuthResponse` com campos flat. O frontend mapeia para `AuthUser`:
```tsx
const authUser: AuthUser = {
  id: userId,
  name, email,
  tenantId: '', tenantName: 'Gym Manager',
  roleId: role, roleName: role,
}
login(token, '', authUser)
```

---

## CLASSES CENTRAIS -- NAO ALTERAR (regra 23 global)

**Backend:**
- `SecurityConfig.java` (configuracao de seguranca)
- `WebConfig.java` (registro de interceptors)
- `GymApplication.java` (entrypoint)

**Frontend:**
- `src/App.tsx` (providers e rotas)
- `src/lib/api.ts` (instancia do api client)
- `src/components/Layout.tsx` (layout principal)
