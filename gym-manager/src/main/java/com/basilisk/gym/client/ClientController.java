package com.basilisk.gym.client;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.core.exception.BusinessException;
import com.basilisk.core.validation.ValidCpf;
import com.basilisk.core.validation.ValidPhone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientRepository clientRepository;

    record ClientRequest(
            @NotBlank String name,
            @Email String email,
            @ValidPhone String phone,
            @ValidCpf String cpf,
            LocalDate birthDate,
            String address
    ) {}

    record ClientResponse(
            UUID id,
            String name,
            String email,
            String phone,
            String cpf,
            LocalDate birthDate,
            String address,
            boolean active,
            java.time.Instant createdAt
    ) {
        static ClientResponse from(Client c) {
            return new ClientResponse(c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                    c.getCpf(), c.getBirthDate(), c.getAddress(), c.isActive(), c.getCreatedAt());
        }
    }

    @GetMapping
    public ApiResponse<List<ClientResponse>> list(@RequestParam(required = false) String search) {
        List<Client> clients = (search == null || search.isBlank())
                ? clientRepository.findByActiveTrueOrderByNameAsc()
                : clientRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndActiveTrue(search, search);
        return ApiResponse.ok(clients.stream().map(ClientResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ApiResponse<ClientResponse> findById(@PathVariable UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        return ApiResponse.ok(ClientResponse.from(client));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ClientResponse> create(@Valid @RequestBody ClientRequest request) {
        Client client = Client.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .cpf(request.cpf())
                .birthDate(request.birthDate())
                .address(request.address())
                .build();
        return ApiResponse.ok(ClientResponse.from(clientRepository.save(client)));
    }

    @PutMapping("/{id}")
    public ApiResponse<ClientResponse> update(@PathVariable UUID id, @Valid @RequestBody ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setCpf(request.cpf());
        client.setBirthDate(request.birthDate());
        client.setAddress(request.address());
        return ApiResponse.ok(ClientResponse.from(clientRepository.save(client)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        client.setActive(false);
        clientRepository.save(client);
        return ApiResponse.ok(null);
    }
}