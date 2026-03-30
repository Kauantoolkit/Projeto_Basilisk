package com.basilisk.gym.client.controller;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.gym.client.dto.CreateEnrollmentRequest;
import com.basilisk.gym.client.dto.EnrollmentResponse;
import com.basilisk.gym.client.entity.EnrollmentStatus;
import com.basilisk.gym.client.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gym/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> listByClient(@PathVariable UUID clientId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.listByClient(clientId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentResponse>> create(@Valid @RequestBody CreateEnrollmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.create(request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateStatus(
            @PathVariable UUID id, @RequestParam EnrollmentStatus status) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.updateStatus(id, status)));
    }
}
