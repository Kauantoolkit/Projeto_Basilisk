package com.basilisk.gym.dashboard;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.finance.enums.EntryStatus;
import com.basilisk.gym.client.ClientRepository;
import com.basilisk.gym.expense.ExpenseRepository;
import com.basilisk.gym.payment.PaymentRepository;
import com.basilisk.gym.subscription.SubscriptionRepository;
import com.basilisk.gym.subscription.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private static final int MONTHS = 6;
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMM");

    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final ClientRepository clientRepository;
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ApiResponse<DashboardSummary> summary() {
        BigDecimal totalRevenue = paymentRepository.sumByStatus(EntryStatus.PAID);
        BigDecimal totalExpenses = expenseRepository.sumByStatus(EntryStatus.PAID);
        long activeClients = clientRepository.findAll().stream().filter(c -> c.isActive()).count();
        long activeSubscriptions = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);
        LocalDate today = LocalDate.now();
        long overdueCount = paymentRepository.countPendingBefore(EntryStatus.PENDING, today);
        BigDecimal overdueAmount = paymentRepository.sumPendingBefore(EntryStatus.PENDING, today);

        LocalDate from = YearMonth.now().minusMonths(MONTHS - 1).atDay(1);
        List<DashboardSummary.MonthlyValue> revenueByMonth = fillMonths(
                paymentRepository.revenueByMonth(EntryStatus.PAID, from), from);
        List<DashboardSummary.MonthlyValue> expensesByMonth = fillMonths(
                expenseRepository.expenseByMonth(EntryStatus.PAID, from), from);

        DashboardSummary summary = new DashboardSummary(
                totalRevenue,
                totalExpenses,
                totalRevenue.subtract(totalExpenses),
                activeClients,
                activeSubscriptions,
                overdueCount,
                overdueAmount,
                revenueByMonth,
                expensesByMonth);

        return ApiResponse.ok(summary);
    }

    private List<DashboardSummary.MonthlyValue> fillMonths(List<Object[]> rows, LocalDate from) {
        Map<YearMonth, BigDecimal> byMonth = new HashMap<>();
        for (Object[] row : rows) {
            LocalDate month = ((java.sql.Date) row[0]).toLocalDate();
            byMonth.put(YearMonth.from(month), (BigDecimal) row[1]);
        }
        List<DashboardSummary.MonthlyValue> result = new ArrayList<>();
        for (int i = 0; i < MONTHS; i++) {
            YearMonth ym = YearMonth.from(from).plusMonths(i);
            result.add(new DashboardSummary.MonthlyValue(
                    ym.atDay(1).format(MONTH_FORMAT), byMonth.getOrDefault(ym, BigDecimal.ZERO)));
        }
        return result;
    }
}