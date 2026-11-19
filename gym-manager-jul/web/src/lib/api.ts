import { createApiClient } from '@basilisk/api'
import { tokenManager } from '@basilisk/auth'

export const api = createApiClient({
  baseURL: '/api/v1',
  getAccessToken: () => tokenManager.getAccessToken(),
  onUnauthorized: () => {
    tokenManager.clearTokens()
    window.location.href = '/login'
  },
})
