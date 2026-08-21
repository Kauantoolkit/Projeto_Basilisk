package com.basilisk.gym.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardSummary(
        BigDecimal totalRevenue,
        BigDecimal totalExpenses,
        BigDecimal profit,
        long activeClients,
        long activeSubscriptions,
        long overdueCount,
        BigDecimal overdueAmount,
        List<MonthlyValue> revenueByMonth,
        List<MonthlyValue> expensesByMonth
) {

    public record MonthlyValue(String month, BigDecimal value) {}
}