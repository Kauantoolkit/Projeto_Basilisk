package com.basilisk.gym.client.dto;

import java.math.BigDecimal;

public record GymMetricsResponse(
        long totalActiveClients,
        long totalActiveEnrollments,
        long pendingPayments,
        long overduePayments,
        BigDecimal revenueThisMonth,
        BigDecimal overdueAmount,
        BigDecimal projectedMonthlyRevenue
) {}
