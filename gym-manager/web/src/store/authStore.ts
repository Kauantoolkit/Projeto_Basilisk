import { create } from 'zustand'

interface AuthUser {
  userId: string
  name: string
  email: string
  role: string
}

interface AuthState {
  user: AuthUser | null
  token: string | null
  setAuth: (token: string, user: AuthUser) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: localStorage.getItem('gym_token'),
  setAuth: (token, user) => {
    localStorage.setItem('gym_token', token)
    set({ token, user })
  },
  logout: () => {
    localStorage.removeItem('gym_token')
    set({ token: null, user: null })
  },
}))
