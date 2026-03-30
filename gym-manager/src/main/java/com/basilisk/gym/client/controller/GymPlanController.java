package com.basilisk.gym.client.controller;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.gym.client.dto.CreatePlanRequest;
import com.basilisk.gym.client.dto.GymPlanResponse;
import com.basilisk.gym.client.service.GymPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gym/plans")
@RequiredArgsConstructor
public class GymPlanController {

    private final GymPlanService gymPlanService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GymPlanResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(gymPlanService.listActive()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GymPlanResponse>> create(@Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(gymPlanService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GymPlanResponse>> update(@PathVariable UUID id,
            @Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(gymPlanService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        gymPlanService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
