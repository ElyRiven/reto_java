package com.banco.bank.infrastructure.persistence;

import com.banco.bank.domain.model.reporte.EstadoCuentaReporte;
import com.banco.bank.domain.port.out.ReporteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReporteRepositoryAdapter implements ReporteRepositoryPort {

    private final ReporteJpaRepository reporteJpaRepository;

    @Override
    public List<EstadoCuentaReporte> findEstadoCuenta(UUID clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        var inicio = fechaInicio.atStartOfDay(ZoneOffset.UTC).toInstant();
        var fin = fechaFin.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC).toInstant();

        return reporteJpaRepository.findMovimientosByClienteIdAndFecha(clienteId, inicio, fin)
                .stream()
                .map(m -> {
                    var cuenta = m.getCuenta();
                    var cliente = cuenta.getCliente();
                    var saldoDisponible = m.getSaldo();
                    var movimientoValor = m.getValor();
                    var saldoInicial = saldoDisponible.subtract(movimientoValor);
                    var fecha = m.getFecha().atZone(ZoneOffset.UTC).toLocalDate();
                    return new EstadoCuentaReporte(
                            fecha,
                            cliente.getNombre(),
                            cuenta.getNumeroCuenta(),
                            cuenta.getTipoCuenta().getDisplayName(),
                            saldoInicial,
                            cuenta.getEstado(),
                            movimientoValor,
                            saldoDisponible
                    );
                })
                .toList();
    }
}
