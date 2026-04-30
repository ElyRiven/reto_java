package com.banco.users.infrastructure.web;

import com.banco.users.application.usecase.ActualizarParcialPersonaUseCase;
import com.banco.users.application.usecase.ActualizarPersonaUseCase;
import com.banco.users.application.usecase.ConsultarPersonaUseCase;
import com.banco.users.application.usecase.EliminarPersonaUseCase;
import com.banco.users.application.usecase.PatchPersonaCommand;
import com.banco.users.application.usecase.RegistrarPersonaUseCase;
import com.banco.users.infrastructure.mapper.PersonaMapper;
import com.banco.users.infrastructure.web.dto.PersonaPatchRequestDTO;
import com.banco.users.infrastructure.web.dto.PersonaRequestDTO;
import com.banco.users.infrastructure.web.dto.PersonaResponseDTO;
import com.banco.users.infrastructure.web.dto.PersonaUpdateRequestDTO;
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
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final RegistrarPersonaUseCase registrarPersonaUseCase;
    private final ConsultarPersonaUseCase consultarPersonaUseCase;
    private final ActualizarPersonaUseCase actualizarPersonaUseCase;
    private final ActualizarParcialPersonaUseCase actualizarParcialPersonaUseCase;
    private final EliminarPersonaUseCase eliminarPersonaUseCase;
    private final PersonaMapper personaMapper;

    @PostMapping
    public ResponseEntity<PersonaResponseDTO> registrar(@Valid @RequestBody PersonaRequestDTO dto) {
        var domainModel = dto.toDomain();
        var result = registrarPersonaUseCase.execute(domainModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(personaMapper.toResponseDTO(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaResponseDTO> consultar(@PathVariable UUID id) {
        var result = consultarPersonaUseCase.execute(id);
        return ResponseEntity.ok(personaMapper.toResponseDTO(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaResponseDTO> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody PersonaUpdateRequestDTO dto) {
        var result = actualizarPersonaUseCase.execute(id, dto.toDomain());
        return ResponseEntity.ok(personaMapper.toResponseDTO(result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PersonaResponseDTO> actualizarParcial(
            @PathVariable UUID id,
            @Valid @RequestBody PersonaPatchRequestDTO dto) {
        var command = new PatchPersonaCommand(
                dto.nombre(),
                dto.genero(),
                dto.edad(),
                dto.identificacion(),
                dto.direccion(),
                dto.telefono()
        );
        var result = actualizarParcialPersonaUseCase.execute(id, command);
        return ResponseEntity.ok(personaMapper.toResponseDTO(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        eliminarPersonaUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
