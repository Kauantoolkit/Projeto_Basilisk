import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth, type AuthUser } from '@basilisk/auth'
import { Button, Input, Card, useToast } from '@basilisk/ui'
import { authService } from '../services/gymService'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()
  const { error: showError } = useToast()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const res = await authService.login(email, password)
      const { token, userId, name, email: userEmail, role } = res.data.data
      const authUser: AuthUser = {
        id: userId,
        name,
        email: userEmail,
        tenantId: '',
        tenantName: 'Gym Manager',
        roleId: role,
        roleName: role,
      }
      login(token, '', authUser)
      navigate('/')
    } catch (err: any) {
      const msg = err?.response?.data?.message ?? 'Email ou senha inválidos.'
      setError(msg)
      showError(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <Card className="w-full max-w-sm">
        <Card.Body>
          <h1 className="text-2xl font-bold text-center mb-6 text-primary-700">Gym Manager</h1>
          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              fullWidth
              required
            />
            <Input
              label="Senha"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              fullWidth
              required
            />
            {error && <p className="text-red-500 text-sm">{error}</p>}
            <Button type="submit" isLoading={loading} fullWidth>
              Entrar
            </Button>
          </form>
        </Card.Body>
      </Card>
    </div>
  )
}
