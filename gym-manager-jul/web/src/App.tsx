import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider, ProtectedRoute } from '@basilisk/auth'
import { ToastProvider } from '@basilisk/ui'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import ClientsPage from './pages/ClientsPage'
import PlansPage from './pages/PlansPage'
import PaymentsPage from './pages/PaymentsPage'
import EnrollmentsPage from './pages/EnrollmentsPage'

export default function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route
              path="/*"
              element={
                <ProtectedRoute>
                  <Layout>
                    <Routes>
                      <Route path="/" element={<DashboardPage />} />
                      <Route path="/clients" element={<ClientsPage />} />
                      <Route path="/plans" element={<PlansPage />} />
                      <Route path="/enrollments" element={<EnrollmentsPage />} />
                      <Route path="/payments" element={<PaymentsPage />} />
                    </Routes>
                  </Layout>
                </ProtectedRoute>
              }
            />
          </Routes>
        </BrowserRouter>
      </ToastProvider>
    </AuthProvider>
  )
}
