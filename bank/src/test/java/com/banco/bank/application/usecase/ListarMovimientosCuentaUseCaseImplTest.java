package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import com.banco.bank.domain.port.out.MovimientoRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarMovimientosCuentaUseCaseImplTest {

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private ListarMovimientosCuentaUseCaseImpl useCase;

    @Test
    void debe_ListarMovimientos_Cuando_CuentaExiste() {
        var cuentaId = UUID.randomUUID();
        var pageable = PageRequest.of(0, 10);
        var cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setDeletedAt(null);

        var movimiento = new Movimiento();
        movimiento.setMovimientoId(UUID.randomUUID());
        Page<Movimiento> page = new PageImpl<>(List.of(movimiento), pageable, 1);

        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(movimientoRepositoryPort.findByCuentaId(cuentaId, pageable, null, null, null)).thenReturn(page);

        var result = useCase.execute(cuentaId, pageable, null, null, null);

        assertEquals(1, result.getTotalElements());
        assertEquals(movimiento.getMovimientoId(), result.getContent().get(0).getMovimientoId());
    }

    @Test
    void debe_LanzarCuentaNoEncontradaException_Cuando_CuentaNoExiste() {
        var cuentaId = UUID.randomUUID();
        var pageable = PageRequest.of(0, 10);
        when(cuentaRepositoryPort.findByCuentaId(cuentaId)).thenReturn(Optional.empty());

        assertThrows(CuentaNoEncontradaException.class,
                () -> useCase.execute(cuentaId, pageable, Instant.now().minusSeconds(3600), Instant.now(), "Depósito"));
    }
}
