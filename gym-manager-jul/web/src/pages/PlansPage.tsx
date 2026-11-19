import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from 'react-query'
import { planService, modalityService } from '../services/gymService'
import { formatCurrency } from '@basilisk/utils'
import { Button, Card, Modal, Badge, useToast } from '@basilisk/ui'
import { Plus } from 'lucide-react'
import type { GymPlan } from '../types/gym'

function PlanForm({ plan, onClose }: { plan?: GymPlan; onClose: () => void }) {
  const qc = useQueryClient()
  const { success, error: showError } = useToast()
  const isEdit = !!plan
  const { data: modRes } = useQuery('modalities', modalityService.list)
  const modalities = modRes?.data.data ?? []

  const [form, setForm] = useState({
    name: plan?.name ?? '',
    description: plan?.description ?? '',
    monthlyPrice: plan?.monthlyPrice ?? 0,
    durationMonths: plan?.durationMonths ?? '',
    paymentDueDay: plan?.paymentDueDay ?? 5,
    modalityIds: plan?.modalities.map((m) => m.id) ?? [] as string[],
  })

  const mutation = useMutation(
    () => isEdit ? planService.update(plan!.id, form) : planService.create(form),
    {
      onSuccess: () => {
        success(isEdit ? 'Plano atualizado com sucesso' : 'Plano criado com sucesso')
        qc.invalidateQueries('plans')
        onClose()
      },
      onError: (err: any) => {
        showError(err?.response?.data?.message ?? 'Erro ao salvar plano')
      },
    }
  )

  const toggleModality = (id: string) =>
    setForm((f) => ({
      ...f,
      modalityIds: f.modalityIds.includes(id)
        ? f.modalityIds.filter((x) => x !== id)
        : [...f.modalityIds, id],
    }))

  return (
    <Modal isOpen onClose={onClose} title={isEdit ? 'Editar Plano' : 'Novo Plano'}
      footer={
        <div className="flex gap-2 justify-end">
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button onClick={() => mutation.mutate()} isLoading={mutation.isLoading}>
            {isEdit ? 'Salvar' : 'Criar'}
          </Button>
        </div>
      }
    >
      <div className="space-y-3">
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Nome *</label>
          <input value={form.name} onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Descrição</label>
          <input value={form.description} onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Preço Mensal (R$) *</label>
            <input type="number" step="0.01" value={form.monthlyPrice}
              onChange={(e) => setForm((f) => ({ ...f, monthlyPrice: parseFloat(e.target.value) }))}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Vencimento (dia)</label>
            <input type="number" min={1} max={28} value={form.paymentDueDay}
              onChange={(e) => setForm((f) => ({ ...f, paymentDueDay: parseInt(e.target.value) }))}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
          </div>
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-2">Modalidades</label>
          <div className="flex flex-wrap gap-2">
            {modalities.map((m) => (
              <button key={m.id} type="button" onClick={() => toggleModality(m.id)}
                className={`px-3 py-1 rounded-full text-xs border transition ${
                  form.modalityIds.includes(m.id)
                    ? 'bg-primary-600 text-white border-primary-600'
                    : 'text-gray-600 border-gray-300 hover:border-primary-400'
                }`}>
                {m.name}
              </button>
            ))}
          </div>
        </div>
      </div>
    </Modal>
  )
}

export default function PlansPage() {
  const qc = useQueryClient()
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState<GymPlan | undefined>()
  const { data } = useQuery('plans', planService.list)
  const plans = data?.data.data ?? []
  const { success, error: showError } = useToast()
  const deactivate = useMutation((id: string) => planService.delete(id), {
    onSuccess: () => {
      success('Plano desativado com sucesso')
      qc.invalidateQueries('plans')
    },
    onError: (err: any) => {
      showError(err?.response?.data?.message ?? 'Erro ao desativar plano')
    },
  })

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Planos</h1>
        <Button leftIcon={<Plus size={16} />} onClick={() => { setEditing(undefined); setShowForm(true) }}>
          Novo Plano
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {plans.map((p) => (
          <Card key={p.id}>
            <Card.Body>
              <div className="flex justify-between items-start mb-2">
                <h3 className="font-semibold text-gray-800">{p.name}</h3>
                <span className="text-primary-600 font-bold">{formatCurrency(p.monthlyPrice)}/mês</span>
              </div>
              {p.description && <p className="text-xs text-gray-500 mb-3">{p.description}</p>}
              <p className="text-xs text-gray-500 mb-2">Vencimento: dia {p.paymentDueDay}</p>
              {p.modalities.length > 0 && (
                <div className="flex flex-wrap gap-1 mb-3">
                  {p.modalities.map((m) => (
                    <Badge key={m.id} variant="default">{m.name}</Badge>
                  ))}
                </div>
              )}
              <div className="flex gap-2 mt-3">
                <Button variant="ghost" size="sm" onClick={() => { setEditing(p); setShowForm(true) }}>
                  Editar
                </Button>
                {p.active && (
                  <Button variant="danger" size="sm" onClick={() => deactivate.mutate(p.id)}>
                    Desativar
                  </Button>
                )}
              </div>
            </Card.Body>
          </Card>
        ))}
      </div>

      {showForm && <PlanForm plan={editing} onClose={() => setShowForm(false)} />}
    </div>
  )
}
