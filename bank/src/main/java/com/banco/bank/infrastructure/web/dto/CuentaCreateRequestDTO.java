package com.banco.bank.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record CuentaCreateRequestDTO(
        @NotNull UUID clienteId,
        @NotBlank @Pattern(regexp = "^[0-9]+$", message = "Número de cuenta debe contener solo dígitos [0-9]") String numeroCuenta,
        @NotNull String tipoCuenta,
        @NotNull @DecimalMin(value = "0.00") BigDecimal saldoInicial,
        @NotNull Boolean estado
) {}
