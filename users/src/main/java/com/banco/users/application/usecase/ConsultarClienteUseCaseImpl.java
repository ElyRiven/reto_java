package com.banco.users.application.usecase;

import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultarClienteUseCaseImpl implements ConsultarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public Cliente execute(UUID clienteId) {
        return clienteRepositoryPort.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));
    }
}
