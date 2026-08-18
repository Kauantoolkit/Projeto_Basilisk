import { useState, useEffect } from "react";
import { AuthProvider, useAuth } from "@basilisk/auth";
import { ToastProvider, Sidebar } from "@basilisk/ui";
import {
  LayoutDashboard,
  Package,
  Users,
  PawPrint,
  ClipboardList,
  LogOut,
  Sun,
  Moon,
} from "lucide-react";
import { LoginPage } from "./pages/LoginPage";
import { DashboardPage } from "./pages/DashboardPage";
import { ProductsPage } from "./pages/ProductsPage";
import { CustomersPage } from "./pages/CustomersPage";
import { PetsPage } from "./pages/PetsPage";
import { OrdersPage } from "./pages/OrdersPage";

type Page = "dashboard" | "products" | "customers" | "pets" | "orders";

const NAV_ITEMS = [
  { icon: <LayoutDashboard size={18} strokeWidth={1.8} />, label: "Dashboard", value: "dashboard" },
  { icon: <Package size={18} strokeWidth={1.8} />, label: "Produtos", value: "products" },
  { icon: <Users size={18} strokeWidth={1.8} />, label: "Clientes", value: "customers" },
  { icon: <PawPrint size={18} strokeWidth={1.8} />, label: "Pets", value: "pets" },
  { icon: <ClipboardList size={18} strokeWidth={1.8} />, label: "Pedidos", value: "orders" },
];

const PAGE_MAP: Record<Page, React.FC> = {
  dashboard: DashboardPage,
  products: ProductsPage,
  customers: CustomersPage,
  pets: PetsPage,
  orders: OrdersPage,
};

function AppContent() {
  const { isAuthenticated, isLoading, user, logout } = useAuth();
  const [activePage, setActivePage] = useState<Page>("dashboard");

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div
          className="w-6 h-6 rounded-full border-2 animate-spin"
          style={{
            borderColor: "var(--bsk-border-light)",
            borderTopColor: "var(--bsk-brand)",
          }}
        />
      </div>
    );
  }

  if (!isAuthenticated) return <LoginPage />;

  const ActivePage = PAGE_MAP[activePage];

  return (
    <div className="min-h-screen flex">
      <Sidebar
        items={NAV_ITEMS}
        activeItem={activePage}
        onItemClick={(value) => setActivePage(value as Page)}
        header={
          <div className="flex items-center gap-3">
            <PawPrint size={20} strokeWidth={1.8} style={{ color: "var(--bsk-brand)" }} />
            <span
              className="text-base font-semibold tracking-tight"
              style={{ color: "var(--bsk-text)" }}
            >
              Petshop
            </span>
          </div>
        }
        footer={
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3 min-w-0">
              <div
                className="w-8 h-8 rounded-full flex items-center justify-center shrink-0 text-xs font-semibold"
                style={{
                  background: "var(--bsk-brand-subtle)",
                  color: "var(--bsk-brand)",
                }}
              >
                {user?.name?.charAt(0)?.toUpperCase() || "U"}
              </div>
              <div className="min-w-0">
                <div className="text-sm font-medium truncate" style={{ color: "var(--bsk-text)" }}>
                  {user?.name}
                </div>
                <div className="text-xs truncate" style={{ color: "var(--bsk-text-secondary)" }}>
                  Operador
                </div>
              </div>
            </div>
            <button
              onClick={logout}
              className="shrink-0 p-1.5 rounded-md transition-colors"
              style={{ color: "var(--bsk-text-secondary)" }}
              title="Sair"
            >
              <LogOut size={16} strokeWidth={1.8} />
            </button>
          </div>
        }
      />

      <main className="flex-1 overflow-auto">
        <div className="max-w-6xl mx-auto p-6">
          <ActivePage />
        </div>
      </main>
    </div>
  );
}

export default function App() {
  const [dark, setDark] = useState(() => {
    const saved = localStorage.getItem("petshop-theme");
    if (saved) return saved === "dark";
    return window.matchMedia("(prefers-color-scheme: dark)").matches;
  });

  useEffect(() => {
    document.documentElement.classList.toggle("dark", dark);
    localStorage.setItem("petshop-theme", dark ? "dark" : "light");
  }, [dark]);

  return (
    <AuthProvider>
      <ToastProvider>
        <div className="min-h-screen transition-colors duration-200" style={{ background: "var(--bsk-surface)" }}>
          <button
            onClick={() => setDark(!dark)}
            className="fixed top-4 right-4 z-50 w-9 h-9 rounded-lg flex items-center justify-center transition-all duration-150 hover:scale-105 active:scale-95"
            style={{
              background: "var(--bsk-surface-raised)",
              border: "1px solid var(--bsk-border)",
              color: "var(--bsk-text-secondary)",
            }}
            title={dark ? "Modo claro" : "Modo escuro"}
          >
            {dark ? <Sun size={16} strokeWidth={1.8} /> : <Moon size={16} strokeWidth={1.8} />}
          </button>
          <AppContent />
        </div>
      </ToastProvider>
    </AuthProvider>
  );
}
