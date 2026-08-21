package com.basilisk.gym.plan;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.core.exception.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanRepository planRepository;

    record PlanRequest(
            @NotBlank String name,
            String description,
            @DecimalMin("0.01") BigDecimal price,
            @Min(1) Integer durationDays
    ) {}

    record PlanResponse(
            UUID id,
            String name,
            String description,
            BigDecimal price,
            Integer durationDays,
            boolean active,
            Instant createdAt
    ) {
        static PlanResponse from(Plan p) {
            return new PlanResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                    p.getDurationDays(), p.isActive(), p.getCreatedAt());
        }
    }

    @GetMapping
    public ApiResponse<List<PlanResponse>> list(@RequestParam(required = false) String search) {
        List<Plan> plans = (search == null || search.isBlank())
                ? planRepository.findByActiveTrueOrderByNameAsc()
                : planRepository.findByNameContainingIgnoreCaseAndActiveTrue(search);
        return ApiResponse.ok(plans.stream().map(PlanResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<PlanResponse> findById(@PathVariable UUID id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plano não encontrado"));
        return ApiResponse.ok(PlanResponse.from(plan));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PlanResponse> create(@Valid @RequestBody PlanRequest request) {
        Plan plan = Plan.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .durationDays(request.durationDays())
                .build();
        return ApiResponse.ok(PlanResponse.from(planRepository.save(plan)));
    }

    @PutMapping("/{id}")
    public ApiResponse<PlanResponse> update(@PathVariable UUID id, @Valid @RequestBody PlanRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plano não encontrado"));
        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setPrice(request.price());
        plan.setDurationDays(request.durationDays());
        return ApiResponse.ok(PlanResponse.from(planRepository.save(plan)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plano não encontrado"));
        plan.setActive(false);
        planRepository.save(plan);
        return ApiResponse.ok(null);
    }
}