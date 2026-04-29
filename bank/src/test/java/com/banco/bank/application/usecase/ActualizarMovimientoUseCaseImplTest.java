package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.MovimientoNoEncontradoException;
import com.banco.bank.domain.exception.SaldoNoDisponibleException;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import com.banco.bank.domain.port.out.MovimientoRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarMovimientoUseCaseImplTest {

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private ActualizarMovimientoUseCaseImpl useCase;

    @Test
    void debe_ActualizarMovimiento_Cuando_DatosValidos() {
        var cuentaId = UUID.randomUUID();
        var movimientoId = UUID.randomUUID();
        var existing = movimiento(cuentaId, movimientoId, "Depósito", new BigDecimal("500.00"), null);

        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setSaldoInicial(new BigDecimal("1500.00"));
        cuenta.setDeletedAt(null);

        var updates = new Movimiento();
        updates.setTipoMovimiento("Retiro");
        updates.setValor(new BigDecimal("-200.00"));

        when(movimientoRepositoryPort.findById(movimientoId)).thenReturn(Optional.of(existing));
        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaRepositoryPort.save(cuenta)).thenReturn(cuenta);
        when(movimientoRepositoryPort.save(existing)).thenReturn(existing);

        var result = useCase.execute(movimientoId, updates);

        assertEquals("Retiro", result.getTipoMovimiento());
        assertEquals(new BigDecimal("-200.00"), result.getValor());
        assertEquals(new BigDecimal("800.00"), result.getSaldo());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void debe_LanzarSaldoNoDisponibleException_Cuando_ReversionMasNuevoValorQuedaNegativo() {
        var cuentaId = UUID.randomUUID();
        var movimientoId = UUID.randomUUID();
        var existing = movimiento(cuentaId, movimientoId, "Depósito", new BigDecimal("100.00"), null);

        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setSaldoInicial(new BigDecimal("100.00"));
        cuenta.setDeletedAt(null);

        var updates = new Movimiento();
        updates.setValor(new BigDecimal("-200.00"));

        when(movimientoRepositoryPort.findById(movimientoId)).thenReturn(Optional.of(existing));
        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));

        assertThrows(SaldoNoDisponibleException.class, () -> useCase.execute(movimientoId, updates));
    }

    @Test
    void debe_LanzarMovimientoNoEncontradoException_Cuando_MovimientoNoExiste() {
        var movimientoId = UUID.randomUUID();
        when(movimientoRepositoryPort.findById(movimientoId)).thenReturn(Optional.empty());

        assertThrows(MovimientoNoEncontradoException.class, () -> useCase.execute(movimientoId, new Movimiento()));
    }

    @Test
    void debe_LanzarMovimientoNoEncontradoException_Cuando_MovimientoSoftDeleted() {
        var movimientoId = UUID.randomUUID();
        var existing = new Movimiento();
        existing.setDeletedAt(Instant.now());
        when(movimientoRepositoryPort.findById(movimientoId)).thenReturn(Optional.of(existing));

        assertThrows(MovimientoNoEncontradoException.class, () -> useCase.execute(movimientoId, new Movimiento()));
    }

    private Movimiento movimiento(UUID cuentaId, UUID movimientoId, String tipo, BigDecimal valor, Instant deletedAt) {
        var cuentaRef = new Cuenta();
        cuentaRef.setCuentaId(cuentaId);
        var m = new Movimiento();
        m.setMovimientoId(movimientoId);
        m.setCuenta(cuentaRef);
        m.setTipoMovimiento(tipo);
        m.setValor(valor);
        m.setSaldo(new BigDecimal("0"));
        m.setDeletedAt(deletedAt);
        return m;
    }
}
