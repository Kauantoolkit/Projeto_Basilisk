package com.basilisk.api.finance.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MarkAsPaidRequest {
    /** Data efetiva do pagamento. Se nulo, usa a data atual. */
    private LocalDate paidDate;
}
