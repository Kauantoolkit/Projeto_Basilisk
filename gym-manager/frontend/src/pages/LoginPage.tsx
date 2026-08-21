import { useState } from "react";
import { useAuth } from "@basilisk/auth";
import { Button, Input, Card } from "@basilisk/ui";
import { Dumbbell } from "lucide-react";
import { authApi } from "../services/api";
import { AxiosError } from "axios";

export function LoginPage() {
  const { login } = useAuth();
  const [isRegister, setIsRegister] = useState(false);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  function validate(): boolean {
    const errs: Record<string, string> = {};
    if (!email.trim()) errs.email = "Email obrigatorio";
    else if (!/\S+@\S+\.\S+/.test(email)) errs.email = "Email invalido";
    if (!password.trim()) errs.password = "Senha obrigatoria";
    else if (password.length < 4) errs.password = "Minimo 4 caracteres";
    if (isRegister && !name.trim()) errs.name = "Nome obrigatorio";
    setFieldErrors(errs);
    return Object.keys(errs).length === 0;
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    if (!validate()) return;
    setLoading(true);
    try {
      const res = isRegister
        ? await authApi.register(name, email, password)
        : await authApi.login(email, password);
      const data = res.data;
      if (!data.success) { setError("Credenciais invalidas"); return; }
      const { token, name: userName, email: userEmail } = data.data;
      login(token, "", { id: "", name: userName, email: userEmail, role: "admin" });
    } catch (err: unknown) {
      if (err instanceof AxiosError && err.response?.data) {
        const body = err.response.data;
        if (body.errors) setFieldErrors(body.errors);
        else setError(body.message || "Erro ao autenticar");
      } else {
        setError("Erro de conexao");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <Card className="!p-8">
          <div className="text-center mb-8">
            <div
              className="w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-5"
              style={{ background: "var(--bsk-brand-subtle)", color: "var(--bsk-brand)" }}
            >
              <Dumbbell size={28} strokeWidth={1.8} />
            </div>
            <h1
              className="text-2xl font-semibold tracking-tight"
              style={{ color: "var(--bsk-text)" }}
            >
              Gym Manager
            </h1>
            <p className="text-sm mt-2" style={{ color: "var(--bsk-text-secondary)" }}>
              {isRegister ? "Crie sua conta para comecar" : "Bem-vindo de volta"}
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            {isRegister && (
              <Input
                label="Nome"
                placeholder="Seu nome completo"
                value={name}
                onChange={(e) => { setName(e.target.value); setFieldErrors({}); }}
                error={fieldErrors.name}
                fullWidth
              />
            )}
            <Input
              label="Email"
              type="email"
              placeholder="seu@email.com"
              value={email}
              onChange={(e) => { setEmail(e.target.value); setFieldErrors({}); }}
              error={fieldErrors.email}
              fullWidth
            />
            <Input
              label="Senha"
              type="password"
              placeholder="******"
              value={password}
              onChange={(e) => { setPassword(e.target.value); setFieldErrors({}); }}
              error={fieldErrors.password}
              fullWidth
            />

            {error && (
              <div
                className="rounded-lg px-4 py-3 text-sm"
                style={{
                  background: "color-mix(in srgb, var(--bsk-danger) 10%, transparent)",
                  border: "1px solid color-mix(in srgb, var(--bsk-danger) 25%, transparent)",
                  color: "var(--bsk-danger)",
                }}
              >
                {error}
              </div>
            )}

            <div className="pt-1">
              <Button type="submit" fullWidth isLoading={loading} variant="primary">
                {isRegister ? "Criar conta" : "Entrar"}
              </Button>
            </div>
          </form>

          <div className="mt-8 pt-5 text-center" style={{ borderTop: "1px solid var(--bsk-border-light)" }}>
            <p className="text-sm" style={{ color: "var(--bsk-text-secondary)" }}>
              {isRegister ? "Ja tem conta?" : "Nao tem conta?"}{" "}
              <button
                type="button"
                onClick={() => { setIsRegister(!isRegister); setError(""); setFieldErrors({}); }}
                className="font-semibold hover:underline transition-colors"
                style={{ color: "var(--bsk-brand)" }}
              >
                {isRegister ? "Fazer login" : "Criar conta"}
              </button>
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
}