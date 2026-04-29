package com.banco.bank.infrastructure.web.dto;

import java.math.BigDecimal;

public record CuentaUpdateRequestDTO(
        String tipoCuenta,
        BigDecimal saldoInicial,
        Boolean estado
) {}
