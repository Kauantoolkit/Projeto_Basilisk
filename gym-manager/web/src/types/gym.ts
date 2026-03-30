export type PaymentMethod = 'CASH' | 'CREDIT_CARD' | 'DEBIT_CARD' | 'PIX' | 'BANK_SLIP' | 'BANK_TRANSFER'
export type EnrollmentStatus = 'ACTIVE' | 'SUSPENDED' | 'CANCELLED' | 'PENDING'
export type PaymentStatus = 'PENDING' | 'PAID' | 'OVERDUE' | 'CANCELLED'
export type Role = 'OWNER' | 'MANAGER' | 'STAFF'

export interface Modality {
  id: string
  name: string
  description: string | null
  active: boolean
  createdAt: string
}

export interface GymPlan {
  id: string
  name: string
  description: string | null
  monthlyPrice: number
  durationMonths: number | null
  paymentDueDay: number
  modalities: Modality[]
  active: boolean
  createdAt: string
}

export interface GymClient {
  id: string
  name: string
  email: string | null
  phone: string | null
  cpf: string | null
  birthDate: string | null
  addressStreet: string | null
  addressNumber: string | null
  addressNeighborhood: string | null
  addressCity: string | null
  addressState: string | null
  addressZipCode: string | null
  active: boolean
  createdAt: string
}

export interface Enrollment {
  id: string
  clientId: string
  clientName: string
  planId: string
  planName: string
  startDate: string
  endDate: string | null
  status: EnrollmentStatus
  preferredPaymentMethod: PaymentMethod
  notes: string | null
  createdAt: string
}

export interface GymPayment {
  id: string
  enrollmentId: string
  clientName: string
  planName: string
  amount: number
  dueDate: string
  paidDate: string | null
  paymentMethod: PaymentMethod
  status: PaymentStatus
  notes: string | null
  createdAt: string
}

export interface GymMetrics {
  totalActiveClients: number
  totalActiveEnrollments: number
  pendingPayments: number
  overduePayments: number
  revenueThisMonth: number
  overdueAmount: number
  projectedMonthlyRevenue: number
}

export interface ApiResponse<T> {
  data: T
  message: string | null
  success: boolean
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
