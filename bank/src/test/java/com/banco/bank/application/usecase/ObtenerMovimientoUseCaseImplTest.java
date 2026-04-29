package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.MovimientoNoEncontradoException;
import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.port.out.MovimientoRepositoryPort;
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
class ObtenerMovimientoUseCaseImplTest {

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @InjectMocks
    private ObtenerMovimientoUseCaseImpl useCase;

    @Test
    void debe_RetornarMovimiento_Cuando_ExisteYNoEstaEliminado() {
        var id = UUID.randomUUID();
        var movimiento = new Movimiento();
        movimiento.setMovimientoId(id);
        movimiento.setDeletedAt(null);

        when(movimientoRepositoryPort.findById(id)).thenReturn(Optional.of(movimiento));

        var result = useCase.execute(id);
        assertEquals(id, result.getMovimientoId());
    }

    @Test
    void debe_LanzarMovimientoNoEncontradoException_Cuando_NoExiste() {
        var id = UUID.randomUUID();
        when(movimientoRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(MovimientoNoEncontradoException.class, () -> useCase.execute(id));
    }

    @Test
    void debe_LanzarMovimientoNoEncontradoException_Cuando_EstaSoftDeleted() {
        var id = UUID.randomUUID();
        var movimiento = new Movimiento();
        movimiento.setDeletedAt(Instant.now());

        when(movimientoRepositoryPort.findById(id)).thenReturn(Optional.of(movimiento));

        assertThrows(MovimientoNoEncontradoException.class, () -> useCase.execute(id));
    }
}
