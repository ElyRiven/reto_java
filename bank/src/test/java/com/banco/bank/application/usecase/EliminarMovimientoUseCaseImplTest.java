package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.MovimientoNoEncontradoException;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarMovimientoUseCaseImplTest {

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private EliminarMovimientoUseCaseImpl useCase;

    @Test
    void debe_EliminarMovimientoSoftDelete_Cuando_MovimientoExiste() {
        var cuentaId = UUID.randomUUID();
        var movimientoId = UUID.randomUUID();

        var cuentaRef = new Cuenta();
        cuentaRef.setCuentaId(cuentaId);
        var movimiento = new Movimiento();
        movimiento.setMovimientoId(movimientoId);
        movimiento.setCuenta(cuentaRef);
        movimiento.setValor(new BigDecimal("500.00"));
        movimiento.setDeletedAt(null);

        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setSaldoInicial(new BigDecimal("1500.00"));
        cuenta.setDeletedAt(null);

        when(movimientoRepositoryPort.findById(movimientoId)).thenReturn(Optional.of(movimiento));
        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaRepositoryPort.save(cuenta)).thenReturn(cuenta);

        assertDoesNotThrow(() -> useCase.execute(movimientoId));

        assertEquals(new BigDecimal("1000.00"), cuenta.getSaldoInicial());
        verify(cuentaRepositoryPort).save(cuenta);
        verify(movimientoRepositoryPort).softDelete(movimientoId);
    }

    @Test
    void debe_LanzarMovimientoNoEncontradoException_Cuando_MovimientoNoExiste() {
        var movimientoId = UUID.randomUUID();
        when(movimientoRepositoryPort.findById(movimientoId)).thenReturn(Optional.empty());

        assertThrows(MovimientoNoEncontradoException.class, () -> useCase.execute(movimientoId));
    }

    @Test
    void debe_LanzarMovimientoNoEncontradoException_Cuando_MovimientoSoftDeleted() {
        var movimientoId = UUID.randomUUID();
        var movimiento = new Movimiento();
        movimiento.setDeletedAt(Instant.now());

        when(movimientoRepositoryPort.findById(movimientoId)).thenReturn(Optional.of(movimiento));

        assertThrows(MovimientoNoEncontradoException.class, () -> useCase.execute(movimientoId));
    }
}
