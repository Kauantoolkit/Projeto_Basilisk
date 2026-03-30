package com.basilisk.gym.client.controller;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.gym.client.dto.GymMetricsResponse;
import com.basilisk.gym.client.service.GymMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gym/metrics")
@RequiredArgsConstructor
public class GymMetricsController {

    private final GymMetricsService gymMetricsService;

    @GetMapping
    public ResponseEntity<ApiResponse<GymMetricsResponse>> getMetrics() {
        return ResponseEntity.ok(ApiResponse.success(gymMetricsService.getMetrics()));
    }
}
