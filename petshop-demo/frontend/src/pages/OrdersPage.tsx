import { useState, useEffect, useCallback } from "react";
import { Button, DataTable, Modal, Input, Select, StatusBadge, EmptyState, Tabs } from "@basilisk/ui";
import { useDebounce } from "@basilisk/hooks";
import { formatCurrency } from "@basilisk/utils";
import { ClipboardList } from "lucide-react";
import { orderApi, customerApi, petApi, type ServiceOrder, type Customer, type Pet } from "../services/api";

const ORDER_TYPES = [
  { value: "grooming", label: "Banho & Tosa" },
  { value: "veterinary", label: "Veterinario" },
  { value: "boarding", label: "Hospedagem" },
  { value: "training", label: "Adestramento" },
];

const STATUS_OPTIONS = [
  { value: "scheduled", label: "Agendado" },
  { value: "in_progress", label: "Em andamento" },
  { value: "completed", label: "Concluido" },
  { value: "cancelled", label: "Cancelado" },
];

const orderStatusMap = {
  scheduled: { label: "Agendado", color: "info" as const },
  in_progress: { label: "Em andamento", color: "warning" as const },
  completed: { label: "Concluido", color: "success" as const },
  cancelled: { label: "Cancelado", color: "danger" as const },
};

const typeLabels: Record<string, string> = {
  grooming: "Banho & Tosa", veterinary: "Veterinario",
  boarding: "Hospedagem", training: "Adestramento",
};

const emptyForm = {
  customerId: "", petId: "", type: "grooming",
  description: "", scheduledDate: "", price: "", status: "scheduled",
};

export function OrdersPage() {
  const [orders, setOrders] = useState<ServiceOrder[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [pets, setPets] = useState<Pet[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [search, setSearch] = useState("");
  const debouncedSearch = useDebounce(search, 300);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [activeTab, setActiveTab] = useState("all");

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [ordersRes, custRes, petsRes] = await Promise.all([
        orderApi.list(),
        customerApi.list().catch(() => ({ data: { data: [] as Customer[] } })),
        petApi.list().catch(() => ({ data: { data: [] as Pet[] } })),
      ]);
      setOrders(ordersRes.data.data);
      setCustomers(custRes.data.data);
      setPets(petsRes.data.data);
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const filteredByTab = activeTab === "all" ? orders : orders.filter((o) => o.status === activeTab);
  const filtered = filteredByTab.filter((o) =>
    (typeLabels[o.type] || o.type).toLowerCase().includes(debouncedSearch.toLowerCase()) ||
    o.description.toLowerCase().includes(debouncedSearch.toLowerCase())
  );

  const tabs = [
    { value: "all", label: "Todos", count: orders.length },
    { value: "scheduled", label: "Agendados", count: orders.filter((o) => o.status === "scheduled").length },
    { value: "in_progress", label: "Em andamento", count: orders.filter((o) => o.status === "in_progress").length },
    { value: "completed", label: "Concluidos", count: orders.filter((o) => o.status === "completed").length },
    { value: "cancelled", label: "Cancelados", count: orders.filter((o) => o.status === "cancelled").length },
  ];

  const customerMap = Object.fromEntries(customers.map((c) => [c.id, c.name]));
  const petMap = Object.fromEntries(pets.map((p) => [p.id, p.name]));

  function openCreate() { setEditingId(null); setForm(emptyForm); setModalOpen(true); }

  function openEdit(order: ServiceOrder) {
    setEditingId(order.id);
    setForm({
      customerId: order.customerId, petId: order.petId, type: order.type,
      description: order.description, scheduledDate: order.scheduledDate?.slice(0, 16) || "",
      price: order.price.toString(), status: order.status,
    });
    setModalOpen(true);
  }

  async function handleSave(e: React.FormEvent) {
    e.preventDefault();
    if (!form.customerId || !form.petId || !form.price) return;
    setSaving(true);
    try {
      const data = {
        customerId: form.customerId, petId: form.petId, type: form.type,
        description: form.description, scheduledDate: form.scheduledDate,
        price: parseFloat(form.price), status: form.status,
      };
      if (editingId) await orderApi.update(editingId, data as Partial<ServiceOrder>);
      else await orderApi.create(data as ServiceOrder);
      setModalOpen(false); setForm(emptyForm); setEditingId(null); load();
    } finally { setSaving(false); }
  }

  async function handleDelete(order: ServiceOrder) {
    if (!confirm("Excluir este pedido?")) return;
    await orderApi.delete(order.id); load();
  }

  const customerOptions = [{ value: "", label: "Selecione..." }, ...customers.map((c) => ({ value: c.id, label: c.name }))];
  const petOptions = [{ value: "", label: "Selecione..." }, ...pets.map((p) => ({ value: p.id, label: p.name }))];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-semibold" style={{ color: "var(--bsk-text)" }}>Pedidos de Servico</h2>
          <p className="text-xs mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
            {orders.length} pedido{orders.length !== 1 ? "s" : ""} registrado{orders.length !== 1 ? "s" : ""}
          </p>
        </div>
        <Button variant="primary" onClick={openCreate}>+ Novo Pedido</Button>
      </div>

      <Tabs tabs={tabs} activeTab={activeTab} onTabChange={setActiveTab} />

      <div className="w-80">
        <Input placeholder="Buscar por tipo ou descricao..." value={search} onChange={(e) => setSearch(e.target.value)} fullWidth />
      </div>

      {!loading && orders.length === 0 ? (
        <EmptyState
          icon={<ClipboardList size={24} strokeWidth={1.8} />}
          title="Nenhum pedido registrado"
          description="Crie o primeiro pedido de servico para comecar."
          action={<Button variant="primary" onClick={openCreate}>+ Novo Pedido</Button>}
        />
      ) : (
        <DataTable
          columns={[
            { accessor: (row: ServiceOrder) => typeLabels[row.type] || row.type, header: "Tipo" },
            { accessor: (row: ServiceOrder) => customerMap[row.customerId] || "\u2014", header: "Cliente" },
            { accessor: (row: ServiceOrder) => petMap[row.petId] || "\u2014", header: "Pet" },
            { accessor: (row: ServiceOrder) => row.scheduledDate ? new Date(row.scheduledDate).toLocaleDateString("pt-BR") : "\u2014", header: "Data" },
            { accessor: (row: ServiceOrder) => formatCurrency(row.price), header: "Valor", align: "right" as const },
            { accessor: (row: ServiceOrder) => <StatusBadge status={row.status} statusMap={orderStatusMap} />, header: "Status" },
            {
              accessor: (row: ServiceOrder) => (
                <div className="flex gap-2 justify-end">
                  <Button variant="outline" size="sm" onClick={(e) => { e.stopPropagation(); openEdit(row); }}>Editar</Button>
                  <Button variant="danger" size="sm" onClick={(e) => { e.stopPropagation(); handleDelete(row); }}>Excluir</Button>
                </div>
              ),
              header: "", align: "right" as const,
            },
          ]}
          data={filtered}
          rowKey="id"
          isLoading={loading}
          emptyMessage="Nenhum pedido encontrado."
        />
      )}

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editingId ? "Editar Pedido" : "Novo Pedido"} size="md"
        footer={<><Button variant="outline" onClick={() => setModalOpen(false)}>Cancelar</Button><Button variant="primary" isLoading={saving} onClick={handleSave}>Salvar</Button></>}
      >
        <form onSubmit={handleSave} className="space-y-4">
          <Select label="Cliente" options={customerOptions} value={form.customerId} onChange={(e) => setForm({ ...form, customerId: e.target.value })} fullWidth />
          <Select label="Pet" options={petOptions} value={form.petId} onChange={(e) => setForm({ ...form, petId: e.target.value })} fullWidth />
          <Select label="Tipo de servico" options={ORDER_TYPES} value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })} fullWidth />
          <Input label="Descricao" placeholder="Detalhes do servico" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} fullWidth />
          <Input label="Data agendada" type="datetime-local" value={form.scheduledDate} onChange={(e) => setForm({ ...form, scheduledDate: e.target.value })} fullWidth />
          <Input label="Valor (R$)" type="number" step="0.01" placeholder="0,00" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} fullWidth />
          {editingId && <Select label="Status" options={STATUS_OPTIONS} value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })} fullWidth />}
        </form>
      </Modal>
    </div>
  );
}
