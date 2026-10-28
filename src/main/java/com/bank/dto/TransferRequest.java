package com.bank.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @NotNull
    private Long toUserId;

    @NotNull
    @DecimalMin(value = "0.01", message = "value must be positive")
    private BigDecimal value;
}
