import { useQuery } from 'react-query'
import { metricsService, paymentService } from '../services/gymService'
import { formatCurrency } from '@basilisk/utils'
import { Card } from '@basilisk/ui'
import { Users, TrendingUp, AlertTriangle, Clock } from 'lucide-react'

function MetricCard({ title, value, icon: Icon, color }: {
  title: string; value: string | number; icon: React.ElementType; color: string
}) {
  return (
    <Card className={`border-l-4 ${color}`}>
      <Card.Body>
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-gray-500">{title}</p>
            <p className="text-2xl font-bold mt-1">{value}</p>
          </div>
          <Icon size={32} className="text-gray-300" />
        </div>
      </Card.Body>
    </Card>
  )
}

export default function DashboardPage() {
  const { data: metricsRes } = useQuery('metrics', metricsService.get)
  const { data: overdueRes } = useQuery('overduePayments', paymentService.listOverdue)
  const { data: upcomingRes } = useQuery('upcomingPayments', () => paymentService.listUpcoming(7))

  const metrics = metricsRes?.data.data
  const overdue = overdueRes?.data.data ?? []
  const upcoming = upcomingRes?.data.data ?? []

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Dashboard</h1>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <MetricCard title="Clientes Ativos" value={metrics?.totalActiveClients ?? '-'} icon={Users} color="border-blue-500" />
        <MetricCard title="Receita do Mês" value={metrics ? formatCurrency(metrics.revenueThisMonth) : '-'} icon={TrendingUp} color="border-green-500" />
        <MetricCard title="Pagamentos em Atraso" value={metrics?.overduePayments ?? '-'} icon={AlertTriangle} color="border-red-500" />
        <MetricCard title="Receita Projetada" value={metrics ? formatCurrency(metrics.projectedMonthlyRevenue) : '-'} icon={Clock} color="border-purple-500" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <Card.Header>
            <h2 className="font-semibold text-gray-700">Pagamentos em Atraso</h2>
          </Card.Header>
          <Card.Body>
            {overdue.length === 0 ? (
              <p className="text-gray-400 text-sm">Nenhum pagamento em atraso.</p>
            ) : (
              <ul className="space-y-2">
                {overdue.slice(0, 5).map((p) => (
                  <li key={p.id} className="flex justify-between text-sm">
                    <span className="font-medium text-gray-700">{p.clientName}</span>
                    <span className="text-red-600 font-semibold">{formatCurrency(p.amount)}</span>
                  </li>
                ))}
              </ul>
            )}
          </Card.Body>
        </Card>

        <Card>
          <Card.Header>
            <h2 className="font-semibold text-gray-700">Próximos Pagamentos (7 dias)</h2>
          </Card.Header>
          <Card.Body>
            {upcoming.length === 0 ? (
              <p className="text-gray-400 text-sm">Nenhum pagamento próximo.</p>
            ) : (
              <ul className="space-y-2">
                {upcoming.slice(0, 5).map((p) => (
                  <li key={p.id} className="flex justify-between text-sm">
                    <span className="font-medium text-gray-700">{p.clientName}</span>
                    <span className="text-gray-600">{formatCurrency(p.amount)} — {p.dueDate}</span>
                  </li>
                ))}
              </ul>
            )}
          </Card.Body>
        </Card>
      </div>
    </div>
  )
}
