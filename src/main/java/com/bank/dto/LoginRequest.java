package com.bank.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequest {

    private String email;
    private String phone;

    @NotBlank
    private String password;
}
