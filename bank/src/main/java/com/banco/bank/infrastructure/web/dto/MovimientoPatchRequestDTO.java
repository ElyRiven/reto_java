package com.banco.bank.infrastructure.web.dto;

import java.math.BigDecimal;

public record MovimientoPatchRequestDTO(
        String tipoMovimiento,
        BigDecimal valor
) {}
