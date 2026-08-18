package com.basilisk.petshop.pet;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.core.exception.BusinessException;
import com.basilisk.petshop.customer.Customer;
import com.basilisk.petshop.customer.CustomerRepository;
import com.basilisk.status.service.StatusMachine;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;
    private final StatusMachine statusMachine;

    record CreatePetRequest(
            @NotBlank String name,
            String species,
            String breed,
            Integer age,
            Double weight,
            UUID ownerId,
            String imageUrl
    ) {}

    record UpdatePetRequest(
            @NotBlank String name,
            String species,
            String breed,
            Integer age,
            Double weight,
            UUID ownerId,
            String imageUrl
    ) {}

    record ChangeStatusRequest(
            @NotBlank String status
    ) {}

    @GetMapping
    public ApiResponse<List<Pet>> list() {
        return ApiResponse.success(petRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ApiResponse<Pet> findById(@PathVariable UUID id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pet não encontrado"));
        return ApiResponse.success(pet);
    }

    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<Pet>> findByOwner(@PathVariable UUID ownerId) {
        return ApiResponse.success(petRepository.findByOwnerIdAndActiveTrue(ownerId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Pet> create(@Valid @RequestBody CreatePetRequest request) {
        Customer owner = null;
        if (request.ownerId() != null) {
            owner = customerRepository.findById(request.ownerId())
                    .orElseThrow(() -> new BusinessException("Dono não encontrado"));
        }
        Pet pet = Pet.builder()
                .name(request.name())
                .species(request.species())
                .breed(request.breed())
                .age(request.age())
                .weight(request.weight())
                .owner(owner)
                .imageUrl(request.imageUrl())
                .build();
        return ApiResponse.success(petRepository.save(pet));
    }

    @PutMapping("/{id}")
    public ApiResponse<Pet> update(@PathVariable UUID id, @Valid @RequestBody UpdatePetRequest request) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pet não encontrado"));
        Customer owner = null;
        if (request.ownerId() != null) {
            owner = customerRepository.findById(request.ownerId())
                    .orElseThrow(() -> new BusinessException("Dono não encontrado"));
        }
        pet.setName(request.name());
        pet.setSpecies(request.species());
        pet.setBreed(request.breed());
        pet.setAge(request.age());
        pet.setWeight(request.weight());
        pet.setOwner(owner);
        pet.setImageUrl(request.imageUrl());
        return ApiResponse.success(petRepository.save(pet));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Pet> changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeStatusRequest request) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pet não encontrado"));
        statusMachine.transition(pet, request.status(), "system", null);
        return ApiResponse.success(petRepository.save(pet));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pet não encontrado"));
        pet.setActive(false);
        petRepository.save(pet);
        return ApiResponse.success(null);
    }
}
