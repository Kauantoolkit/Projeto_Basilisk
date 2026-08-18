package com.basilisk.petshop.order;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.core.exception.BusinessException;
import com.basilisk.petshop.customer.Customer;
import com.basilisk.petshop.customer.CustomerRepository;
import com.basilisk.petshop.pet.Pet;
import com.basilisk.petshop.pet.PetRepository;
import com.basilisk.status.service.StatusMachine;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;
    private final StatusMachine statusMachine;

    record CreateOrderRequest(
            @NotNull UUID customerId,
            @NotNull UUID petId,
            @NotBlank String type,
            String description,
            @NotNull LocalDateTime scheduledDate,
            @NotNull BigDecimal price
    ) {}

    record UpdateOrderRequest(
            @NotNull UUID customerId,
            @NotNull UUID petId,
            @NotBlank String type,
            String description,
            @NotNull LocalDateTime scheduledDate,
            @NotNull BigDecimal price
    ) {}

    record ChangeStatusRequest(
            @NotBlank String status
    ) {}

    @GetMapping
    public ApiResponse<List<ServiceOrder>> list() {
        return ApiResponse.success(serviceOrderRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ApiResponse<ServiceOrder> findById(@PathVariable UUID id) {
        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Ordem de serviço não encontrada"));
        return ApiResponse.success(order);
    }

    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<ServiceOrder>> findByCustomer(@PathVariable UUID customerId) {
        return ApiResponse.success(serviceOrderRepository.findByCustomerIdAndActiveTrue(customerId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ServiceOrder> create(@Valid @RequestBody CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new BusinessException("Pet não encontrado"));
        ServiceOrder order = ServiceOrder.builder()
                .customer(customer)
                .pet(pet)
                .type(request.type())
                .description(request.description())
                .scheduledDate(request.scheduledDate())
                .price(request.price())
                .build();
        return ApiResponse.success(serviceOrderRepository.save(order));
    }

    @PutMapping("/{id}")
    public ApiResponse<ServiceOrder> update(@PathVariable UUID id, @Valid @RequestBody UpdateOrderRequest request) {
        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Ordem de serviço não encontrada"));
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new BusinessException("Pet não encontrado"));
        order.setCustomer(customer);
        order.setPet(pet);
        order.setType(request.type());
        order.setDescription(request.description());
        order.setScheduledDate(request.scheduledDate());
        order.setPrice(request.price());
        return ApiResponse.success(serviceOrderRepository.save(order));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<ServiceOrder> changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeStatusRequest request) {
        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Ordem de serviço não encontrada"));
        statusMachine.transition(order, request.status(), "system", null);
        if ("completed".equals(request.status())) {
            order.setCompletedDate(LocalDateTime.now());
        }
        return ApiResponse.success(serviceOrderRepository.save(order));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Ordem de serviço não encontrada"));
        order.setActive(false);
        serviceOrderRepository.save(order);
        return ApiResponse.success(null);
    }
}
