package com.banco.bank.application.usecase;

import com.banco.bank.domain.model.reporte.EstadoCuentaReporte;
import com.banco.bank.domain.port.in.ObtenerEstadoCuentaUseCase;
import com.banco.bank.domain.port.out.ReporteRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObtenerEstadoCuentaUseCaseImpl implements ObtenerEstadoCuentaUseCase {

    private final ReporteRepositoryPort reporteRepositoryPort;

    @Override
    public List<EstadoCuentaReporte> execute(UUID clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Generando reporte de estado de cuenta: clienteId={}, fechaInicio={}, fechaFin={}",
                clienteId, fechaInicio, fechaFin);
        return reporteRepositoryPort.findEstadoCuenta(clienteId, fechaInicio, fechaFin);
    }
}
