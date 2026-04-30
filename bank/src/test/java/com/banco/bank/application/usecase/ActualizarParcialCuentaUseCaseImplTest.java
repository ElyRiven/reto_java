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
class ActualizarParcialCuentaUseCaseImplTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private ActualizarParcialCuentaUseCaseImpl useCase;

    @Test
    void debe_ActualizarSoloCamposEnviados_Cuando_PatchEsValido() {
        var cuentaId = UUID.randomUUID();
        var existing = new Cuenta();
        existing.setCuentaId(cuentaId);
        existing.setTipoCuenta("Ahorros");
        existing.setSaldoInicial(new BigDecimal("100.00"));
        existing.setEstado(true);
        existing.setDeletedAt(null);

        var patch = new Cuenta();
        patch.setEstado(false);

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(existing));
        when(cuentaRepositoryPort.save(existing)).thenReturn(existing);

        var result = useCase.execute(cuentaId, patch);

        assertEquals(false, result.getEstado());
        assertEquals("Ahorros", result.getTipoCuenta());
        assertNotNull(result.getUpdatedAt());
        verify(cuentaRepositoryPort).save(existing);
    }

    @Test
    void debe_LanzarIllegalArgumentException_Cuando_PatchVacio() {
        var cuentaId = UUID.randomUUID();
        var patch = new Cuenta();

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(cuentaId, patch));
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_NoExiste() {
        var cuentaId = UUID.randomUUID();
        var patch = new Cuenta();
        patch.setEstado(true);
        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.empty());

        assertThrows(CuentaNoEncontradaException.class, () -> useCase.execute(cuentaId, patch));
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_EstaSoftDeleted() {
        var cuentaId = UUID.randomUUID();
        var existing = new Cuenta();
        existing.setDeletedAt(Instant.now());
        var patch = new Cuenta();
        patch.setEstado(true);

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(existing));

        assertThrows(CuentaNoEncontradaException.class, () -> useCase.execute(cuentaId, patch));
    }

    @Test
    void debe_LanzarTipoCuentaInvalidoException_Cuando_TipoCuentaNoPermitido() {
        var cuentaId = UUID.randomUUID();
        var existing = new Cuenta();
        existing.setDeletedAt(null);
        var patch = new Cuenta();
        patch.setTipoCuenta("X");

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(existing));

        assertThrows(TipoCuentaInvalidoException.class, () -> useCase.execute(cuentaId, patch));
    }
}
