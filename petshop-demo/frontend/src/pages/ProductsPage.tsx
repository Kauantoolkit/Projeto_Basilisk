import { useState, useEffect, useCallback } from "react";
import { Button, DataTable, Modal, Input, Select, EmptyState } from "@basilisk/ui";
import { useDebounce } from "@basilisk/hooks";
import { formatCurrency } from "@basilisk/utils";
import { Package } from "lucide-react";
import { productApi, type Product } from "../services/api";

const CATEGORIES = [
  { value: "alimentacao", label: "Alimentacao" },
  { value: "brinquedos", label: "Brinquedos" },
  { value: "higiene", label: "Higiene" },
  { value: "acessorios", label: "Acessorios" },
  { value: "medicamentos", label: "Medicamentos" },
];

const columns = [
  { accessor: "name" as const, header: "Nome" },
  { accessor: "description" as const, header: "Descricao" },
  { accessor: "category" as const, header: "Categoria" },
  {
    accessor: (row: Product) => formatCurrency(row.price),
    header: "Preco",
    align: "right" as const,
  },
];

export function ProductsPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [search, setSearch] = useState("");
  const debouncedSearch = useDebounce(search, 300);
  const [form, setForm] = useState({ name: "", description: "", price: "", category: "alimentacao" });
  const [saving, setSaving] = useState(false);

  const loadProducts = useCallback(async () => {
    setLoading(true);
    try {
      const res = await productApi.list();
      setProducts(res.data.data);
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadProducts(); }, [loadProducts]);

  const filtered = products.filter((p) =>
    p.name.toLowerCase().includes(debouncedSearch.toLowerCase()) ||
    p.category.toLowerCase().includes(debouncedSearch.toLowerCase())
  );

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault();
    if (!form.name || !form.price) return;
    setSaving(true);
    try {
      await productApi.create({
        name: form.name,
        description: form.description,
        price: parseFloat(form.price),
        category: form.category,
      });
      setModalOpen(false);
      setForm({ name: "", description: "", price: "", category: "alimentacao" });
      loadProducts();
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(product: Product) {
    if (!confirm(`Excluir "${product.name}"?`)) return;
    await productApi.delete(product.id);
    loadProducts();
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-semibold" style={{ color: "var(--bsk-text)" }}>
            Produtos
          </h2>
          <p className="text-xs mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
            {products.length} produto{products.length !== 1 ? "s" : ""} cadastrado{products.length !== 1 ? "s" : ""}
          </p>
        </div>
        <Button variant="primary" onClick={() => setModalOpen(true)}>+ Novo Produto</Button>
      </div>

      <div className="w-80">
        <Input
          placeholder="Buscar por nome ou categoria..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          fullWidth
        />
      </div>

      {!loading && products.length === 0 ? (
        <EmptyState
          icon={<Package size={24} strokeWidth={1.8} />}
          title="Nenhum produto cadastrado"
          description="Adicione o primeiro produto para comecar a gerenciar o estoque."
          action={<Button variant="primary" onClick={() => setModalOpen(true)}>+ Novo Produto</Button>}
        />
      ) : (
        <DataTable
          columns={[
            ...columns,
            {
              accessor: (row: Product) => (
                <Button
                  variant="danger"
                  size="sm"
                  onClick={(e) => { e.stopPropagation(); handleDelete(row); }}
                >
                  Excluir
                </Button>
              ),
              header: "",
              align: "right" as const,
            },
          ]}
          data={filtered}
          rowKey="id"
          isLoading={loading}
          emptyMessage="Nenhum produto encontrado."
        />
      )}

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title="Novo Produto"
        size="md"
        footer={
          <>
            <Button variant="outline" onClick={() => setModalOpen(false)}>Cancelar</Button>
            <Button variant="primary" isLoading={saving} onClick={handleCreate}>Salvar</Button>
          </>
        }
      >
        <form onSubmit={handleCreate} className="space-y-4">
          <Input label="Nome" placeholder="Nome do produto" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} fullWidth />
          <Input label="Descricao" placeholder="Descricao breve" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} fullWidth />
          <Input label="Preco (R$)" type="number" step="0.01" placeholder="0,00" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} fullWidth />
          <Select label="Categoria" options={CATEGORIES} value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} fullWidth />
        </form>
      </Modal>
    </div>
  );
}
