package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerCuentaUseCaseImplTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private ObtenerCuentaUseCaseImpl useCase;

    @Test
    void debe_RetornarCuenta_Cuando_ExisteYNoEstaEliminada() {
        var cuentaId = UUID.randomUUID();
        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setDeletedAt(null);

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));

        var result = useCase.execute(cuentaId);
        assertEquals(cuentaId, result.getCuentaId());
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_NoExiste() {
        var cuentaId = UUID.randomUUID();
        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.empty());

        assertThrows(CuentaNoEncontradaException.class, () -> useCase.execute(cuentaId));
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_EstaSoftDeleted() {
        var cuentaId = UUID.randomUUID();
        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setDeletedAt(Instant.now());

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));

        assertThrows(CuentaNoEncontradaException.class, () -> useCase.execute(cuentaId));
    }
}
