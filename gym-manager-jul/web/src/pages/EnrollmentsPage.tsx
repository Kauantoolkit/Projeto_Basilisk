import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from 'react-query'
import { enrollmentService, clientService, planService } from '../services/gymService'
import { Button, Modal, Badge, Select, Input, useToast } from '@basilisk/ui'
import { formatDate } from '@basilisk/utils'
import { Plus } from 'lucide-react'
import type { Enrollment, EnrollmentStatus } from '../types/gym'

const statusConfig: Record<EnrollmentStatus, { label: string; variant: 'success' | 'warning' | 'error' | 'default' }> = {
  ACTIVE: { label: 'Ativa', variant: 'success' },
  PENDING: { label: 'Pendente', variant: 'warning' },
  SUSPENDED: { label: 'Suspensa', variant: 'error' },
  CANCELLED: { label: 'Cancelada', variant: 'default' },
}

function EnrollmentForm({ onClose }: { onClose: () => void }) {
  const qc = useQueryClient()
  const { success, error: showError } = useToast()

  const { data: clientsRes } = useQuery(['clients', ''], () => clientService.search('', 0, 100))
  const { data: plansRes } = useQuery('plans', planService.list)
  const clients = clientsRes?.data.data.content ?? []
  const plans = plansRes?.data.data ?? []

  const [form, setForm] = useState({
    clientId: '',
    planId: '',
    startDate: new Date().toISOString().split('T')[0],
    endDate: '',
    preferredPaymentMethod: 'PIX' as string,
    notes: '',
  })

  const mutation = useMutation(
    () => enrollmentService.create({
      ...form,
      endDate: form.endDate || null,
      notes: form.notes || null,
    }),
    {
      onSuccess: () => {
        success('Matrícula criada com sucesso')
        qc.invalidateQueries('enrollments')
        onClose()
      },
      onError: (err: any) => {
        showError(err?.response?.data?.message ?? 'Erro ao criar matrícula')
      },
    }
  )

  return (
    <Modal isOpen onClose={onClose} title="Nova Matrícula"
      footer={
        <div className="flex gap-2 justify-end">
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button onClick={() => mutation.mutate()} isLoading={mutation.isLoading}>Criar</Button>
        </div>
      }
    >
      <div className="space-y-3">
        <Select
          label="Cliente *"
          value={form.clientId}
          onChange={(e) => setForm((f) => ({ ...f, clientId: e.target.value }))}
          options={clients.map((c) => ({ value: c.id, label: c.name }))}
          placeholder="Selecione um cliente"
          fullWidth
        />
        <Select
          label="Plano *"
          value={form.planId}
          onChange={(e) => setForm((f) => ({ ...f, planId: e.target.value }))}
          options={plans.map((p) => ({ value: p.id, label: `${p.name} — R$ ${p.monthlyPrice}` }))}
          placeholder="Selecione um plano"
          fullWidth
        />
        <div className="grid grid-cols-2 gap-3">
          <Input
            label="Data de Início *"
            type="date"
            value={form.startDate}
            onChange={(e) => setForm((f) => ({ ...f, startDate: e.target.value }))}
            fullWidth
          />
          <Input
            label="Data de Fim"
            type="date"
            value={form.endDate}
            onChange={(e) => setForm((f) => ({ ...f, endDate: e.target.value }))}
            fullWidth
          />
        </div>
        <Select
          label="Forma de Pagamento"
          value={form.preferredPaymentMethod}
          onChange={(e) => setForm((f) => ({ ...f, preferredPaymentMethod: e.target.value }))}
          options={[
            { value: 'PIX', label: 'PIX' },
            { value: 'CREDIT_CARD', label: 'Cartão de Crédito' },
            { value: 'DEBIT_CARD', label: 'Cartão de Débito' },
            { value: 'CASH', label: 'Dinheiro' },
            { value: 'BANK_SLIP', label: 'Boleto' },
            { value: 'BANK_TRANSFER', label: 'Transferência' },
          ]}
          fullWidth
        />
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Observações</label>
          <textarea
            value={form.notes}
            onChange={(e) => setForm((f) => ({ ...f, notes: e.target.value }))}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
            rows={2}
          />
        </div>
      </div>
    </Modal>
  )
}

export default function EnrollmentsPage() {
  const qc = useQueryClient()
  const { success, error: showError } = useToast()
  const [showForm, setShowForm] = useState(false)
  const [selectedClient, setSelectedClient] = useState('')

  const { data: clientsRes } = useQuery(['clients', ''], () => clientService.search('', 0, 100))
  const clients = clientsRes?.data.data.content ?? []

  const { data: enrollmentsRes } = useQuery(
    ['enrollments', selectedClient],
    () => enrollmentService.listByClient(selectedClient),
    { enabled: !!selectedClient }
  )
  const enrollments: Enrollment[] = enrollmentsRes?.data.data ?? []

  const updateStatus = useMutation(
    ({ id, status }: { id: string; status: EnrollmentStatus }) =>
      enrollmentService.updateStatus(id, status),
    {
      onSuccess: () => {
        success('Status atualizado')
        qc.invalidateQueries('enrollments')
      },
      onError: (err: any) => {
        showError(err?.response?.data?.message ?? 'Erro ao atualizar status')
      },
    }
  )

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Matrículas</h1>
        <Button leftIcon={<Plus size={16} />} onClick={() => setShowForm(true)}>
          Nova Matrícula
        </Button>
      </div>

      <Select
        label="Filtrar por cliente"
        value={selectedClient}
        onChange={(e) => setSelectedClient(e.target.value)}
        options={clients.map((c) => ({ value: c.id, label: c.name }))}
        placeholder="Selecione um cliente para ver matrículas"
        fullWidth
      />

      {selectedClient && (
        <div className="bg-white rounded-xl shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
              <tr>
                <th className="px-4 py-3 text-left">Plano</th>
                <th className="px-4 py-3 text-left">Início</th>
                <th className="px-4 py-3 text-left">Fim</th>
                <th className="px-4 py-3 text-left">Pagamento</th>
                <th className="px-4 py-3 text-left">Status</th>
                <th className="px-4 py-3" />
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {enrollments.map((e) => {
                const cfg = statusConfig[e.status]
                return (
                  <tr key={e.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 font-medium text-gray-800">{e.planName}</td>
                    <td className="px-4 py-3 text-gray-600">{formatDate(e.startDate)}</td>
                    <td className="px-4 py-3 text-gray-600">{e.endDate ? formatDate(e.endDate) : '—'}</td>
                    <td className="px-4 py-3 text-gray-600">{e.preferredPaymentMethod}</td>
                    <td className="px-4 py-3">
                      <Badge variant={cfg.variant}>{cfg.label}</Badge>
                    </td>
                    <td className="px-4 py-3 text-right space-x-2">
                      {e.status === 'ACTIVE' && (
                        <Button variant="ghost" size="sm"
                          onClick={() => updateStatus.mutate({ id: e.id, status: 'SUSPENDED' })}>
                          Suspender
                        </Button>
                      )}
                      {e.status === 'SUSPENDED' && (
                        <Button variant="ghost" size="sm"
                          onClick={() => updateStatus.mutate({ id: e.id, status: 'ACTIVE' })}>
                          Reativar
                        </Button>
                      )}
                      {(e.status === 'ACTIVE' || e.status === 'SUSPENDED') && (
                        <Button variant="danger" size="sm"
                          onClick={() => updateStatus.mutate({ id: e.id, status: 'CANCELLED' })}>
                          Cancelar
                        </Button>
                      )}
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
          {enrollments.length === 0 && (
            <p className="text-center text-gray-400 text-sm py-8">Nenhuma matrícula encontrada.</p>
          )}
        </div>
      )}

      {showForm && <EnrollmentForm onClose={() => setShowForm(false)} />}
    </div>
  )
}
