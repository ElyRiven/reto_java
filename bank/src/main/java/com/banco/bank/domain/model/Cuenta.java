package com.banco.bank.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Cuenta {

    private UUID cuentaId;
    private Cliente cliente;
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldoInicial;
    private Boolean estado;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
