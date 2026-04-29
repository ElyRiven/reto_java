package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.MovimientoNoEncontradoException;
import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.port.in.ObtenerMovimientoUseCase;
import com.banco.bank.domain.port.out.MovimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObtenerMovimientoUseCaseImpl implements ObtenerMovimientoUseCase {

    private final MovimientoRepositoryPort movimientoRepositoryPort;

    @Override
    public Movimiento execute(UUID movimientoId) {
        return movimientoRepositoryPort.findById(movimientoId)
                .filter(m -> m.getDeletedAt() == null)
                .orElseThrow(() -> new MovimientoNoEncontradoException("Movimiento no encontrado"));
    }
}
