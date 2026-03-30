import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from 'react-query'
import { planService, modalityService } from '../services/gymService'
import { formatCurrency } from '../lib/format'
import { Plus } from 'lucide-react'
import type { GymPlan } from '../types/gym'

function PlanForm({ plan, onClose }: { plan?: GymPlan; onClose: () => void }) {
  const qc = useQueryClient()
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
    { onSuccess: () => { qc.invalidateQueries('plans'); onClose() } }
  )

  const toggleModality = (id: string) =>
    setForm((f) => ({
      ...f,
      modalityIds: f.modalityIds.includes(id)
        ? f.modalityIds.filter((x) => x !== id)
        : [...f.modalityIds, id],
    }))

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-md">
        <h2 className="text-lg font-bold mb-4">{isEdit ? 'Editar Plano' : 'Novo Plano'}</h2>
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
        <div className="flex gap-2 mt-4 justify-end">
          <button onClick={onClose} className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg">
            Cancelar
          </button>
          <button onClick={() => mutation.mutate()}
            className="px-4 py-2 text-sm bg-primary-600 text-white rounded-lg hover:bg-primary-700">
            {isEdit ? 'Salvar' : 'Criar'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default function PlansPage() {
  const qc = useQueryClient()
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState<GymPlan | undefined>()
  const { data } = useQuery('plans', planService.list)
  const plans = data?.data.data ?? []
  const deactivate = useMutation((id: string) => planService.delete(id), {
    onSuccess: () => qc.invalidateQueries('plans'),
  })

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Planos</h1>
        <button onClick={() => { setEditing(undefined); setShowForm(true) }}
          className="flex items-center gap-2 bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 text-sm font-medium">
          <Plus size={16} /> Novo Plano
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {plans.map((p) => (
          <div key={p.id} className="bg-white rounded-xl shadow-sm p-5">
            <div className="flex justify-between items-start mb-2">
              <h3 className="font-semibold text-gray-800">{p.name}</h3>
              <span className="text-primary-600 font-bold">{formatCurrency(p.monthlyPrice)}/mês</span>
            </div>
            {p.description && <p className="text-xs text-gray-500 mb-3">{p.description}</p>}
            <p className="text-xs text-gray-500 mb-2">Vencimento: dia {p.paymentDueDay}</p>
            {p.modalities.length > 0 && (
              <div className="flex flex-wrap gap-1 mb-3">
                {p.modalities.map((m) => (
                  <span key={m.id} className="bg-gray-100 text-gray-600 text-xs px-2 py-0.5 rounded-full">{m.name}</span>
                ))}
              </div>
            )}
            <div className="flex gap-2 mt-3">
              <button onClick={() => { setEditing(p); setShowForm(true) }}
                className="text-xs text-primary-600 hover:underline">Editar</button>
              {p.active && (
                <button onClick={() => deactivate.mutate(p.id)}
                  className="text-xs text-red-500 hover:underline">Desativar</button>
              )}
            </div>
          </div>
        ))}
      </div>

      {showForm && <PlanForm plan={editing} onClose={() => setShowForm(false)} />}
    </div>
  )
}
