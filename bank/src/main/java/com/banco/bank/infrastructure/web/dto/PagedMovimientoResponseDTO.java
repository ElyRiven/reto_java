package com.banco.bank.infrastructure.web.dto;

import java.util.List;

public record PagedMovimientoResponseDTO(
        List<MovimientoResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
