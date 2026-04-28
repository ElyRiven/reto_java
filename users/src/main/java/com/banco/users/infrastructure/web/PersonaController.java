package com.banco.users.infrastructure.web;

import com.banco.users.application.usecase.ConsultarPersonaUseCase;
import com.banco.users.application.usecase.RegistrarPersonaUseCase;
import com.banco.users.infrastructure.mapper.PersonaMapper;
import com.banco.users.infrastructure.web.dto.PersonaRequestDTO;
import com.banco.users.infrastructure.web.dto.PersonaResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final RegistrarPersonaUseCase registrarPersonaUseCase;
    private final ConsultarPersonaUseCase consultarPersonaUseCase;
    private final PersonaMapper personaMapper;

    @PostMapping
    public ResponseEntity<PersonaResponseDTO> registrar(@Valid @RequestBody PersonaRequestDTO dto) {
        var domainModel = dto.toDomain();
        var result = registrarPersonaUseCase.execute(domainModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(personaMapper.toResponseDTO(result));
    }

    @GetMapping("/{identificacion}")
    public ResponseEntity<PersonaResponseDTO> consultar(@PathVariable String identificacion) {
        var result = consultarPersonaUseCase.execute(identificacion);
        return ResponseEntity.ok(personaMapper.toResponseDTO(result));
    }
}
