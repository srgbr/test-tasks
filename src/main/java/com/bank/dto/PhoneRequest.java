package com.bank.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PhoneRequest {

    @NotBlank
    @Pattern(regexp = "\\d{11,13}", message = "phone must be 11-13 digits")
    private String phone;
}
