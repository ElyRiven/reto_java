package com.banco.bank.domain.model.reporte;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EstadoCuentaReporte(
        LocalDate fecha,
        String cliente,
        String numeroCuenta,
        String tipo,
        BigDecimal saldoInicial,
        Boolean estado,
        BigDecimal movimiento,
        BigDecimal saldoDisponible) {
}
