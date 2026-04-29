package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.exception.CuentaTieneMovimientosException;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import com.banco.bank.domain.port.out.MovimientoRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarCuentaUseCaseImplTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @InjectMocks
    private EliminarCuentaUseCaseImpl useCase;

    @Test
    void debe_EliminarCuentaSoftDelete_Cuando_NoTieneMovimientos() {
        var cuentaId = UUID.randomUUID();
        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setDeletedAt(null);

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(movimientoRepositoryPort.existsByCuentaId(cuentaId)).thenReturn(false);

        assertDoesNotThrow(() -> useCase.execute(cuentaId));
        verify(cuentaRepositoryPort).delete(cuentaId);
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_NoExisteCuenta() {
        var cuentaId = UUID.randomUUID();
        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.empty());

        assertThrows(CuentaNoEncontradaException.class, () -> useCase.execute(cuentaId));
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_CuentaYaFueEliminada() {
        var cuentaId = UUID.randomUUID();
        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setDeletedAt(Instant.now());

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));

        assertThrows(CuentaNoEncontradaException.class, () -> useCase.execute(cuentaId));
    }

    @Test
    void debe_LanzarCuentaTieneMovimientosException_Cuando_ExistenMovimientos() {
        var cuentaId = UUID.randomUUID();
        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setDeletedAt(null);

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(movimientoRepositoryPort.existsByCuentaId(cuentaId)).thenReturn(true);

        assertThrows(CuentaTieneMovimientosException.class, () -> useCase.execute(cuentaId));
    }
}
