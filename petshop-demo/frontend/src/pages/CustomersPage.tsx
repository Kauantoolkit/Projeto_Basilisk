import { useState, useEffect, useCallback } from "react";
import { Button, DataTable, Modal, Input, EmptyState } from "@basilisk/ui";
import { useDebounce } from "@basilisk/hooks";
import { Users } from "lucide-react";
import { customerApi, type Customer } from "../services/api";

const columns = [
  { accessor: "name" as const, header: "Nome" },
  { accessor: "email" as const, header: "Email" },
  { accessor: "phone" as const, header: "Telefone" },
  { accessor: "address" as const, header: "Endereco" },
];

const emptyForm = { name: "", email: "", phone: "", address: "" };

export function CustomersPage() {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [search, setSearch] = useState("");
  const debouncedSearch = useDebounce(search, 300);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await customerApi.list();
      setCustomers(res.data.data);
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const filtered = customers.filter((c) =>
    c.name.toLowerCase().includes(debouncedSearch.toLowerCase()) ||
    c.email.toLowerCase().includes(debouncedSearch.toLowerCase())
  );

  function openCreate() {
    setEditingId(null);
    setForm(emptyForm);
    setModalOpen(true);
  }

  function openEdit(customer: Customer) {
    setEditingId(customer.id);
    setForm({ name: customer.name, email: customer.email, phone: customer.phone, address: customer.address });
    setModalOpen(true);
  }

  async function handleSave(e: React.FormEvent) {
    e.preventDefault();
    if (!form.name || !form.email) return;
    setSaving(true);
    try {
      if (editingId) {
        await customerApi.update(editingId, form);
      } else {
        await customerApi.create(form);
      }
      setModalOpen(false);
      setForm(emptyForm);
      setEditingId(null);
      load();
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(customer: Customer) {
    if (!confirm(`Excluir cliente "${customer.name}"?`)) return;
    await customerApi.delete(customer.id);
    load();
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-semibold" style={{ color: "var(--bsk-text)" }}>
            Clientes
          </h2>
          <p className="text-xs mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
            {customers.length} cliente{customers.length !== 1 ? "s" : ""} cadastrado{customers.length !== 1 ? "s" : ""}
          </p>
        </div>
        <Button variant="primary" onClick={openCreate}>+ Novo Cliente</Button>
      </div>

      <div className="w-80">
        <Input
          placeholder="Buscar por nome ou email..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          fullWidth
        />
      </div>

      {!loading && customers.length === 0 ? (
        <EmptyState
          icon={<Users size={24} strokeWidth={1.8} />}
          title="Nenhum cliente cadastrado"
          description="Adicione o primeiro cliente para comecar a gerenciar seus contatos."
          action={<Button variant="primary" onClick={openCreate}>+ Novo Cliente</Button>}
        />
      ) : (
        <DataTable
          columns={[
            ...columns,
            {
              accessor: (row: Customer) => (
                <div className="flex gap-2 justify-end">
                  <Button variant="outline" size="sm" onClick={(e) => { e.stopPropagation(); openEdit(row); }}>
                    Editar
                  </Button>
                  <Button variant="danger" size="sm" onClick={(e) => { e.stopPropagation(); handleDelete(row); }}>
                    Excluir
                  </Button>
                </div>
              ),
              header: "",
              align: "right" as const,
            },
          ]}
          data={filtered}
          rowKey="id"
          isLoading={loading}
          emptyMessage="Nenhum cliente encontrado."
        />
      )}

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editingId ? "Editar Cliente" : "Novo Cliente"}
        size="md"
        footer={
          <>
            <Button variant="outline" onClick={() => setModalOpen(false)}>Cancelar</Button>
            <Button variant="primary" isLoading={saving} onClick={handleSave}>Salvar</Button>
          </>
        }
      >
        <form onSubmit={handleSave} className="space-y-4">
          <Input label="Nome" placeholder="Nome completo" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} fullWidth />
          <Input label="Email" type="email" placeholder="email@exemplo.com" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} fullWidth />
          <Input label="Telefone" placeholder="(00) 00000-0000" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} fullWidth />
          <Input label="Endereco" placeholder="Rua, numero, cidade" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} fullWidth />
        </form>
      </Modal>
    </div>
  );
}
