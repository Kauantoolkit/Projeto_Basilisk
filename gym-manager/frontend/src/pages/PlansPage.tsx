import { useCallback, useEffect, useState } from "react";
import { Button, Input, Modal, DataTable, Card, useToast, type Column, StatusBadge } from "@basilisk/ui";
import { formatCurrency } from "@basilisk/utils";
import { Plus, Trash2, Pencil } from "lucide-react";
import { planApi, type Plan } from "../services/api";

export function PlansPage() {
  const { success, error } = useToast();
  const [plans, setPlans] = useState<Plan[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Plan | null>(null);
  const [form, setForm] = useState({ name: "", description: "", price: "", durationDays: "30" });
  const [saving, setSaving] = useState(false);

  const load = useCallback(() => {
    setLoading(true);
    planApi.list()
      .then((res) => setPlans(res.data.data))
      .catch(() => error("Erro ao carregar planos"))
      .finally(() => setLoading(false));
  }, [error]);

  useEffect(() => { load(); }, [load]);

  function openCreate() {
    setEditing(null);
    setForm({ name: "", description: "", price: "", durationDays: "30" });
    setModalOpen(true);
  }

  function openEdit(p: Plan) {
    setEditing(p);
    setForm({ name: p.name, description: p.description ?? "", price: String(p.price), durationDays: String(p.durationDays) });
    setModalOpen(true);
  }

  async function handleSave() {
    if (!form.name.trim() || !form.price) return;
    setSaving(true);
    try {
      const payload = {
        name: form.name,
        description: form.description,
        price: Number(form.price),
        durationDays: Number(form.durationDays),
      };
      if (editing) {
        await planApi.update(editing.id, payload);
        success("Plano atualizado");
      } else {
        await planApi.create(payload);
        success("Plano criado");
      }
      setModalOpen(false);
      load();
    } catch {
      error("Erro ao salvar plano");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(p: Plan) {
    if (!confirm(`Excluir plano ${p.name}?`)) return;
    try {
      await planApi.remove(p.id);
      success("Plano excluido");
      load();
    } catch {
      error("Erro ao excluir plano");
    }
  }

  const columns: Column<Plan>[] = [
    { accessor: "name", header: "Plano" },
    { accessor: "description", header: "Descricao" },
    { accessor: (p) => formatCurrency(p.price), header: "Preco", align: "right" },
    { accessor: (p) => `${p.durationDays} dias`, header: "Duracao", align: "center" },
    { accessor: (p) => <StatusBadge status={p.active ? "ACTIVE" : "INACTIVE"} size="sm" />, header: "Status" },
    {
      accessor: (p) => (
        <div className="flex gap-2 justify-end">
          <button onClick={() => openEdit(p)} title="Editar" style={{ color: "var(--bsk-text-secondary)" }}>
            <Pencil size={15} strokeWidth={1.8} />
          </button>
          <button onClick={() => handleDelete(p)} title="Excluir" style={{ color: "var(--bsk-danger)" }}>
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
            Planos
          </h1>
          <p className="text-sm mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
            Diversos planos com precos e duracoes diferentes
          </p>
        </div>
        <Button onClick={openCreate} leftIcon={<Plus size={16} />}>
          Novo plano
        </Button>
      </div>

      <Card noPadding>
        <DataTable columns={columns} data={plans} rowKey="id" isLoading={loading} />
      </Card>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? "Editar plano" : "Novo plano"}
        footer={
          <div className="flex justify-end gap-2">
            <Button variant="ghost" onClick={() => setModalOpen(false)}>Cancelar</Button>
            <Button onClick={handleSave} isLoading={saving}>{editing ? "Salvar" : "Criar"}</Button>
          </div>
        }
      >
        <div className="space-y-4">
          <Input label="Nome *" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} fullWidth />
          <Input label="Descricao" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} fullWidth />
          <Input label="Preco (R$) *" type="number" step="0.01" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} fullWidth />
          <Input label="Duracao (dias) *" type="number" value={form.durationDays} onChange={(e) => setForm({ ...form, durationDays: e.target.value })} fullWidth />
        </div>
      </Modal>
    </div>
  );
}