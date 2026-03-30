package com.basilisk.gym.client.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.gym.client.dto.CreateModalityRequest;
import com.basilisk.gym.client.dto.ModalityResponse;
import com.basilisk.gym.client.entity.Modality;
import com.basilisk.gym.client.repository.ModalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModalityService {

    private final ModalityRepository modalityRepository;

    public List<ModalityResponse> listActive() {
        return modalityRepository.findAllByActiveTrue().stream()
                .map(ModalityResponse::from).collect(Collectors.toList());
    }

    public ModalityResponse create(CreateModalityRequest request) {
        if (modalityRepository.existsByName(request.name())) {
            throw new BusinessException("Modality already exists: " + request.name());
        }
        Modality modality = Modality.builder()
                .name(request.name())
                .description(request.description())
                .build();
        return ModalityResponse.from(modalityRepository.save(modality));
    }

    public ModalityResponse update(UUID id, CreateModalityRequest request) {
        Modality modality = modalityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Modality not found"));
        modality.setName(request.name());
        modality.setDescription(request.description());
        return ModalityResponse.from(modalityRepository.save(modality));
    }

    public void deactivate(UUID id) {
        Modality modality = modalityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Modality not found"));
        modality.setActive(false);
        modalityRepository.save(modality);
    }

    public Modality findById(UUID id) {
        return modalityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Modality not found: " + id));
    }
}
