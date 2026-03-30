package com.basilisk.api.finance.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class RegisterSaleRequest {

    @NotNull
    @Min(1)
    private Integer quantity;

    /** Preço unitário. Se nulo, usa o preço padrão do produto. */
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal unitPrice;

    /** Data da venda. Se nula, usa a data atual. */
    private LocalDate saleDate;

    @Size(max = 500)
    private String notes;
}
