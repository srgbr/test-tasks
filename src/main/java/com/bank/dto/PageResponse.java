package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;

    public static <T> PageResponse<T> of(Page<?> source, List<T> content) {
        return new PageResponse<>(content, source.getNumber(), source.getSize(), source.getTotalElements());
    }
}
