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

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
  active: boolean;
}

export interface Customer {
  id: string;
  name: string;
  email: string;
  phone: string;
  address: string;
  active: boolean;
}

export interface Pet {
  id: string;
  name: string;
  species: string;
  breed: string;
  age: number | null;
  weight: number | null;
  ownerId: string;
  owner?: Customer;
  status: string;
  imageUrl: string | null;
  active: boolean;
}

export interface ServiceOrder {
  id: string;
  customerId: string;
  customer?: Customer;
  petId: string;
  pet?: Pet;
  type: string;
  description: string;
  scheduledDate: string;
  completedDate: string | null;
  status: string;
  price: number;
  active: boolean;
}

export interface AuthResponse {
  name: string;
  token: string;
}

type ApiRes<T> = { success: boolean; data: T };

export const authApi = {
  login: (email: string, password: string) =>
    api.post<ApiRes<AuthResponse>>("/auth/login", { email, password }),
  register: (name: string, email: string, password: string) =>
    api.post<ApiRes<AuthResponse>>("/auth/register", { name, email, password }),
};

export const productApi = {
  list: () => api.get<ApiRes<Product[]>>("/products"),
  getById: (id: string) => api.get<ApiRes<Product>>(`/products/${id}`),
  create: (product: Omit<Product, "id" | "active">) =>
    api.post<ApiRes<Product>>("/products", product),
  delete: (id: string) => api.delete(`/products/${id}`),
};

export const customerApi = {
  list: () => api.get<ApiRes<Customer[]>>("/customers"),
  getById: (id: string) => api.get<ApiRes<Customer>>(`/customers/${id}`),
  create: (customer: Omit<Customer, "id" | "active">) =>
    api.post<ApiRes<Customer>>("/customers", customer),
  update: (id: string, customer: Partial<Customer>) =>
    api.put<ApiRes<Customer>>(`/customers/${id}`, customer),
  delete: (id: string) => api.delete(`/customers/${id}`),
};

export const petApi = {
  list: () => api.get<ApiRes<Pet[]>>("/pets"),
  getById: (id: string) => api.get<ApiRes<Pet>>(`/pets/${id}`),
  listByOwner: (ownerId: string) => api.get<ApiRes<Pet[]>>(`/pets/owner/${ownerId}`),
  create: (pet: Omit<Pet, "id" | "active" | "owner">) =>
    api.post<ApiRes<Pet>>("/pets", pet),
  update: (id: string, pet: Partial<Pet>) =>
    api.put<ApiRes<Pet>>(`/pets/${id}`, pet),
  updateStatus: (id: string, status: string) =>
    api.put<ApiRes<Pet>>(`/pets/${id}/status`, { status }),
  delete: (id: string) => api.delete(`/pets/${id}`),
};

export const orderApi = {
  list: () => api.get<ApiRes<ServiceOrder[]>>("/orders"),
  getById: (id: string) => api.get<ApiRes<ServiceOrder>>(`/orders/${id}`),
  listByCustomer: (customerId: string) => api.get<ApiRes<ServiceOrder[]>>(`/orders/customer/${customerId}`),
  create: (order: Omit<ServiceOrder, "id" | "active" | "customer" | "pet" | "completedDate">) =>
    api.post<ApiRes<ServiceOrder>>("/orders", order),
  update: (id: string, order: Partial<ServiceOrder>) =>
    api.put<ApiRes<ServiceOrder>>(`/orders/${id}`, order),
  updateStatus: (id: string, status: string) =>
    api.put<ApiRes<ServiceOrder>>(`/orders/${id}/status`, { status }),
  delete: (id: string) => api.delete(`/orders/${id}`),
};
