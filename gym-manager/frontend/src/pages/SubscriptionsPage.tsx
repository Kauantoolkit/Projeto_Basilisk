import { useCallback, useEffect, useState } from "react";
import { Button, Input, Modal, DataTable, Card, useToast, type Column, Select, StatusBadge } from "@basilisk/ui";
import { formatCurrency, formatDate } from "@basilisk/utils";
import { Plus, RefreshCcw, XCircle } from "lucide-react";
import { clientApi, planApi, subscriptionApi, type Client, type Plan, type Subscription } from "../services/api";

export function SubscriptionsPage() {
  const { success, error } = useToast();
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
  const [clients, setClients] = useState<Client[]>([]);
  const [plans, setPlans] = useState<Plan[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ clientId: "", planId: "", startDate: "", autoRenew: true });
  const [saving, setSaving] = useState(false);

  const load = useCallback(() => {
    setLoading(true);
    subscriptionApi.list()
      .then((res) => setSubscriptions(res.data.data))
      .catch(() => error("Erro ao carregar assinaturas"))
      .finally(() => setLoading(false));
  }, [error]);

  useEffect(() => {
    load();
    clientApi.list().then((res) => setClients(res.data.data)).catch(() => {});
    planApi.list().then((res) => setPlans(res.data.data)).catch(() => {});
  }, [load]);

  function openCreate() {
    const today = new Date().toISOString().slice(0, 10);
    setForm({ clientId: "", planId: "", startDate: today, autoRenew: true });
    setModalOpen(true);
  }

  async function handleSave() {
    if (!form.clientId || !form.planId || !form.startDate) return;
    setSaving(true);
    try {
      await subscriptionApi.create(form);
      success("Assinatura criada — mensalidades geradas");
      setModalOpen(false);
      load();
    } catch {
      error("Erro ao criar assinatura");
    } finally {
      setSaving(false);
    }
  }

  async function handleRenew(s: Subscription) {
    try {
      await subscriptionApi.renew(s.id);
      success("Assinatura renovada");
      load();
    } catch {
      error("Erro ao renovar assinatura");
    }
  }

  async function handleCancel(s: Subscription) {
    if (!confirm(`Cancelar assinatura de ${s.clientName}?`)) return;
    try {
      await subscriptionApi.cancel(s.id);
      success("Assinatura cancelada");
      load();
    } catch {
      error("Erro ao cancelar assinatura");
    }
  }

  const statusMap = { ACTIVE: { label: "Ativa", color: "success" as const }, EXPIRED: { label: "Expirada", color: "warning" as const }, CANCELLED: { label: "Cancelada", color: "danger" as const } };

  const columns: Column<Subscription>[] = [
    { accessor: "clientName", header: "Cliente" },
    { accessor: "planName", header: "Plano" },
    { accessor: (s) => formatCurrency(s.planPrice), header: "Valor", align: "right" },
    { accessor: (s) => formatDate(s.startDate), header: "Inicio" },
    { accessor: (s) => formatDate(s.endDate), header: "Fim" },
    { accessor: (s) => <StatusBadge status={s.status} statusMap={statusMap} size="sm" />, header: "Status" },
    {
      accessor: (s) => (
        <div className="flex gap-2 justify-end">
          {s.status !== "CANCELLED" && (
            <button onClick={() => handleRenew(s)} title="Renovar" style={{ color: "var(--bsk-text-secondary)" }}>
              <RefreshCcw size={15} strokeWidth={1.8} />
            </button>
          )}
          {s.status === "ACTIVE" && (
            <button onClick={() => handleCancel(s)} title="Cancelar" style={{ color: "var(--bsk-danger)" }}>
              <XCircle size={15} strokeWidth={1.8} />
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
            Assinaturas
          </h1>
          <p className="text-sm mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
            Matriculas com geracao automatica de mensalidades
          </p>
        </div>
        <Button onClick={openCreate} leftIcon={<Plus size={16} />}>
          Nova assinatura
        </Button>
      </div>

      <Card noPadding>
        <DataTable columns={columns} data={subscriptions} rowKey="id" isLoading={loading} />
      </Card>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title="Nova assinatura"
        footer={
          <div className="flex justify-end gap-2">
            <Button variant="ghost" onClick={() => setModalOpen(false)}>Cancelar</Button>
            <Button onClick={handleSave} isLoading={saving}>Criar</Button>
          </div>
        }
      >
        <div className="space-y-4">
          <Select
            label="Cliente *"
            placeholder="Selecione o cliente"
            options={clients.map((c) => ({ value: c.id, label: c.name }))}
            value={form.clientId}
            onChange={(e) => setForm({ ...form, clientId: e.target.value })}
            fullWidth
          />
          <Select
            label="Plano *"
            placeholder="Selecione o plano"
            options={plans.map((p) => ({ value: p.id, label: `${p.name} — ${formatCurrency(p.price)}` }))}
            value={form.planId}
            onChange={(e) => setForm({ ...form, planId: e.target.value })}
            fullWidth
          />
          <Input
            label="Data de inicio *"
            type="date"
            value={form.startDate}
            onChange={(e) => setForm({ ...form, startDate: e.target.value })}
            fullWidth
          />
        </div>
      </Modal>
    </div>
  );
}