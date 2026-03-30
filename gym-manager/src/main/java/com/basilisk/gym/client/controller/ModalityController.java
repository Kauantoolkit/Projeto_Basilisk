package com.basilisk.gym.client.controller;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.gym.client.dto.CreateModalityRequest;
import com.basilisk.gym.client.dto.ModalityResponse;
import com.basilisk.gym.client.service.ModalityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gym/modalities")
@RequiredArgsConstructor
public class ModalityController {

    private final ModalityService modalityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ModalityResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(modalityService.listActive()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ModalityResponse>> create(@Valid @RequestBody CreateModalityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(modalityService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModalityResponse>> update(@PathVariable UUID id,
            @Valid @RequestBody CreateModalityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(modalityService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        modalityService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
