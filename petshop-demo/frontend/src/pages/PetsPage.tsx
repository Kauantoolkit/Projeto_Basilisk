import { useState, useEffect, useCallback } from "react";
import { Button, DataTable, Modal, Input, Select, StatusBadge, EmptyState, Tabs } from "@basilisk/ui";
import { useDebounce } from "@basilisk/hooks";
import { PawPrint } from "lucide-react";
import { petApi, customerApi, type Pet, type Customer } from "../services/api";

const SPECIES = [
  { value: "dog", label: "Cachorro" },
  { value: "cat", label: "Gato" },
  { value: "bird", label: "Passaro" },
  { value: "fish", label: "Peixe" },
  { value: "other", label: "Outro" },
];

const STATUS_OPTIONS = [
  { value: "available", label: "Disponivel" },
  { value: "reserved", label: "Reservado" },
  { value: "adopted", label: "Adotado" },
  { value: "in_treatment", label: "Em tratamento" },
];

const petStatusMap = {
  available: { label: "Disponivel", color: "success" as const },
  reserved: { label: "Reservado", color: "warning" as const },
  adopted: { label: "Adotado", color: "info" as const },
  in_treatment: { label: "Em tratamento", color: "danger" as const },
};

const speciesLabels: Record<string, string> = {
  dog: "Cachorro", cat: "Gato", bird: "Passaro", fish: "Peixe", other: "Outro",
};

const emptyForm = { name: "", species: "dog", breed: "", age: "", weight: "", ownerId: "", status: "available", imageUrl: "" };

export function PetsPage() {
  const [pets, setPets] = useState<Pet[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
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
      const [petsRes, custRes] = await Promise.all([
        petApi.list(),
        customerApi.list().catch(() => ({ data: { data: [] } })),
      ]);
      setPets(petsRes.data.data);
      setCustomers(custRes.data.data);
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const filteredByTab = activeTab === "all" ? pets : pets.filter((p) => p.status === activeTab);
  const filtered = filteredByTab.filter((p) =>
    p.name.toLowerCase().includes(debouncedSearch.toLowerCase()) ||
    p.species.toLowerCase().includes(debouncedSearch.toLowerCase())
  );

  const tabs = [
    { value: "all", label: "Todos", count: pets.length },
    { value: "available", label: "Disponiveis", count: pets.filter((p) => p.status === "available").length },
    { value: "reserved", label: "Reservados", count: pets.filter((p) => p.status === "reserved").length },
    { value: "in_treatment", label: "Em tratamento", count: pets.filter((p) => p.status === "in_treatment").length },
    { value: "adopted", label: "Adotados", count: pets.filter((p) => p.status === "adopted").length },
  ];

  function openCreate() { setEditingId(null); setForm(emptyForm); setModalOpen(true); }

  function openEdit(pet: Pet) {
    setEditingId(pet.id);
    setForm({
      name: pet.name, species: pet.species, breed: pet.breed || "",
      age: pet.age?.toString() || "", weight: pet.weight?.toString() || "",
      ownerId: pet.ownerId || "", status: pet.status, imageUrl: pet.imageUrl || "",
    });
    setModalOpen(true);
  }

  async function handleSave(e: React.FormEvent) {
    e.preventDefault();
    if (!form.name) return;
    setSaving(true);
    try {
      const data = {
        name: form.name, species: form.species, breed: form.breed || null,
        age: form.age ? parseInt(form.age) : null, weight: form.weight ? parseFloat(form.weight) : null,
        ownerId: form.ownerId || null, status: form.status, imageUrl: form.imageUrl || null,
      };
      if (editingId) await petApi.update(editingId, data as Partial<Pet>);
      else await petApi.create(data as Pet);
      setModalOpen(false); setForm(emptyForm); setEditingId(null); load();
    } finally { setSaving(false); }
  }

  async function handleDelete(pet: Pet) {
    if (!confirm(`Excluir pet "${pet.name}"?`)) return;
    await petApi.delete(pet.id); load();
  }

  const ownerOptions = [
    { value: "", label: "Sem dono" },
    ...customers.map((c) => ({ value: c.id, label: c.name })),
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-semibold" style={{ color: "var(--bsk-text)" }}>Pets</h2>
          <p className="text-xs mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
            {pets.length} pet{pets.length !== 1 ? "s" : ""} cadastrado{pets.length !== 1 ? "s" : ""}
          </p>
        </div>
        <Button variant="primary" onClick={openCreate}>+ Novo Pet</Button>
      </div>

      <Tabs tabs={tabs} activeTab={activeTab} onTabChange={setActiveTab} />

      <div className="w-80">
        <Input placeholder="Buscar por nome ou especie..." value={search} onChange={(e) => setSearch(e.target.value)} fullWidth />
      </div>

      {!loading && pets.length === 0 ? (
        <EmptyState
          icon={<PawPrint size={24} strokeWidth={1.8} />}
          title="Nenhum pet cadastrado"
          description="Adicione o primeiro pet para comecar o gerenciamento."
          action={<Button variant="primary" onClick={openCreate}>+ Novo Pet</Button>}
        />
      ) : (
        <DataTable
          columns={[
            { accessor: "name" as const, header: "Nome" },
            { accessor: (row: Pet) => speciesLabels[row.species] || row.species, header: "Especie" },
            { accessor: (row: Pet) => row.breed || "\u2014", header: "Raca" },
            { accessor: (row: Pet) => row.age ? `${row.age} anos` : "\u2014", header: "Idade" },
            { accessor: (row: Pet) => <StatusBadge status={row.status} statusMap={petStatusMap} />, header: "Status" },
            {
              accessor: (row: Pet) => (
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
          emptyMessage="Nenhum pet encontrado."
        />
      )}

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editingId ? "Editar Pet" : "Novo Pet"} size="md"
        footer={<><Button variant="outline" onClick={() => setModalOpen(false)}>Cancelar</Button><Button variant="primary" isLoading={saving} onClick={handleSave}>Salvar</Button></>}
      >
        <form onSubmit={handleSave} className="space-y-4">
          <Input label="Nome" placeholder="Nome do pet" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} fullWidth />
          <Select label="Especie" options={SPECIES} value={form.species} onChange={(e) => setForm({ ...form, species: e.target.value })} fullWidth />
          <Input label="Raca" placeholder="Raca (opcional)" value={form.breed} onChange={(e) => setForm({ ...form, breed: e.target.value })} fullWidth />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Idade" type="number" placeholder="Anos" value={form.age} onChange={(e) => setForm({ ...form, age: e.target.value })} fullWidth />
            <Input label="Peso (kg)" type="number" step="0.1" placeholder="0,0" value={form.weight} onChange={(e) => setForm({ ...form, weight: e.target.value })} fullWidth />
          </div>
          <Select label="Dono" options={ownerOptions} value={form.ownerId} onChange={(e) => setForm({ ...form, ownerId: e.target.value })} fullWidth />
          <Select label="Status" options={STATUS_OPTIONS} value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })} fullWidth />
        </form>
      </Modal>
    </div>
  );
}
