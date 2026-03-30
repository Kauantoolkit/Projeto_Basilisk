package com.basilisk.gym.client.service;

import com.basilisk.gym.client.dto.GymMetricsResponse;
import com.basilisk.gym.client.entity.EnrollmentStatus;
import com.basilisk.gym.client.repository.EnrollmentRepository;
import com.basilisk.gym.client.repository.GymClientRepository;
import com.basilisk.gym.client.repository.GymPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GymMetricsService {

    private final GymClientRepository gymClientRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GymPaymentRepository gymPaymentRepository;

    public GymMetricsResponse getMetrics() {
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);

        long totalActiveClients = gymClientRepository.findAllByActiveTrue().size();
        long totalActiveEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
        long pendingPayments = gymPaymentRepository.countByStatus("PENDING");
        long overduePayments = gymPaymentRepository.countByStatus("OVERDUE");
        BigDecimal revenueThisMonth = gymPaymentRepository.sumPaidBetween(firstOfMonth, today);
        BigDecimal overdueAmount = gymPaymentRepository.sumOverdue(today);

        // Projected = sum of monthly prices of all active enrollments
        BigDecimal projectedMonthlyRevenue = enrollmentRepository.findAllByStatus(EnrollmentStatus.ACTIVE)
                .stream()
                .map(e -> e.getPlan().getMonthlyPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new GymMetricsResponse(
                totalActiveClients, totalActiveEnrollments,
                pendingPayments, overduePayments,
                revenueThisMonth, overdueAmount, projectedMonthlyRevenue);
    }
}
