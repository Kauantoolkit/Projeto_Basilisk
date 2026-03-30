package com.basilisk.gym.client.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.gym.client.dto.CreateClientRequest;
import com.basilisk.gym.client.dto.GymClientResponse;
import com.basilisk.gym.client.entity.GymClient;
import com.basilisk.gym.client.repository.GymClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GymClientService {

    private final GymClientRepository gymClientRepository;

    public Page<GymClientResponse> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return gymClientRepository.findAll(pageable).map(GymClientResponse::from);
        }
        return gymClientRepository.search(query, pageable).map(GymClientResponse::from);
    }

    public GymClientResponse getById(UUID id) {
        return GymClientResponse.from(findById(id));
    }

    public GymClientResponse create(CreateClientRequest request) {
        if (request.email() != null && gymClientRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already in use");
        }
        if (request.cpf() != null && gymClientRepository.existsByCpf(request.cpf())) {
            throw new BusinessException("CPF already registered");
        }
        GymClient client = GymClient.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .cpf(request.cpf())
                .birthDate(request.birthDate())
                .addressStreet(request.addressStreet())
                .addressNumber(request.addressNumber())
                .addressNeighborhood(request.addressNeighborhood())
                .addressCity(request.addressCity())
                .addressState(request.addressState())
                .addressZipCode(request.addressZipCode())
                .build();
        return GymClientResponse.from(gymClientRepository.save(client));
    }

    public GymClientResponse update(UUID id, CreateClientRequest request) {
        GymClient client = findById(id);
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setCpf(request.cpf());
        client.setBirthDate(request.birthDate());
        client.setAddressStreet(request.addressStreet());
        client.setAddressNumber(request.addressNumber());
        client.setAddressNeighborhood(request.addressNeighborhood());
        client.setAddressCity(request.addressCity());
        client.setAddressState(request.addressState());
        client.setAddressZipCode(request.addressZipCode());
        return GymClientResponse.from(gymClientRepository.save(client));
    }

    public void deactivate(UUID id) {
        GymClient client = findById(id);
        client.setActive(false);
        gymClientRepository.save(client);
    }

    public GymClient findById(UUID id) {
        return gymClientRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Client not found: " + id));
    }
}
