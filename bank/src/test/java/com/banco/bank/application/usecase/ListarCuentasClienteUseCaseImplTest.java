package com.banco.bank.application.usecase;

import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarCuentasClienteUseCaseImplTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private ListarCuentasClienteUseCaseImpl useCase;

    @Test
    void debe_ListarCuentasPaginadas_Cuando_ClienteTieneCuentas() {
        var clienteId = UUID.randomUUID();
        var pageable = PageRequest.of(0, 10);

        var cuenta = new Cuenta();
        cuenta.setCuentaId(UUID.randomUUID());
        Page<Cuenta> expected = new PageImpl<>(List.of(cuenta), pageable, 1);

        when(cuentaRepositoryPort.findByClienteId(clienteId, pageable)).thenReturn(expected);

        var result = useCase.execute(clienteId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(cuenta.getCuentaId(), result.getContent().get(0).getCuentaId());
    }

    @Test
    void debe_RetornarPaginaVacia_Cuando_ClienteNoTieneCuentas() {
        var clienteId = UUID.randomUUID();
        var pageable = PageRequest.of(0, 10);
        Page<Cuenta> expected = Page.empty(pageable);

        when(cuentaRepositoryPort.findByClienteId(clienteId, pageable)).thenReturn(expected);

        var result = useCase.execute(clienteId, pageable);

        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }
}
