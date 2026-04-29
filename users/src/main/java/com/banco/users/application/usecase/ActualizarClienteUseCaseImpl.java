package com.banco.users.application.usecase;

import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActualizarClienteUseCaseImpl implements ActualizarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Cliente execute(UUID clienteId, Cliente datosActualizados) {
        var existente = clienteRepositoryPort.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));

        existente.setNombre(datosActualizados.getNombre());
        existente.setGenero(datosActualizados.getGenero());
        existente.setEdad(datosActualizados.getEdad());
        existente.setIdentificacion(datosActualizados.getIdentificacion());
        existente.setDireccion(datosActualizados.getDireccion());
        existente.setTelefono(datosActualizados.getTelefono());
        existente.setContrasena(passwordEncoder.encode(datosActualizados.getContrasena()));
        existente.setEstado(datosActualizados.getEstado());
        existente.setUpdatedAt(Instant.now());

        return clienteRepositoryPort.save(existente);
    }
}
