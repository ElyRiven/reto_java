package com.banco.bank.infrastructure.web.dto;

import java.math.BigDecimal;

public record CuentaPatchRequestDTO(
        String tipoCuenta,
        BigDecimal saldoInicial,
        Boolean estado
) {}
