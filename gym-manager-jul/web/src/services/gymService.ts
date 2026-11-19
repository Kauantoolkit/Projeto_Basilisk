import { api } from '../lib/api'
import type {
  ApiResponse, GymClient, GymMetrics, GymPayment, GymPlan,
  Enrollment, EnrollmentStatus, Modality, Page, PaymentMethod,
} from '../types/gym'

// Auth
export const authService = {
  login: (email: string, password: string) =>
    api.post<ApiResponse<{ token: string; userId: string; name: string; email: string; role: string }>>(
      '/auth/login', { email, password }),
  register: (data: { name: string; email: string; password: string; role?: string }) =>
    api.post('/auth/register', data),
}

// Modalities
export const modalityService = {
  list: () => api.get<ApiResponse<Modality[]>>('/gym/modalities'),
  create: (data: { name: string; description?: string }) => api.post('/gym/modalities', data),
  update: (id: string, data: { name: string; description?: string }) => api.put(`/gym/modalities/${id}`, data),
  delete: (id: string) => api.delete(`/gym/modalities/${id}`),
}

// Plans
export const planService = {
  list: () => api.get<ApiResponse<GymPlan[]>>('/gym/plans'),
  create: (data: object) => api.post('/gym/plans', data),
  update: (id: string, data: object) => api.put(`/gym/plans/${id}`, data),
  delete: (id: string) => api.delete(`/gym/plans/${id}`),
}

// Clients
export const clientService = {
  search: (query?: string, page = 0, size = 20) =>
    api.get<ApiResponse<Page<GymClient>>>('/gym/clients', { params: { query, page, size } }),
  getById: (id: string) => api.get<ApiResponse<GymClient>>(`/gym/clients/${id}`),
  create: (data: object) => api.post('/gym/clients', data),
  update: (id: string, data: object) => api.put(`/gym/clients/${id}`, data),
  delete: (id: string) => api.delete(`/gym/clients/${id}`),
}

// Enrollments
export const enrollmentService = {
  listByClient: (clientId: string) =>
    api.get<ApiResponse<Enrollment[]>>(`/gym/enrollments/client/${clientId}`),
  create: (data: object) => api.post('/gym/enrollments', data),
  updateStatus: (id: string, status: EnrollmentStatus) =>
    api.patch(`/gym/enrollments/${id}/status`, null, { params: { status } }),
}

// Payments
export const paymentService = {
  listByEnrollment: (enrollmentId: string) =>
    api.get<ApiResponse<GymPayment[]>>(`/gym/payments/enrollment/${enrollmentId}`),
  listOverdue: () => api.get<ApiResponse<GymPayment[]>>('/gym/payments/overdue'),
  listUpcoming: (days = 7) => api.get<ApiResponse<GymPayment[]>>('/gym/payments/upcoming', { params: { days } }),
  create: (data: object) => api.post('/gym/payments', data),
  markAsPaid: (id: string, paidDate?: string, method?: PaymentMethod) =>
    api.patch(`/gym/payments/${id}/pay`, null, { params: { paidDate, method } }),
  cancel: (id: string) => api.patch(`/gym/payments/${id}/cancel`),
}

// Metrics
export const metricsService = {
  get: () => api.get<ApiResponse<GymMetrics>>('/gym/metrics'),
}
