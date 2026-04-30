package com.banco.bank.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record MovimientoCreateRequestDTO(
        @NotNull UUID cuentaId,
        @NotBlank String tipoMovimiento,
        @NotNull BigDecimal valor
) {}
