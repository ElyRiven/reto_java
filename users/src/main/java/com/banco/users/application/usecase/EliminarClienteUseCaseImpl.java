package com.banco.users.application.usecase;

import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EliminarClienteUseCaseImpl implements EliminarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public void execute(UUID clienteId) {
        clienteRepositoryPort.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));
        clienteRepositoryPort.softDelete(clienteId);
    }
}
