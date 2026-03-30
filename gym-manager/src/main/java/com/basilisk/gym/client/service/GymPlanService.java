package com.basilisk.gym.client.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.gym.client.dto.CreatePlanRequest;
import com.basilisk.gym.client.dto.GymPlanResponse;
import com.basilisk.gym.client.entity.GymPlan;
import com.basilisk.gym.client.entity.Modality;
import com.basilisk.gym.client.repository.GymPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymPlanService {

    private final GymPlanRepository gymPlanRepository;
    private final ModalityService modalityService;

    public List<GymPlanResponse> listActive() {
        return gymPlanRepository.findAllByActiveTrue().stream()
                .map(GymPlanResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public GymPlanResponse create(CreatePlanRequest request) {
        if (gymPlanRepository.existsByName(request.name())) {
            throw new BusinessException("Plan already exists: " + request.name());
        }
        Set<Modality> modalities = resolveModalities(request.modalityIds());
        GymPlan plan = GymPlan.builder()
                .name(request.name())
                .description(request.description())
                .monthlyPrice(request.monthlyPrice())
                .durationMonths(request.durationMonths())
                .paymentDueDay(request.paymentDueDay())
                .modalities(modalities)
                .build();
        return GymPlanResponse.from(gymPlanRepository.save(plan));
    }

    @Transactional
    public GymPlanResponse update(UUID id, CreatePlanRequest request) {
        GymPlan plan = gymPlanRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plan not found"));
        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setMonthlyPrice(request.monthlyPrice());
        plan.setDurationMonths(request.durationMonths());
        plan.setPaymentDueDay(request.paymentDueDay());
        plan.setModalities(resolveModalities(request.modalityIds()));
        return GymPlanResponse.from(gymPlanRepository.save(plan));
    }

    public void deactivate(UUID id) {
        GymPlan plan = gymPlanRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plan not found"));
        plan.setActive(false);
        gymPlanRepository.save(plan);
    }

    public GymPlan findById(UUID id) {
        return gymPlanRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plan not found: " + id));
    }

    private Set<Modality> resolveModalities(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return ids.stream().map(modalityService::findById).collect(Collectors.toSet());
    }
}
