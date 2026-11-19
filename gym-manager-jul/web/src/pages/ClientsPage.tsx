import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from 'react-query'
import { clientService } from '../services/gymService'
import { Button, Input, Modal, Badge, useToast } from '@basilisk/ui'
import { useDebounce } from '@basilisk/hooks'
import { maskCPF, maskPhone } from '@basilisk/utils'
import { Plus, Search, UserCheck, UserX } from 'lucide-react'
import type { GymClient } from '../types/gym'

function ClientForm({ client, onClose }: { client?: GymClient; onClose: () => void }) {
  const qc = useQueryClient()
  const { success, error: showError } = useToast()
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
    {
      onSuccess: () => {
        success(isEdit ? 'Cliente atualizado com sucesso' : 'Cliente criado com sucesso')
        qc.invalidateQueries('clients')
        onClose()
      },
      onError: (err: any) => {
        showError(err?.response?.data?.message ?? 'Erro ao salvar cliente')
      },
    }
  )

  const set = (k: string) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [k]: e.target.value }))

  return (
    <Modal isOpen onClose={onClose} title={isEdit ? 'Editar Cliente' : 'Novo Cliente'} size="lg"
      footer={
        <div className="flex gap-2 justify-end">
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button onClick={() => mutation.mutate()} isLoading={mutation.isLoading}>
            {isEdit ? 'Salvar' : 'Criar'}
          </Button>
        </div>
      }
    >
      <div className="grid grid-cols-2 gap-3">
        <div className="col-span-2">
          <Input label="Nome *" value={form.name} onChange={set('name')} fullWidth />
        </div>
        <div className="col-span-2">
          <Input label="Email" type="email" value={form.email} onChange={set('email')} fullWidth />
        </div>
        <Input label="Telefone" value={form.phone} onChange={set('phone')} />
        <Input label="CPF" value={form.cpf} onChange={set('cpf')} />
        <Input label="Nascimento" type="date" value={form.birthDate} onChange={set('birthDate')} />
        <div className="col-span-2">
          <Input label="Rua" value={form.addressStreet} onChange={set('addressStreet')} fullWidth />
        </div>
        <Input label="Número" value={form.addressNumber} onChange={set('addressNumber')} />
        <Input label="Bairro" value={form.addressNeighborhood} onChange={set('addressNeighborhood')} />
        <Input label="Cidade" value={form.addressCity} onChange={set('addressCity')} />
        <Input label="UF" value={form.addressState} onChange={set('addressState')} />
        <Input label="CEP" value={form.addressZipCode} onChange={set('addressZipCode')} />
      </div>
    </Modal>
  )
}

export default function ClientsPage() {
  const qc = useQueryClient()
  const [query, setQuery] = useState('')
  const debouncedQuery = useDebounce(query)
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState<GymClient | undefined>()

  const { data } = useQuery(['clients', debouncedQuery], () => clientService.search(debouncedQuery))
  const clients = data?.data.data.content ?? []

  const { success, error: showError } = useToast()
  const deactivate = useMutation((id: string) => clientService.delete(id), {
    onSuccess: () => {
      success('Cliente desativado com sucesso')
      qc.invalidateQueries('clients')
    },
    onError: (err: any) => {
      showError(err?.response?.data?.message ?? 'Erro ao desativar cliente')
    },
  })

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Clientes</h1>
        <Button leftIcon={<Plus size={16} />} onClick={() => { setEditing(undefined); setShowForm(true) }}>
          Novo Cliente
        </Button>
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
                <td className="px-4 py-3 text-gray-600">{c.phone ? maskPhone(c.phone) : '-'}</td>
                <td className="px-4 py-3 text-gray-600">{c.cpf ? maskCPF(c.cpf) : '-'}</td>
                <td className="px-4 py-3">
                  {c.active
                    ? <Badge variant="success"><UserCheck size={14} className="inline mr-1" />Ativo</Badge>
                    : <Badge variant="default"><UserX size={14} className="inline mr-1" />Inativo</Badge>}
                </td>
                <td className="px-4 py-3 text-right">
                  <Button variant="ghost" size="sm" onClick={() => { setEditing(c); setShowForm(true) }}>
                    Editar
                  </Button>
                  {c.active && (
                    <Button variant="danger" size="sm" onClick={() => deactivate.mutate(c.id)}>
                      Desativar
                    </Button>
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

      {showForm && <ClientForm client={editing} onClose={() => setShowForm(false)} />}
    </div>
  )
}
