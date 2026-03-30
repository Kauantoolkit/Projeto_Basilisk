import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from 'react-query'
import { clientService } from '../services/gymService'
import { Plus, Search, UserCheck, UserX } from 'lucide-react'
import type { GymClient } from '../types/gym'

function ClientForm({ client, onClose }: { client?: GymClient; onClose: () => void }) {
  const qc = useQueryClient()
  const isEdit = !!client

  const [form, setForm] = useState({
    name: client?.name ?? '',
    email: client?.email ?? '',
    phone: client?.phone ?? '',
    cpf: client?.cpf ?? '',
    birthDate: client?.birthDate ?? '',
    addressStreet: client?.addressStreet ?? '',
    addressNumber: client?.addressNumber ?? '',
    addressNeighborhood: client?.addressNeighborhood ?? '',
    addressCity: client?.addressCity ?? '',
    addressState: client?.addressState ?? '',
    addressZipCode: client?.addressZipCode ?? '',
  })

  const mutation = useMutation(
    () => isEdit ? clientService.update(client!.id, form) : clientService.create(form),
    { onSuccess: () => { qc.invalidateQueries('clients'); onClose() } }
  )

  const set = (k: string) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [k]: e.target.value }))

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-lg max-h-[90vh] overflow-y-auto">
        <h2 className="text-lg font-bold mb-4">{isEdit ? 'Editar Cliente' : 'Novo Cliente'}</h2>
        <div className="grid grid-cols-2 gap-3">
          {[
            ['name', 'Nome *', 'col-span-2'],
            ['email', 'Email', 'col-span-2'],
            ['phone', 'Telefone', ''],
            ['cpf', 'CPF', ''],
            ['birthDate', 'Nascimento', '', 'date'],
            ['addressStreet', 'Rua', 'col-span-2'],
            ['addressNumber', 'Número', ''],
            ['addressNeighborhood', 'Bairro', ''],
            ['addressCity', 'Cidade', ''],
            ['addressState', 'UF', ''],
            ['addressZipCode', 'CEP', ''],
          ].map(([k, label, cls, type]) => (
            <div key={k} className={cls || ''}>
              <label className="block text-xs font-medium text-gray-600 mb-1">{label}</label>
              <input
                type={(type as string) || 'text'}
                value={(form as Record<string, string>)[k as string]}
                onChange={set(k as string)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
          ))}
        </div>
        <div className="flex gap-2 mt-4 justify-end">
          <button onClick={onClose} className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg">
            Cancelar
          </button>
          <button
            onClick={() => mutation.mutate()}
            className="px-4 py-2 text-sm bg-primary-600 text-white rounded-lg hover:bg-primary-700"
          >
            {isEdit ? 'Salvar' : 'Criar'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default function ClientsPage() {
  const qc = useQueryClient()
  const [query, setQuery] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState<GymClient | undefined>()

  const { data } = useQuery(['clients', query], () => clientService.search(query))
  const clients = data?.data.data.content ?? []

  const deactivate = useMutation((id: string) => clientService.delete(id), {
    onSuccess: () => qc.invalidateQueries('clients'),
  })

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Clientes</h1>
        <button
          onClick={() => { setEditing(undefined); setShowForm(true) }}
          className="flex items-center gap-2 bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 text-sm font-medium"
        >
          <Plus size={16} /> Novo Cliente
        </button>
      </div>

      <div className="flex items-center gap-2 bg-white border border-gray-200 rounded-lg px-3 py-2 w-full max-w-md">
        <Search size={16} className="text-gray-400" />
        <input
          type="text"
          placeholder="Buscar por nome, email ou CPF..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="flex-1 outline-none text-sm"
        />
      </div>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
            <tr>
              <th className="px-4 py-3 text-left">Nome</th>
              <th className="px-4 py-3 text-left">Email</th>
              <th className="px-4 py-3 text-left">Telefone</th>
              <th className="px-4 py-3 text-left">CPF</th>
              <th className="px-4 py-3 text-left">Status</th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {clients.map((c) => (
              <tr key={c.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-medium text-gray-800">{c.name}</td>
                <td className="px-4 py-3 text-gray-600">{c.email ?? '-'}</td>
                <td className="px-4 py-3 text-gray-600">{c.phone ?? '-'}</td>
                <td className="px-4 py-3 text-gray-600">{c.cpf ?? '-'}</td>
                <td className="px-4 py-3">
                  {c.active
                    ? <span className="text-green-600 flex items-center gap-1"><UserCheck size={14} /> Ativo</span>
                    : <span className="text-gray-400 flex items-center gap-1"><UserX size={14} /> Inativo</span>}
                </td>
                <td className="px-4 py-3 text-right">
                  <button
                    onClick={() => { setEditing(c); setShowForm(true) }}
                    className="text-primary-600 hover:underline text-xs mr-3"
                  >
                    Editar
                  </button>
                  {c.active && (
                    <button
                      onClick={() => deactivate.mutate(c.id)}
                      className="text-red-500 hover:underline text-xs"
                    >
                      Desativar
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {clients.length === 0 && (
          <p className="text-center text-gray-400 text-sm py-8">Nenhum cliente encontrado.</p>
        )}
      </div>

      {showForm && (
        <ClientForm client={editing} onClose={() => setShowForm(false)} />
      )}
    </div>
  )
}
