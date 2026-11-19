import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from 'react-query'
import { paymentService } from '../services/gymService'
import { formatCurrency, formatDate } from '@basilisk/utils'
import { Button, Badge, useToast } from '@basilisk/ui'
import { CheckCircle, XCircle } from 'lucide-react'
import type { PaymentStatus } from '../types/gym'

const statusConfig: Record<PaymentStatus, { label: string; variant: 'warning' | 'success' | 'error' | 'default' }> = {
  PENDING: { label: 'Pendente', variant: 'warning' },
  PAID: { label: 'Pago', variant: 'success' },
  OVERDUE: { label: 'Atrasado', variant: 'error' },
  CANCELLED: { label: 'Cancelado', variant: 'default' },
}

type Tab = 'overdue' | 'upcoming'

export default function PaymentsPage() {
  const qc = useQueryClient()
  const { success, error: showError } = useToast()
  const [tab, setTab] = useState<Tab>('overdue')

  const overdueQ = useQuery('overduePayments', paymentService.listOverdue)
  const upcomingQ = useQuery('upcomingPayments', () => paymentService.listUpcoming(30))

  const payments = tab === 'overdue'
    ? (overdueQ.data?.data.data ?? [])
    : (upcomingQ.data?.data.data ?? [])

  const markPaid = useMutation((id: string) => paymentService.markAsPaid(id), {
    onSuccess: () => {
      success('Pagamento marcado como pago')
      qc.invalidateQueries('overduePayments')
      qc.invalidateQueries('upcomingPayments')
    },
    onError: (err: any) => {
      showError(err?.response?.data?.message ?? 'Erro ao marcar pagamento como pago')
    },
  })

  const cancel = useMutation((id: string) => paymentService.cancel(id), {
    onSuccess: () => {
      success('Pagamento cancelado')
      qc.invalidateQueries('overduePayments')
      qc.invalidateQueries('upcomingPayments')
    },
    onError: (err: any) => {
      showError(err?.response?.data?.message ?? 'Erro ao cancelar pagamento')
    },
  })

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold text-gray-800">Pagamentos</h1>

      <div className="flex gap-2">
        {(['overdue', 'upcoming'] as Tab[]).map((t) => (
          <Button key={t} variant={tab === t ? 'primary' : 'outline'} onClick={() => setTab(t)}>
            {t === 'overdue' ? 'Em Atraso' : 'Próximos (30 dias)'}
          </Button>
        ))}
      </div>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
            <tr>
              <th className="px-4 py-3 text-left">Cliente</th>
              <th className="px-4 py-3 text-left">Plano</th>
              <th className="px-4 py-3 text-left">Valor</th>
              <th className="px-4 py-3 text-left">Vencimento</th>
              <th className="px-4 py-3 text-left">Status</th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {payments.map((p) => {
              const cfg = statusConfig[p.status]
              return (
                <tr key={p.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium text-gray-800">{p.clientName}</td>
                  <td className="px-4 py-3 text-gray-600">{p.planName}</td>
                  <td className="px-4 py-3 font-semibold">{formatCurrency(p.amount)}</td>
                  <td className="px-4 py-3 text-gray-600">{formatDate(p.dueDate)}</td>
                  <td className="px-4 py-3">
                    <Badge variant={cfg.variant}>{cfg.label}</Badge>
                  </td>
                  <td className="px-4 py-3 text-right space-x-2">
                    {p.status !== 'PAID' && p.status !== 'CANCELLED' && (
                      <button onClick={() => markPaid.mutate(p.id)}
                        className="text-green-600 hover:text-green-700" title="Marcar como pago">
                        <CheckCircle size={18} />
                      </button>
                    )}
                    {p.status === 'PENDING' && (
                      <button onClick={() => cancel.mutate(p.id)}
                        className="text-red-500 hover:text-red-600" title="Cancelar">
                        <XCircle size={18} />
                      </button>
                    )}
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
        {payments.length === 0 && (
          <p className="text-center text-gray-400 text-sm py-8">Nenhum pagamento encontrado.</p>
        )}
      </div>
    </div>
  )
}
