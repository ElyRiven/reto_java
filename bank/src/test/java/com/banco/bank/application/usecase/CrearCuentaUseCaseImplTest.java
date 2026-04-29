package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.ClienteNoEncontradoException;
import com.banco.bank.domain.exception.NumeroCuentaDuplicadoException;
import com.banco.bank.domain.exception.NumeroCuentaInvalidoException;
import com.banco.bank.domain.exception.TipoCuentaInvalidoException;
import com.banco.bank.domain.model.Cliente;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.port.out.ClienteRepositoryPort;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class CrearCuentaUseCaseImplTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @InjectMocks
    private CrearCuentaUseCaseImpl useCase;

    @Test
    void debe_CrearCuenta_Cuando_DatosSonValidos() {
        var cliente = new Cliente();
        cliente.setClienteId(UUID.randomUUID());
        cliente.setNombre("Ana");
        cliente.setEstado(true);

        var cuenta = buildCuenta(cliente.getClienteId(), "123456789", "Ahorros", new BigDecimal("1500.99"), true);

        when(clienteRepositoryPort.findByClienteId(cliente.getClienteId())).thenReturn(Optional.of(cliente));
        when(cuentaRepositoryPort.existsByNumeroCuentaAndDeletedAtIsNull("123456789")).thenReturn(false);
        when(cuentaRepositoryPort.save(any(Cuenta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.execute(cuenta);

        assertNotNull(result.getCuentaId());
        assertNotNull(result.getCreatedAt());
        assertEquals("123456789", result.getNumeroCuenta());
        assertEquals(new BigDecimal("1500.99"), result.getSaldoInicial());
    }

    @Test
    void debe_LanzarClienteNoEncontradoException_Cuando_ClienteNoExiste() {
        var clienteId = UUID.randomUUID();
        var cuenta = buildCuenta(clienteId, "123456789", "Ahorros", new BigDecimal("100.00"), true);
        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class, () -> useCase.execute(cuenta));
        verify(cuentaRepositoryPort, never()).save(any());
    }

    @Test
    void debe_LanzarNumeroCuentaInvalidoException_Cuando_NumeroTieneCaracteresInvalidos() {
        var clienteId = UUID.randomUUID();
        var cliente = new Cliente();
        cliente.setClienteId(clienteId);
        var cuenta = buildCuenta(clienteId, "12A-45", "Ahorros", new BigDecimal("100.00"), true);

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(cliente));

        assertThrows(NumeroCuentaInvalidoException.class, () -> useCase.execute(cuenta));
        verify(cuentaRepositoryPort, never()).save(any());
    }

    @Test
    void debe_LanzarTipoCuentaInvalidoException_Cuando_TipoCuentaNoPermitido() {
        var clienteId = UUID.randomUUID();
        var cliente = new Cliente();
        cliente.setClienteId(clienteId);
        var cuenta = buildCuenta(clienteId, "123456789", "VIP", new BigDecimal("100.00"), true);

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(cliente));

        assertThrows(TipoCuentaInvalidoException.class, () -> useCase.execute(cuenta));
        verify(cuentaRepositoryPort, never()).save(any());
    }

    @Test
    void debe_LanzarNumeroCuentaDuplicadoException_Cuando_NumeroCuentaYaExiste() {
        var clienteId = UUID.randomUUID();
        var cliente = new Cliente();
        cliente.setClienteId(clienteId);
        var cuenta = buildCuenta(clienteId, "123456789", "Ahorros", new BigDecimal("100.00"), true);

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaRepositoryPort.existsByNumeroCuentaAndDeletedAtIsNull("123456789")).thenReturn(true);

        assertThrows(NumeroCuentaDuplicadoException.class, () -> useCase.execute(cuenta));
        verify(cuentaRepositoryPort, never()).save(any());
    }

    private Cuenta buildCuenta(UUID clienteId, String numero, String tipo, BigDecimal saldo, Boolean estado) {
        var cuenta = new Cuenta();
        var clienteRef = new Cliente();
        clienteRef.setClienteId(clienteId);
        cuenta.setCliente(clienteRef);
        cuenta.setNumeroCuenta(numero);
        cuenta.setTipoCuenta(tipo);
        cuenta.setSaldoInicial(saldo);
        cuenta.setEstado(estado);
        return cuenta;
    }
}
