package com.banco.bank.domain.port.out;

import com.banco.bank.domain.model.reporte.EstadoCuentaReporte;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReporteRepositoryPort {

    List<EstadoCuentaReporte> findEstadoCuenta(UUID clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
