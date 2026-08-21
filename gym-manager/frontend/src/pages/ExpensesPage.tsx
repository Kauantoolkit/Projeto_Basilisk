import { useCallback, useEffect, useState } from "react";
import { Button, Input, Modal, DataTable, Card, useToast, type Column, Select, StatusBadge } from "@basilisk/ui";
import { formatCurrency, formatDate } from "@basilisk/utils";
import { Plus, CheckCircle2, Trash2 } from "lucide-react";
import { expenseApi, type Expense, type ExpenseStatus } from "../services/api";

const STATUS_MAP: Record<ExpenseStatus, { label: string; color: "success" | "warning" | "danger" | "neutral" | "info" }> = {
  PAID: { label: "Pago", color: "success" },
  PENDING: { label: "Pendente", color: "warning" },
  OVERDUE: { label: "Atrasado", color: "danger" },
  CANCELLED: { label: "Cancelado", color: "neutral" },
};

export function ExpensesPage() {
  const { success, error } = useToast();
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<ExpenseStatus | "">("PENDING");
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ description: "", category: "", amount: "", expenseDate: "" });
  const [saving, setSaving] = useState(false);

  const load = useCallback(() => {
    setLoading(true);
    expenseApi.list(filter || undefined)
      .then((res) => setExpenses(res.data.data))
      .catch(() => error("Erro ao carregar despesas"))
      .finally(() => setLoading(false));
  }, [filter, error]);

  useEffect(() => { load(); }, [load]);

  function openCreate() {
    const today = new Date().toISOString().slice(0, 10);
    setForm({ description: "", category: "", amount: "", expenseDate: today });
    setModalOpen(true);
  }

  async function handleSave() {
    if (!form.description.trim() || !form.amount || !form.expenseDate) return;
    setSaving(true);
    try {
      await expenseApi.create({
        description: form.description,
        category: form.category,
        amount: Number(form.amount),
        expenseDate: form.expenseDate,
      });
      success("Despesa criada");
      setModalOpen(false);
      load();
    } catch {
      error("Erro ao criar despesa");
    } finally {
      setSaving(false);
    }
  }

  async function handlePay(e: Expense) {
    try {
      await expenseApi.pay(e.id);
      success("Despesa baixada");
      load();
    } catch {
      error("Erro ao baixar despesa");
    }
  }

  async function handleDelete(e: Expense) {
    if (!confirm(`Excluir despesa ${e.description}?`)) return;
    try {
      await expenseApi.remove(e.id);
      success("Despesa excluida");
      load();
    } catch {
      error("Erro ao excluir despesa");
    }
  }

  const columns: Column<Expense>[] = [
    { accessor: "description", header: "Descricao" },
    { accessor: "category", header: "Categoria" },
    { accessor: (e) => formatCurrency(e.amount), header: "Valor", align: "right" },
    { accessor: (e) => formatDate(e.expenseDate), header: "Data" },
    { accessor: (e) => (e.recurrence ? e.recurrence.frequency : "—"), header: "Recorrencia" },
    { accessor: (e) => <StatusBadge status={e.status} statusMap={STATUS_MAP} size="sm" />, header: "Status" },
    {
      accessor: (e) => (
        <div className="flex gap-2 justify-end">
          {e.status === "PENDING" && (
            <button onClick={() => handlePay(e)} title="Baixar" style={{ color: "#16a34a" }}>
              <CheckCircle2 size={16} strokeWidth={1.8} />
            </button>
          )}
          <button onClick={() => handleDelete(e)} title="Excluir" style={{ color: "var(--bsk-danger)" }}>
            <Trash2 size={15} strokeWidth={1.8} />
          </button>
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
            Despesas
          </h1>
          <p className="text-sm mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
            Controle de gastos da academia
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Select
            placeholder="Filtrar status"
            options={[
              { value: "PENDING", label: "Pendentes" },
              { value: "PAID", label: "Pagas" },
              { value: "CANCELLED", label: "Canceladas" },
            ]}
            value={filter}
            onChange={(e) => setFilter(e.target.value as ExpenseStatus | "")}
          />
          <Button onClick={openCreate} leftIcon={<Plus size={16} />}>
            Nova despesa
          </Button>
        </div>
      </div>

      <Card noPadding>
        <DataTable columns={columns} data={expenses} rowKey="id" isLoading={loading} />
      </Card>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title="Nova despesa"
        footer={
          <div className="flex justify-end gap-2">
            <Button variant="ghost" onClick={() => setModalOpen(false)}>Cancelar</Button>
            <Button onClick={handleSave} isLoading={saving}>Criar</Button>
          </div>
        }
      >
        <div className="space-y-4">
          <Input label="Descricao *" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} fullWidth />
          <Input label="Categoria" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} fullWidth />
          <Input label="Valor (R$) *" type="number" step="0.01" value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} fullWidth />
          <Input label="Data *" type="date" value={form.expenseDate} onChange={(e) => setForm({ ...form, expenseDate: e.target.value })} fullWidth />
        </div>
      </Modal>
    </div>
  );
}