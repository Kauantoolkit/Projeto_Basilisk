package com.basilisk.gym.client.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.gym.client.dto.CreateEnrollmentRequest;
import com.basilisk.gym.client.dto.EnrollmentResponse;
import com.basilisk.gym.client.entity.*;
import com.basilisk.gym.client.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final GymClientService gymClientService;
    private final GymPlanService gymPlanService;

    public List<EnrollmentResponse> listByClient(UUID clientId) {
        GymClient client = gymClientService.findById(clientId);
        return enrollmentRepository.findAllByClient(client).stream()
                .map(EnrollmentResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentResponse create(CreateEnrollmentRequest request) {
        GymClient client = gymClientService.findById(request.clientId());
        GymPlan plan = gymPlanService.findById(request.planId());

        enrollmentRepository.findByClientAndStatus(client, EnrollmentStatus.ACTIVE).ifPresent(e -> {
            throw new BusinessException("Client already has an active enrollment");
        });

        Enrollment enrollment = Enrollment.builder()
                .client(client)
                .plan(plan)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .preferredPaymentMethod(request.preferredPaymentMethod() != null
                        ? request.preferredPaymentMethod() : PaymentMethod.PIX)
                .notes(request.notes())
                .build();
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    public EnrollmentResponse updateStatus(UUID id, EnrollmentStatus status) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Enrollment not found"));
        enrollment.setStatus(status);
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    public Enrollment findById(UUID id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Enrollment not found: " + id));
    }
}
