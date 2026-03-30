package com.basilisk.gym.client.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.gym.client.dto.GymPaymentResponse;
import com.basilisk.gym.client.dto.RegisterPaymentRequest;
import com.basilisk.gym.client.entity.Enrollment;
import com.basilisk.gym.client.entity.GymPayment;
import com.basilisk.gym.client.entity.PaymentMethod;
import com.basilisk.gym.client.repository.GymPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymPaymentService {

    private final GymPaymentRepository gymPaymentRepository;
    private final EnrollmentService enrollmentService;

    public List<GymPaymentResponse> listByEnrollment(UUID enrollmentId) {
        Enrollment enrollment = enrollmentService.findById(enrollmentId);
        return gymPaymentRepository.findAllByEnrollment(enrollment).stream()
                .map(GymPaymentResponse::from).collect(Collectors.toList());
    }

    public List<GymPaymentResponse> listOverdue() {
        return gymPaymentRepository.findOverdue(LocalDate.now()).stream()
                .map(GymPaymentResponse::from).collect(Collectors.toList());
    }

    public List<GymPaymentResponse> listUpcoming(int days) {
        LocalDate today = LocalDate.now();
        return gymPaymentRepository.findUpcoming(today, today.plusDays(days)).stream()
                .map(GymPaymentResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public GymPaymentResponse create(RegisterPaymentRequest request) {
        Enrollment enrollment = enrollmentService.findById(request.enrollmentId());
        GymPayment payment = GymPayment.builder()
                .enrollment(enrollment)
                .amount(request.amount())
                .dueDate(request.dueDate())
                .paidDate(request.paidDate())
                .paymentMethod(request.paymentMethod() != null ? request.paymentMethod() : PaymentMethod.PIX)
                .status(request.paidDate() != null ? "PAID" : "PENDING")
                .notes(request.notes())
                .build();
        return GymPaymentResponse.from(gymPaymentRepository.save(payment));
    }

    @Transactional
    public GymPaymentResponse markAsPaid(UUID id, LocalDate paidDate, PaymentMethod method) {
        GymPayment payment = gymPaymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Payment not found"));
        if ("PAID".equals(payment.getStatus())) {
            throw new BusinessException("Payment already marked as paid");
        }
        payment.setPaidDate(paidDate != null ? paidDate : LocalDate.now());
        payment.setPaymentMethod(method != null ? method : payment.getPaymentMethod());
        payment.setStatus("PAID");
        return GymPaymentResponse.from(gymPaymentRepository.save(payment));
    }

    @Transactional
    public GymPaymentResponse cancel(UUID id) {
        GymPayment payment = gymPaymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Payment not found"));
        payment.setStatus("CANCELLED");
        return GymPaymentResponse.from(gymPaymentRepository.save(payment));
    }

    public void markOverduePayments() {
        LocalDate today = LocalDate.now();
        List<GymPayment> overdue = gymPaymentRepository.findOverdue(today);
        overdue.forEach(p -> p.setStatus("OVERDUE"));
        gymPaymentRepository.saveAll(overdue);
    }
}
