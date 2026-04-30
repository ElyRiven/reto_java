package com.banco.bank.infrastructure.web.dto;

import java.math.BigDecimal;

public record MovimientoUpdateRequestDTO(
        String tipoMovimiento,
        BigDecimal valor
) {}
