package com.banco.users.infrastructure.web;

import com.banco.users.application.usecase.ActualizarClienteUseCase;
import com.banco.users.application.usecase.ActualizarParcialClienteUseCase;
import com.banco.users.application.usecase.ConsultarClienteUseCase;
import com.banco.users.application.usecase.CrearClienteUseCase;
import com.banco.users.application.usecase.EliminarClienteUseCase;
import com.banco.users.application.usecase.PatchClienteCommand;
import com.banco.users.domain.model.Cliente;
import com.banco.users.infrastructure.mapper.ClienteMapper;
import com.banco.users.infrastructure.web.dto.ClienteCreateRequestDTO;
import com.banco.users.infrastructure.web.dto.ClientePatchRequestDTO;
import com.banco.users.infrastructure.web.dto.ClienteResponseDTO;
import com.banco.users.infrastructure.web.dto.ClienteUpdateRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final CrearClienteUseCase crearClienteUseCase;
    private final ConsultarClienteUseCase consultarClienteUseCase;
    private final ActualizarClienteUseCase actualizarClienteUseCase;
    private final ActualizarParcialClienteUseCase actualizarParcialClienteUseCase;
    private final EliminarClienteUseCase eliminarClienteUseCase;
    private final ClienteMapper clienteMapper;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crear(@Valid @RequestBody ClienteCreateRequestDTO dto) {
        var result = crearClienteUseCase.execute(dto.personaId(), dto.contrasena(), dto.estado());
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteMapper.toResponseDTO(result));
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteResponseDTO> consultar(@PathVariable UUID clienteId) {
        var result = consultarClienteUseCase.execute(clienteId);
        return ResponseEntity.ok(clienteMapper.toResponseDTO(result));
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteResponseDTO> actualizar(
            @PathVariable UUID clienteId,
            @Valid @RequestBody ClienteUpdateRequestDTO dto) {
        var domainModel = new Cliente();
        domainModel.setNombre(dto.nombre());
        domainModel.setGenero(dto.genero());
        domainModel.setEdad(dto.edad());
        domainModel.setIdentificacion(dto.identificacion());
        domainModel.setDireccion(dto.direccion());
        domainModel.setTelefono(dto.telefono());
        domainModel.setContrasena(dto.contrasena());
        domainModel.setEstado(dto.estado());
        var result = actualizarClienteUseCase.execute(clienteId, domainModel);
        return ResponseEntity.ok(clienteMapper.toResponseDTO(result));
    }

    @PatchMapping("/{clienteId}")
    public ResponseEntity<ClienteResponseDTO> actualizarParcial(
            @PathVariable UUID clienteId,
            @Valid @RequestBody ClientePatchRequestDTO dto) {
        var command = new PatchClienteCommand(
                dto.nombre(),
                dto.genero(),
                dto.edad(),
                dto.identificacion(),
                dto.direccion(),
                dto.telefono(),
                dto.contrasena(),
                dto.estado()
        );
        var result = actualizarParcialClienteUseCase.execute(clienteId, command);
        return ResponseEntity.ok(clienteMapper.toResponseDTO(result));
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID clienteId) {
        eliminarClienteUseCase.execute(clienteId);
        return ResponseEntity.noContent().build();
    }
}
