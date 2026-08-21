import { useEffect, useState } from "react";
import { StatsCard, Card } from "@basilisk/ui";
import { formatCurrency } from "@basilisk/utils";
import {
  Wallet,
  ReceiptText,
  TrendingUp,
  Users,
  CreditCard,
  AlertTriangle,
} from "lucide-react";
import { dashboardApi, type DashboardSummary } from "../services/api";

export function DashboardPage() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    dashboardApi.summary()
      .then((res) => setSummary(res.data.data))
      .catch(() => setSummary(null))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-24">
        <div
          className="w-6 h-6 rounded-full border-2 animate-spin"
          style={{ borderColor: "var(--bsk-border-light)", borderTopColor: "var(--bsk-brand)" }}
        />
      </div>
    );
  }

  if (!summary) {
    return <p style={{ color: "var(--bsk-text-secondary)" }}>Nao foi possivel carregar o dashboard.</p>;
  }

  const maxValue = Math.max(...summary.revenueByMonth.map((m) => m.value), 1);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight" style={{ color: "var(--bsk-text)" }}>
          Dashboard
        </h1>
        <p className="text-sm mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
          Visao geral financeira da academia
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatsCard
          label="Receita total"
          value={formatCurrency(summary.totalRevenue)}
          icon={<Wallet size={18} strokeWidth={1.8} />}
        />
        <StatsCard
          label="Despesas"
          value={formatCurrency(summary.totalExpenses)}
          icon={<ReceiptText size={18} strokeWidth={1.8} />}
        />
        <StatsCard
          label="Lucro"
          value={formatCurrency(summary.profit)}
          icon={<TrendingUp size={18} strokeWidth={1.8} />}
        />
        <StatsCard
          label="Em atraso"
          value={`${summary.overdueCount} · ${formatCurrency(summary.overdueAmount)}`}
          icon={<AlertTriangle size={18} strokeWidth={1.8} />}
        />
        <StatsCard
          label="Clientes ativos"
          value={summary.activeClients}
          icon={<Users size={18} strokeWidth={1.8} />}
        />
        <StatsCard
          label="Assinaturas ativas"
          value={summary.activeSubscriptions}
          icon={<CreditCard size={18} strokeWidth={1.8} />}
        />
      </div>

      <Card>
        <Card.Header>
          <h2 className="text-base font-semibold" style={{ color: "var(--bsk-text)" }}>
            Receita vs Despesas — ultimos 6 meses
          </h2>
        </Card.Header>
        <Card.Body>
          <div className="flex items-end gap-3 h-48" style={{ minHeight: "12rem" }}>
            {summary.revenueByMonth.map((m, i) => {
              const exp = summary.expensesByMonth[i]?.value ?? 0;
              const revH = Math.round((m.value / maxValue) * 100);
              const expH = Math.round((exp / maxValue) * 100);
              return (
                <div key={m.month} className="flex-1 flex flex-col items-center gap-2">
                  <div className="flex items-end gap-1 w-full h-full">
                    <div
                      className="flex-1 rounded-t-md transition-all"
                      style={{
                        height: `${revH}%`,
                        background: "var(--bsk-brand)",
                        opacity: 0.85,
                      }}
                      title={`Receita ${m.month}: ${formatCurrency(m.value)}`}
                    />
                    <div
                      className="flex-1 rounded-t-md transition-all"
                      style={{
                        height: `${expH}%`,
                        background: "var(--bsk-danger)",
                        opacity: 0.7,
                      }}
                      title={`Despesas ${m.month}: ${formatCurrency(exp)}`}
                    />
                  </div>
                  <span className="text-xs capitalize" style={{ color: "var(--bsk-text-secondary)" }}>
                    {m.month.replace(".", "")}
                  </span>
                </div>
              );
            })}
          </div>
          <div className="flex gap-6 mt-4 text-xs" style={{ color: "var(--bsk-text-secondary)" }}>
            <span className="flex items-center gap-2">
              <span className="w-3 h-3 rounded-sm" style={{ background: "var(--bsk-brand)" }} /> Receita
            </span>
            <span className="flex items-center gap-2">
              <span className="w-3 h-3 rounded-sm" style={{ background: "var(--bsk-danger)" }} /> Despesas
            </span>
          </div>
        </Card.Body>
      </Card>
    </div>
  );
}