package com.banco.bank.infrastructure.web.dto;

import com.banco.bank.domain.model.reporte.EstadoCuentaReporte;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public record EstadoCuentaReporteDTO(
        @JsonProperty("Fecha") String fecha,
        @JsonProperty("Cliente") String cliente,
        @JsonProperty("Numero Cuenta") String numeroCuenta,
        @JsonProperty("Tipo") String tipo,
        @JsonProperty("Saldo Inicial") BigDecimal saldoInicial,
        @JsonProperty("Estado") Boolean estado,
        @JsonProperty("Movimiento") BigDecimal movimiento,
        @JsonProperty("Saldo Disponible") BigDecimal saldoDisponible) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static EstadoCuentaReporteDTO fromDomain(EstadoCuentaReporte reporte) {
        return new EstadoCuentaReporteDTO(
                reporte.fecha().format(FORMATTER),
                reporte.cliente(),
                reporte.numeroCuenta(),
                reporte.tipo(),
                reporte.saldoInicial(),
                reporte.estado(),
                reporte.movimiento(),
                reporte.saldoDisponible()
        );
    }
}
