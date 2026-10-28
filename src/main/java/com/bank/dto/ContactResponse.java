package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContactResponse {

    private final Long id;
    private final String value;
}
