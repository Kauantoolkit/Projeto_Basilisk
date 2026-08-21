import { useCallback, useEffect, useState } from "react";
import { DataTable, Card, useToast, type Column, Select, StatusBadge } from "@basilisk/ui";
import { formatCurrency, formatDate } from "@basilisk/utils";
import { CheckCircle2, XCircle } from "lucide-react";
import { paymentApi, type Payment, type PaymentMethod, type PaymentStatus } from "../services/api";

const STATUS_MAP: Record<PaymentStatus, { label: string; color: "success" | "warning" | "danger" | "neutral" | "info" }> = {
  PAID: { label: "Pago", color: "success" },
  PENDING: { label: "Pendente", color: "warning" },
  OVERDUE: { label: "Atrasado", color: "danger" },
  CANCELLED: { label: "Cancelado", color: "neutral" },
};

export function PaymentsPage() {
  const { success, error } = useToast();
  const [payments, setPayments] = useState<Payment[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<PaymentStatus | "">("");
  const [payingId, setPayingId] = useState<string | null>(null);
  const [method, setMethod] = useState<PaymentMethod>("PIX");

  const load = useCallback(() => {
    setLoading(true);
    paymentApi.list(filter || undefined)
      .then((res) => setPayments(res.data.data))
      .catch(() => error("Erro ao carregar pagamentos"))
      .finally(() => setLoading(false));
  }, [filter, error]);

  useEffect(() => { load(); }, [load]);

  async function handlePay(p: Payment) {
    if (!confirm(`Receber ${formatCurrency(p.amount)} de ${p.clientName} via ${method}?`)) return;
    setPayingId(p.id);
    try {
      await paymentApi.pay(p.id, method);
      success("Pagamento recebido");
      load();
    } catch {
      error("Erro ao registrar pagamento");
    } finally {
      setPayingId(null);
    }
  }

  async function handleCancel(p: Payment) {
    if (!confirm("Cancelar este pagamento?")) return;
    try {
      await paymentApi.remove(p.id);
      success("Pagamento cancelado");
      load();
    } catch {
      error("Erro ao cancelar pagamento");
    }
  }

  const columns: Column<Payment>[] = [
    { accessor: "clientName", header: "Cliente" },
    { accessor: (p) => p.planName ?? "Avulso", header: "Plano" },
    { accessor: (p) => formatCurrency(p.amount), header: "Valor", align: "right" },
    { accessor: (p) => formatDate(p.dueDate), header: "Vencimento" },
    { accessor: (p) => (p.paidDate ? formatDate(p.paidDate) : "—"), header: "Pago em" },
    { accessor: (p) => p.method ?? "—", header: "Metodo" },
    { accessor: (p) => <StatusBadge status={p.status} statusMap={STATUS_MAP} size="sm" />, header: "Status" },
    {
      accessor: (p) => (
        <div className="flex gap-2 justify-end">
          {(p.status === "PENDING" || p.status === "OVERDUE") && (
            <button
              onClick={() => handlePay(p)}
              title="Receber"
              style={{ color: "#16a34a" }}
              disabled={payingId === p.id}
            >
              <CheckCircle2 size={16} strokeWidth={1.8} />
            </button>
          )}
          {(p.status === "PENDING" || p.status === "OVERDUE") && (
            <button onClick={() => handleCancel(p)} title="Cancelar" style={{ color: "var(--bsk-danger)" }}>
              <XCircle size={16} strokeWidth={1.8} />
            </button>
          )}
        </div>
      ),
      header: "Acoes",
      align: "right",
    },
  ];

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight" style={{ color: "var(--bsk-text)" }}>
            Pagamentos
          </h1>
          <p className="text-sm mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
            Mensalidades geradas pelas assinaturas
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Select
            placeholder="Filtrar status"
            options={[
              { value: "", label: "Todos" },
              { value: "PENDING", label: "Pendentes" },
              { value: "OVERDUE", label: "Atrasados" },
              { value: "PAID", label: "Pagos" },
              { value: "CANCELLED", label: "Cancelados" },
            ]}
            value={filter}
            onChange={(e) => setFilter(e.target.value as PaymentStatus | "")}
          />
          <Select
            placeholder="Metodo"
            options={[
              { value: "PIX", label: "PIX" },
              { value: "CARD", label: "Cartao" },
              { value: "CASH", label: "Dinheiro" },
              { value: "TRANSFER", label: "Transferencia" },
            ]}
            value={method}
            onChange={(e) => setMethod(e.target.value as PaymentMethod)}
          />
        </div>
      </div>

      <Card noPadding>
        <DataTable columns={columns} data={payments} rowKey="id" isLoading={loading} />
      </Card>
    </div>
  );
}