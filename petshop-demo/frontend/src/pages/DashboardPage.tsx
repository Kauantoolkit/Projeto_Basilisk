import { useState, useEffect } from "react";
import {
  DollarSign,
  Users,
  PawPrint,
  ClipboardList,
  Package,
  Clock,
  TrendingUp,
  Home,
} from "lucide-react";
import { productApi, customerApi, petApi, orderApi } from "../services/api";
import type { Product, Customer, Pet, ServiceOrder } from "../services/api";

function formatBRL(value: number) {
  return value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

export function DashboardPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [pets, setPets] = useState<Pet[]>([]);
  const [orders, setOrders] = useState<ServiceOrder[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      productApi.list().then((r) => setProducts(r.data.data)),
      customerApi.list().then((r) => setCustomers(r.data.data)).catch(() => {}),
      petApi.list().then((r) => setPets(r.data.data)).catch(() => {}),
      orderApi.list().then((r) => setOrders(r.data.data)).catch(() => {}),
    ]).finally(() => setLoading(false));
  }, []);

  const completedOrders = orders.filter((o) => o.status === "completed");
  const totalRevenue = completedOrders.reduce((sum, o) => sum + o.price, 0);
  const pendingOrders = orders.filter((o) => o.status === "scheduled" || o.status === "in_progress").length;
  const availablePets = pets.filter((p) => p.status === "available").length;

  const speciesCount: Record<string, number> = {};
  pets.forEach((p) => { speciesCount[p.species] = (speciesCount[p.species] || 0) + 1; });

  const ordersByType: Record<string, number> = {};
  orders.forEach((o) => { ordersByType[o.type] = (ordersByType[o.type] || 0) + 1; });

  const typeLabels: Record<string, string> = {
    grooming: "Banho & Tosa", veterinary: "Veterinario",
    boarding: "Hospedagem", training: "Adestramento",
  };

  const speciesLabels: Record<string, string> = {
    dog: "Cachorro", cat: "Gato", bird: "Passaro", fish: "Peixe", other: "Outro",
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div
          className="w-6 h-6 rounded-full border-2 animate-spin"
          style={{ borderColor: "var(--bsk-border-light)", borderTopColor: "var(--bsk-brand)" }}
        />
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Page title */}
      <div>
        <h2
          className="text-xl font-semibold"
          style={{ color: "var(--bsk-text)" }}
        >
          Dashboard
        </h2>
        <p className="text-xs mt-1" style={{ color: "var(--bsk-text-secondary)" }}>
          Visao geral do petshop
        </p>
      </div>

      {/* Main metric — revenue */}
      <div
        className="rounded-xl p-6"
        style={{
          background: "var(--bsk-surface-raised)",
          border: "1px solid var(--bsk-border-light)",
        }}
      >
        <div className="flex items-center gap-3 mb-3">
          <span
            className="inline-flex items-center justify-center w-9 h-9 rounded-lg"
            style={{
              background: "var(--bsk-brand-subtle)",
              color: "var(--bsk-brand)",
            }}
          >
            <DollarSign size={18} strokeWidth={1.8} />
          </span>
          <span
            className="text-xs font-semibold uppercase"
            style={{ color: "var(--bsk-text-secondary)", letterSpacing: "0.07em" }}
          >
            Receita total
          </span>
        </div>
        <p
          className="text-4xl font-semibold"
          style={{
            color: "var(--bsk-text)",
            fontVariantNumeric: "tabular-nums",
          }}
        >
          {formatBRL(totalRevenue)}
        </p>
        <p className="text-xs mt-2" style={{ color: "var(--bsk-text-secondary)" }}>
          de {completedOrders.length} pedido{completedOrders.length !== 1 ? "s" : ""} concluido{completedOrders.length !== 1 ? "s" : ""}
        </p>
      </div>

      {/* Secondary metrics — compact row */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        {[
          { icon: <Users size={16} strokeWidth={1.8} />, label: "Clientes", value: customers.length },
          { icon: <PawPrint size={16} strokeWidth={1.8} />, label: "Pets", value: pets.length, context: `${availablePets} disponive${availablePets !== 1 ? "is" : "l"}` },
          { icon: <ClipboardList size={16} strokeWidth={1.8} />, label: "Pedidos pendentes", value: pendingOrders, warn: pendingOrders > 0 },
          { icon: <Package size={16} strokeWidth={1.8} />, label: "Produtos", value: products.length },
        ].map(({ icon, label, value, context, warn }) => (
          <div
            key={label}
            className="rounded-xl p-4 flex items-center gap-3"
            style={{
              background: "var(--bsk-surface-raised)",
              border: "1px solid var(--bsk-border-light)",
            }}
          >
            <span
              className="inline-flex items-center justify-center w-8 h-8 rounded-lg shrink-0"
              style={{
                background: "var(--bsk-surface-hover)",
                color: "var(--bsk-text-secondary)",
              }}
            >
              {icon}
            </span>
            <div className="min-w-0">
              <span
                className="text-xs font-semibold uppercase block"
                style={{ color: "var(--bsk-text-secondary)", letterSpacing: "0.07em" }}
              >
                {label}
              </span>
              <span
                className="text-2xl font-semibold"
                style={{
                  color: warn ? "var(--bsk-warning)" : "var(--bsk-text)",
                  fontVariantNumeric: "tabular-nums",
                }}
              >
                {value}
              </span>
              {context && (
                <span className="text-xs block" style={{ color: "var(--bsk-text-secondary)" }}>
                  {context}
                </span>
              )}
            </div>
          </div>
        ))}
      </div>

      {/* Breakdowns */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Pets by species */}
        <div
          className="rounded-xl p-5"
          style={{ background: "var(--bsk-surface-raised)", border: "1px solid var(--bsk-border-light)" }}
        >
          <div className="flex items-center gap-2 mb-4">
            <PawPrint size={16} strokeWidth={1.8} style={{ color: "var(--bsk-text-secondary)" }} />
            <h3
              className="text-xs font-semibold uppercase"
              style={{ color: "var(--bsk-text-secondary)", letterSpacing: "0.07em" }}
            >
              Pets por especie
            </h3>
          </div>
          {Object.entries(speciesCount).length === 0 ? (
            <div className="flex flex-col items-center justify-center py-8 gap-3" style={{ minHeight: 120 }}>
              <span
                className="inline-flex items-center justify-center w-10 h-10 rounded-xl"
                style={{ background: "var(--bsk-surface-hover)", color: "var(--bsk-text-secondary)" }}
              >
                <Home size={20} strokeWidth={1.8} />
              </span>
              <p className="text-sm" style={{ color: "var(--bsk-text-secondary)" }}>
                Nenhum pet cadastrado ainda
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {Object.entries(speciesCount).map(([species, count]) => (
                <div key={species} className="flex items-center justify-between gap-3">
                  <span className="text-sm" style={{ color: "var(--bsk-text)" }}>
                    {speciesLabels[species] || species}
                  </span>
                  <div className="flex items-center gap-3">
                    <div
                      className="w-24 h-1.5 rounded-full overflow-hidden"
                      style={{ background: "var(--bsk-border-light)" }}
                    >
                      <div
                        className="h-full rounded-full"
                        style={{
                          width: `${(count / pets.length) * 100}%`,
                          background: "var(--bsk-brand)",
                        }}
                      />
                    </div>
                    <span
                      className="text-sm font-medium w-6 text-right"
                      style={{ color: "var(--bsk-text)", fontVariantNumeric: "tabular-nums" }}
                    >
                      {count}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Orders by type */}
        <div
          className="rounded-xl p-5"
          style={{ background: "var(--bsk-surface-raised)", border: "1px solid var(--bsk-border-light)" }}
        >
          <div className="flex items-center gap-2 mb-4">
            <TrendingUp size={16} strokeWidth={1.8} style={{ color: "var(--bsk-text-secondary)" }} />
            <h3
              className="text-xs font-semibold uppercase"
              style={{ color: "var(--bsk-text-secondary)", letterSpacing: "0.07em" }}
            >
              Pedidos por tipo
            </h3>
          </div>
          {Object.entries(ordersByType).length === 0 ? (
            <div className="flex flex-col items-center justify-center py-8 gap-3" style={{ minHeight: 120 }}>
              <span
                className="inline-flex items-center justify-center w-10 h-10 rounded-xl"
                style={{ background: "var(--bsk-surface-hover)", color: "var(--bsk-text-secondary)" }}
              >
                <Clock size={20} strokeWidth={1.8} />
              </span>
              <p className="text-sm" style={{ color: "var(--bsk-text-secondary)" }}>
                Nenhum pedido registrado ainda
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {Object.entries(ordersByType).map(([type, count]) => (
                <div key={type} className="flex items-center justify-between gap-3">
                  <span className="text-sm" style={{ color: "var(--bsk-text)" }}>
                    {typeLabels[type] || type}
                  </span>
                  <div className="flex items-center gap-3">
                    <div
                      className="w-24 h-1.5 rounded-full overflow-hidden"
                      style={{ background: "var(--bsk-border-light)" }}
                    >
                      <div
                        className="h-full rounded-full"
                        style={{
                          width: `${(count / orders.length) * 100}%`,
                          background: "var(--bsk-brand)",
                        }}
                      />
                    </div>
                    <span
                      className="text-sm font-medium w-6 text-right"
                      style={{ color: "var(--bsk-text)", fontVariantNumeric: "tabular-nums" }}
                    >
                      {count}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
