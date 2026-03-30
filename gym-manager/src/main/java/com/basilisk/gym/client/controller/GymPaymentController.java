package com.basilisk.gym.client.controller;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.gym.client.dto.GymPaymentResponse;
import com.basilisk.gym.client.dto.RegisterPaymentRequest;
import com.basilisk.gym.client.entity.PaymentMethod;
import com.basilisk.gym.client.service.GymPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gym/payments")
@RequiredArgsConstructor
public class GymPaymentController {

    private final GymPaymentService gymPaymentService;

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<ApiResponse<List<GymPaymentResponse>>> listByEnrollment(@PathVariable UUID enrollmentId) {
        return ResponseEntity.ok(ApiResponse.success(gymPaymentService.listByEnrollment(enrollmentId)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<GymPaymentResponse>>> listOverdue() {
        return ResponseEntity.ok(ApiResponse.success(gymPaymentService.listOverdue()));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<GymPaymentResponse>>> listUpcoming(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(ApiResponse.success(gymPaymentService.listUpcoming(days)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GymPaymentResponse>> create(@Valid @RequestBody RegisterPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(gymPaymentService.create(request)));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<GymPaymentResponse>> markAsPaid(
            @PathVariable UUID id,
            @RequestParam(required = false) LocalDate paidDate,
            @RequestParam(required = false) PaymentMethod method) {
        return ResponseEntity.ok(ApiResponse.success(gymPaymentService.markAsPaid(id, paidDate, method)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<GymPaymentResponse>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(gymPaymentService.cancel(id)));
    }
}
