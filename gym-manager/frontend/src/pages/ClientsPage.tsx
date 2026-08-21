import { useCallback, useEffect, useState } from "react";
import { Button, Input, Modal, DataTable, Card, useToast, type Column } from "@basilisk/ui";
import { formatDate } from "@basilisk/utils";
import { Plus, Trash2, Pencil } from "lucide-react";
import { clientApi, type Client } from "../services/api";

export function ClientsPage() {
  const { success, error } = useToast();
  const [clients, setClients] = useState<Client[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Client | null>(null);
  const [form, setForm] = useState({ name: "", email: "", phone: "", cpf: "", address: "" });
  const [saving, setSaving] = useState(false);

  const load = useCallback(() => {
    setLoading(true);
    clientApi.list(search || undefined)
      .then((res) => setClients(res.data.data))
      .catch(() => error("Erro ao carregar clientes"))
      .finally(() => setLoading(false));
  }, [search, error]);

  useEffect(() => { load(); }, [load]);

  function openCreate() {
    setEditing(null);
    setForm({ name: "", email: "", phone: "", cpf: "", address: "" });
    setModalOpen(true);
  }

  function openEdit(c: Client) {
    setEditing(c);
    setForm({ name: c.name, email: c.email ?? "", phone: c.phone ?? "", cpf: c.cpf ?? "", address: c.address ?? "" });
    setModalOpen(true);
  }

  async function handleSave() {
    if (!form.name.trim()) return;
    setSaving(true);
    try {
      if (editing) {
        await clientApi.update(editing.id, form);
        success("Cliente atualizado");
      } else {
        await clientApi.create(form);
        success("Cliente criado");
      }
      setModalOpen(false);
      load();
    } catch {
      error("Erro ao salvar cliente");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(c: Client) {
    if (!confirm(`Excluir ${c.name}?`)) return;
    try {
      await clientApi.remove(c.id);
      success("Cliente excluido");
      load();
    } catch {
      error("Erro ao excluir cliente");
    }
  }

  const columns: Column<Client>[] = [
    { accessor: "name", header: "Nome" },
    { accessor: "email", header: "Email" },
    { accessor: "phone", header: "Telefone" },
    { accessor: "cpf", header: "CPF" },
    { accessor: (c) => formatDate(c.createdAt), header: "Cadastro" },
    {
      accessor: (c) => (
        <div className="flex gap-2 justify-end">
          <button onClick={() => openEdit(c)} title="Editar" style={{ color: "var(--bsk-text-secondary)" }}>
            <Pencil size={15} strokeWidth={1.8} />
          </button>
          <button onClick={() => handleDelete(c)} title="Excluir" style={{ color: "var(--bsk-danger)" }}>
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
            Clientes
          </h1>
          <p className="text-sm mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
            {clients.length} clientes ativos
          </p>
        </div>
        <Button onClick={openCreate} leftIcon={<Plus size={16} />}>
          Novo cliente
        </Button>
      </div>

      <div className="flex items-center gap-3">
        <Input
          placeholder="Buscar por nome ou email..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          fullWidth
        />
      </div>

      <Card noPadding>
        <DataTable columns={columns} data={clients} rowKey="id" isLoading={loading} />
      </Card>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? "Editar cliente" : "Novo cliente"}
        footer={
          <div className="flex justify-end gap-2">
            <Button variant="ghost" onClick={() => setModalOpen(false)}>Cancelar</Button>
            <Button onClick={handleSave} isLoading={saving}>{editing ? "Salvar" : "Criar"}</Button>
          </div>
        }
      >
        <div className="space-y-4">
          <Input label="Nome *" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} fullWidth />
          <Input label="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} fullWidth />
          <Input label="Telefone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} fullWidth />
          <Input label="CPF" value={form.cpf} onChange={(e) => setForm({ ...form, cpf: e.target.value })} fullWidth />
          <Input label="Endereco" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} fullWidth />
        </div>
      </Modal>
    </div>
  );
}