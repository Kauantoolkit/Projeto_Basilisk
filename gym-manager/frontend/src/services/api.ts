import { createApiClient } from "@basilisk/api";
import { tokenManager } from "@basilisk/auth";

export const api = createApiClient({
  baseURL: "/api",
  getAccessToken: () => tokenManager.getAccessToken(),
  onUnauthorized: () => {
    tokenManager.clearTokens();
    window.location.href = "/";
  },
});

export interface Client {
  id: string;
  name: string;
  email: string | null;
  phone: string | null;
  cpf: string | null;
  birthDate: string | null;
  address: string | null;
  active: boolean;
  createdAt: string;
}

export interface Plan {
  id: string;
  name: string;
  description: string | null;
  price: number;
  durationDays: number;
  active: boolean;
  createdAt: string;
}

export type SubscriptionStatus = "ACTIVE" | "EXPIRED" | "CANCELLED";

export interface Subscription {
  id: string;
  clientId: string;
  clientName: string;
  planId: string;
  planName: string;
  planPrice: number;
  startDate: string;
  endDate: string;
  status: SubscriptionStatus;
  autoRenew: boolean;
  createdAt: string;
}

export type PaymentStatus = "PENDING" | "PAID" | "OVERDUE" | "CANCELLED";
export type PaymentMethod = "PIX" | "CARD" | "CASH" | "TRANSFER";

export interface Payment {
  id: string;
  clientId: string;
  clientName: string;
  subscriptionId: string | null;
  planName: string | null;
  amount: number;
  dueDate: string;
  paidDate: string | null;
  method: PaymentMethod | null;
  status: PaymentStatus;
  createdAt: string;
}

export type ExpenseStatus = "PENDING" | "PAID" | "OVERDUE" | "CANCELLED";

export interface Expense {
  id: string;
  description: string;
  category: string;
  amount: number;
  expenseDate: string;
  recurrence: {
    frequency: string;
    startDate: string | null;
    endDate: string | null;
    dayOfMonth: number | null;
    dayOfWeek: number | null;
    monthOfYear: number | null;
  } | null;
  status: ExpenseStatus;
  createdAt: string;
}

export interface DashboardSummary {
  totalRevenue: number;
  totalExpenses: number;
  profit: number;
  activeClients: number;
  activeSubscriptions: number;
  overdueCount: number;
  overdueAmount: number;
  revenueByMonth: { month: string; value: number }[];
  expensesByMonth: { month: string; value: number }[];
}

type ApiRes<T> = { success: boolean; data: T };

export const authApi = {
  login: (email: string, password: string) =>
    api.post<ApiRes<{ token: string; name: string; email: string }>>("/auth/login", { email, password }),
  register: (name: string, email: string, password: string) =>
    api.post<ApiRes<{ token: string; name: string; email: string }>>("/auth/register", { name, email, password }),
};

export const clientApi = {
  list: (search?: string) =>
    api.get<ApiRes<Client[]>>("/clients", { params: search ? { search } : {} }),
  create: (client: Partial<Client>) => api.post<ApiRes<Client>>("/clients", client),
  update: (id: string, client: Partial<Client>) => api.put<ApiRes<Client>>(`/clients/${id}`, client),
  remove: (id: string) => api.delete(`/clients/${id}`),
};

export const planApi = {
  list: (search?: string) =>
    api.get<ApiRes<Plan[]>>("/plans", { params: search ? { search } : {} }),
  create: (plan: Partial<Plan>) => api.post<ApiRes<Plan>>("/plans", plan),
  update: (id: string, plan: Partial<Plan>) => api.put<ApiRes<Plan>>(`/plans/${id}`, plan),
  remove: (id: string) => api.delete(`/plans/${id}`),
};

export const subscriptionApi = {
  list: (status?: SubscriptionStatus) =>
    api.get<ApiRes<Subscription[]>>("/subscriptions", { params: status ? { status } : {} }),
  create: (data: { clientId: string; planId: string; startDate: string; autoRenew?: boolean }) =>
    api.post<ApiRes<Subscription>>("/subscriptions", data),
  renew: (id: string) => api.post<ApiRes<Subscription>>(`/subscriptions/${id}/renew`),
  cancel: (id: string) => api.post<ApiRes<Subscription>>(`/subscriptions/${id}/cancel`),
};

export const paymentApi = {
  list: (status?: PaymentStatus, clientId?: string) =>
    api.get<ApiRes<Payment[]>>("/payments", { params: { status, clientId } }),
  pay: (id: string, method: PaymentMethod) =>
    api.post<ApiRes<Payment>>(`/payments/${id}/pay`, { method }),
  remove: (id: string) => api.delete(`/payments/${id}`),
};

export const expenseApi = {
  list: (status?: ExpenseStatus) =>
    api.get<ApiRes<Expense[]>>("/expenses", { params: status ? { status } : {} }),
  create: (expense: Partial<Expense>) => api.post<ApiRes<Expense>>("/expenses", expense),
  pay: (id: string) => api.post<ApiRes<Expense>>(`/expenses/${id}/pay`),
  remove: (id: string) => api.delete(`/expenses/${id}`),
};

export const dashboardApi = {
  summary: () => api.get<ApiRes<DashboardSummary>>("/dashboard"),
};