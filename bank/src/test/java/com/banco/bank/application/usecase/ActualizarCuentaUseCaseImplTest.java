package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.exception.TipoCuentaInvalidoException;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarCuentaUseCaseImplTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private ActualizarCuentaUseCaseImpl useCase;

    @Test
    void debe_ActualizarCuenta_Cuando_DatosSonValidos() {
        var cuentaId = UUID.randomUUID();
        var existing = new Cuenta();
        existing.setCuentaId(cuentaId);
        existing.setTipoCuenta("Ahorros");
        existing.setSaldoInicial(new BigDecimal("100.00"));
        existing.setEstado(true);
        existing.setDeletedAt(null);

        var updates = new Cuenta();
        updates.setTipoCuenta("Corriente");
        updates.setSaldoInicial(new BigDecimal("999.99"));
        updates.setEstado(false);

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(existing));
        when(cuentaRepositoryPort.save(existing)).thenReturn(existing);

        var result = useCase.execute(cuentaId, updates);

        assertEquals("Corriente", result.getTipoCuenta());
        assertEquals(new BigDecimal("999.99"), result.getSaldoInicial());
        assertEquals(false, result.getEstado());
        assertNotNull(result.getUpdatedAt());
        verify(cuentaRepositoryPort).save(existing);
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_CuentaNoExiste() {
        var cuentaId = UUID.randomUUID();
        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.empty());

        assertThrows(CuentaNoEncontradaException.class, () -> useCase.execute(cuentaId, new Cuenta()));
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_CuentaEstaEliminada() {
        var cuentaId = UUID.randomUUID();
        var existing = new Cuenta();
        existing.setDeletedAt(Instant.now());

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(existing));

        assertThrows(CuentaNoEncontradaException.class, () -> useCase.execute(cuentaId, new Cuenta()));
    }

    @Test
    void debe_LanzarTipoCuentaInvalidoException_Cuando_TipoCuentaNoEsPermitido() {
        var cuentaId = UUID.randomUUID();
        var existing = new Cuenta();
        existing.setDeletedAt(null);

        var updates = new Cuenta();
        updates.setTipoCuenta("Platino");

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(existing));

        assertThrows(TipoCuentaInvalidoException.class, () -> useCase.execute(cuentaId, updates));
    }
}
