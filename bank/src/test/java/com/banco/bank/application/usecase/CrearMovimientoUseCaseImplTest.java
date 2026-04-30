package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearMovimientoUseCaseImplTest {

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private CrearMovimientoUseCaseImpl useCase;

    @Test
    void debe_RegistrarDeposito_Cuando_DatosValidos() {
        var cuentaId = UUID.randomUUID();
        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setSaldoInicial(new BigDecimal("1000.00"));
        cuenta.setDeletedAt(null);

        var movimiento = movimiento(cuentaId, "Depósito", new BigDecimal("500.00"));

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaRepositoryPort.save(any(Cuenta.class))).thenAnswer(i -> i.getArgument(0));
        when(movimientoRepositoryPort.save(any(Movimiento.class))).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute(movimiento);

        assertNotNull(result.getMovimientoId());
        assertEquals(new BigDecimal("1500.00"), result.getSaldo());
        assertNotNull(result.getCreatedAt());
        verify(cuentaRepositoryPort).save(any(Cuenta.class));
    }

    @Test
    void debe_RegistrarRetiroSaldoCero_Cuando_ValorExactoDisponible() {
        var cuentaId = UUID.randomUUID();
        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setSaldoInicial(new BigDecimal("500.00"));
        cuenta.setDeletedAt(null);

        var movimiento = movimiento(cuentaId, "Retiro", new BigDecimal("-500.00"));

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaRepositoryPort.save(any(Cuenta.class))).thenAnswer(i -> i.getArgument(0));
        when(movimientoRepositoryPort.save(any(Movimiento.class))).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute(movimiento);

        assertEquals(new BigDecimal("0.00"), result.getSaldo());
    }

    @Test
    void debe_LanzarSaldoNoDisponibleException_Cuando_RetiroSuperaSaldo() {
        var cuentaId = UUID.randomUUID();
        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setSaldoInicial(new BigDecimal("300.00"));
        cuenta.setDeletedAt(null);

        var movimiento = movimiento(cuentaId, "Retiro", new BigDecimal("-500.00"));

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));

        assertThrows(SaldoNoDisponibleException.class, () -> useCase.execute(movimiento));
        verify(movimientoRepositoryPort, never()).save(any());
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_CuentaNoExiste() {
        var cuentaId = UUID.randomUUID();
        var movimiento = movimiento(cuentaId, "Depósito", new BigDecimal("100.00"));
        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.empty());

        assertThrows(CuentaNoEncontradaException.class, () -> useCase.execute(movimiento));
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_CuentaEstaSoftDeleted() {
        var cuentaId = UUID.randomUUID();
        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setDeletedAt(Instant.now());
        var movimiento = movimiento(cuentaId, "Depósito", new BigDecimal("100.00"));

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));

        assertThrows(CuentaNoEncontradaException.class, () -> useCase.execute(movimiento));
    }

    private Movimiento movimiento(UUID cuentaId, String tipo, BigDecimal valor) {
        var cuentaRef = new Cuenta();
        cuentaRef.setCuentaId(cuentaId);
        var m = new Movimiento();
        m.setCuenta(cuentaRef);
        m.setTipoMovimiento(tipo);
        m.setValor(valor);
        return m;
    }
}
