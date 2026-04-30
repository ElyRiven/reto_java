package com.banco.bank.infrastructure.persistence;

import com.banco.bank.domain.model.TipoCuentaEnum;
import com.banco.bank.infrastructure.entity.ClienteEntity;
import com.banco.bank.infrastructure.entity.CuentaEntity;
import com.banco.bank.infrastructure.entity.MovimientoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteRepositoryAdapterTest {

    @Mock
    private ReporteJpaRepository reporteJpaRepository;

    @InjectMocks
    private ReporteRepositoryAdapter adapter;

    @Test
    void debe_MapearEstadoCuentaYCalcularSaldoInicial_Cuando_ConsultaEsValida() {
        var clienteId = UUID.randomUUID();
        var inicio = LocalDate.of(2022, 2, 1);
        var fin = LocalDate.of(2022, 2, 28);

        var cliente = new ClienteEntity();
        cliente.setClienteId(clienteId);
        cliente.setNombre("Marianela Montalvo");

        var cuenta = new CuentaEntity();
        cuenta.setCuentaId(UUID.randomUUID());
        cuenta.setClienteId(clienteId);
        cuenta.setCliente(cliente);
        cuenta.setNumeroCuenta("110834");
        cuenta.setTipoCuenta(TipoCuentaEnum.AHORROS);
        cuenta.setEstado(true);

        var movimiento = new MovimientoEntity();
        movimiento.setMovimientoId(UUID.randomUUID());
        movimiento.setCuentaId(cuenta.getCuentaId());
        movimiento.setCuenta(cuenta);
        movimiento.setFecha(Instant.parse("2022-02-11T10:15:30Z"));
        movimiento.setValor(new BigDecimal("-200.00"));
        movimiento.setSaldo(new BigDecimal("500.00"));

        when(reporteJpaRepository.findMovimientosByClienteIdAndFecha(clienteId, inicio.atStartOfDay(ZoneOffset.UTC).toInstant(),
                fin.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC).toInstant())).thenReturn(List.of(movimiento));

        var result = adapter.findEstadoCuenta(clienteId, inicio, fin);

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2022, 2, 11), result.get(0).fecha());
        assertEquals("Marianela Montalvo", result.get(0).cliente());
        assertEquals("110834", result.get(0).numeroCuenta());
        assertEquals("Ahorros", result.get(0).tipo());
        assertEquals(new BigDecimal("700.00"), result.get(0).saldoInicial());
        assertEquals(new BigDecimal("-200.00"), result.get(0).movimiento());
        assertEquals(new BigDecimal("500.00"), result.get(0).saldoDisponible());
    }

    @Test
    void debe_EnviarRangoUTCInicioFinDia_Cuando_TransformaFechasParaConsulta() {
        var clienteId = UUID.randomUUID();
        var inicio = LocalDate.of(2026, 1, 1);
        var fin = LocalDate.of(2026, 1, 31);

        when(reporteJpaRepository.findMovimientosByClienteIdAndFecha(clienteId, inicio.atStartOfDay(ZoneOffset.UTC).toInstant(),
                fin.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC).toInstant())).thenReturn(List.of());

        adapter.findEstadoCuenta(clienteId, inicio, fin);

        var inicioCaptor = ArgumentCaptor.forClass(Instant.class);
        var finCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(reporteJpaRepository).findMovimientosByClienteIdAndFecha(org.mockito.ArgumentMatchers.eq(clienteId), inicioCaptor.capture(), finCaptor.capture());

        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), inicioCaptor.getValue());
        assertEquals(fin.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC).toInstant(), finCaptor.getValue());
    }
}
