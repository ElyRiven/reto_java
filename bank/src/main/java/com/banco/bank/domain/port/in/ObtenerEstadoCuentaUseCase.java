package com.banco.bank.domain.port.in;

import com.banco.bank.domain.model.reporte.EstadoCuentaReporte;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ObtenerEstadoCuentaUseCase {

    List<EstadoCuentaReporte> execute(UUID clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
