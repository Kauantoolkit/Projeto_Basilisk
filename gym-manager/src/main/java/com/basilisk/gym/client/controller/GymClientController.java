package com.basilisk.gym.client.controller;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.gym.client.dto.CreateClientRequest;
import com.basilisk.gym.client.dto.GymClientResponse;
import com.basilisk.gym.client.service.GymClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gym/clients")
@RequiredArgsConstructor
public class GymClientController {

    private final GymClientService gymClientService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GymClientResponse>>> search(
            @RequestParam(required = false) String query,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(gymClientService.search(query, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GymClientResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(gymClientService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GymClientResponse>> create(@Valid @RequestBody CreateClientRequest request) {
        return ResponseEntity.ok(ApiResponse.success(gymClientService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GymClientResponse>> update(@PathVariable UUID id,
            @Valid @RequestBody CreateClientRequest request) {
        return ResponseEntity.ok(ApiResponse.success(gymClientService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        gymClientService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
