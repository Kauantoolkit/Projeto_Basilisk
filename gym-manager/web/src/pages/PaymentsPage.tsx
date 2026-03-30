import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from 'react-query'
import { paymentService } from '../services/gymService'
import { formatCurrency, formatDate } from '../lib/format'
import { CheckCircle, XCircle } from 'lucide-react'
import type { PaymentStatus } from '../types/gym'

const statusLabel: Record<PaymentStatus, string> = {
  PENDING: 'Pendente',
  PAID: 'Pago',
  OVERDUE: 'Atrasado',
  CANCELLED: 'Cancelado',
}

const statusColor: Record<PaymentStatus, string> = {
  PENDING: 'text-yellow-600 bg-yellow-50',
  PAID: 'text-green-600 bg-green-50',
  OVERDUE: 'text-red-600 bg-red-50',
  CANCELLED: 'text-gray-500 bg-gray-100',
}

type Tab = 'overdue' | 'upcoming'

export default function PaymentsPage() {
  const qc = useQueryClient()
  const [tab, setTab] = useState<Tab>('overdue')

  const overdueQ = useQuery('overduePayments', paymentService.listOverdue)
  const upcomingQ = useQuery('upcomingPayments', () => paymentService.listUpcoming(30))

  const payments = tab === 'overdue'
    ? (overdueQ.data?.data.data ?? [])
    : (upcomingQ.data?.data.data ?? [])

  const markPaid = useMutation((id: string) => paymentService.markAsPaid(id), {
    onSuccess: () => { qc.invalidateQueries('overduePayments'); qc.invalidateQueries('upcomingPayments') },
  })

  const cancel = useMutation((id: string) => paymentService.cancel(id), {
    onSuccess: () => { qc.invalidateQueries('overduePayments'); qc.invalidateQueries('upcomingPayments') },
  })

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold text-gray-800">Pagamentos</h1>

      <div className="flex gap-2">
        {(['overdue', 'upcoming'] as Tab[]).map((t) => (
          <button key={t} onClick={() => setTab(t)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
              tab === t ? 'bg-primary-600 text-white' : 'bg-white text-gray-600 border hover:bg-gray-50'
            }`}>
            {t === 'overdue' ? 'Em Atraso' : 'Próximos (30 dias)'}
          </button>
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
            {payments.map((p) => (
              <tr key={p.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-medium text-gray-800">{p.clientName}</td>
                <td className="px-4 py-3 text-gray-600">{p.planName}</td>
                <td className="px-4 py-3 font-semibold">{formatCurrency(p.amount)}</td>
                <td className="px-4 py-3 text-gray-600">{formatDate(p.dueDate)}</td>
                <td className="px-4 py-3">
                  <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${statusColor[p.status]}`}>
                    {statusLabel[p.status]}
                  </span>
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
            ))}
          </tbody>
        </table>
        {payments.length === 0 && (
          <p className="text-center text-gray-400 text-sm py-8">Nenhum pagamento encontrado.</p>
        )}
      </div>
    </div>
  )
}
