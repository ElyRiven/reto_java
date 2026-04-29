package com.banco.users.application.usecase;

import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActualizarParcialClienteUseCaseImpl implements ActualizarParcialClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Cliente execute(UUID clienteId, PatchClienteCommand command) {
        var existente = clienteRepositoryPort.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));

        Optional.ofNullable(command.nombre()).ifPresent(existente::setNombre);
        Optional.ofNullable(command.genero()).ifPresent(existente::setGenero);
        Optional.ofNullable(command.edad()).ifPresent(existente::setEdad);
        Optional.ofNullable(command.identificacion()).ifPresent(existente::setIdentificacion);
        Optional.ofNullable(command.direccion()).ifPresent(existente::setDireccion);
        Optional.ofNullable(command.telefono()).ifPresent(existente::setTelefono);
        Optional.ofNullable(command.contrasena())
                .ifPresent(p -> existente.setContrasena(passwordEncoder.encode(p)));
        Optional.ofNullable(command.estado()).ifPresent(existente::setEstado);
        existente.setUpdatedAt(Instant.now());

        return clienteRepositoryPort.save(existente);
    }
}
