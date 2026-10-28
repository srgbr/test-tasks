package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String name;
    private final LocalDate dateOfBirth;
    private final List<String> emails;
    private final List<String> phones;
}
