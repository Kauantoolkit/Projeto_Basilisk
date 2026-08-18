package com.basilisk.petshop.customer;

import com.basilisk.core.dto.ApiResponse;
import com.basilisk.core.exception.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

    record CreateCustomerRequest(
            @NotBlank String name,
            String email,
            String phone,
            String address
    ) {}

    record UpdateCustomerRequest(
            @NotBlank String name,
            String email,
            String phone,
            String address
    ) {}

    @GetMapping
    public ApiResponse<List<Customer>> list() {
        return ApiResponse.success(customerRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ApiResponse<Customer> findById(@PathVariable UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        return ApiResponse.success(customer);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Customer> create(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = Customer.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .build();
        return ApiResponse.success(customerRepository.save(customer));
    }

    @PutMapping("/{id}")
    public ApiResponse<Customer> update(@PathVariable UUID id, @Valid @RequestBody UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
        return ApiResponse.success(customerRepository.save(customer));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        customer.setActive(false);
        customerRepository.save(customer);
        return ApiResponse.success(null);
    }
}
