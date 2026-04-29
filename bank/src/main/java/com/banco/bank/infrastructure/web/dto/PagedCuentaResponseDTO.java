package com.banco.bank.infrastructure.web.dto;

import java.util.List;

public record PagedCuentaResponseDTO(
        List<CuentaResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
