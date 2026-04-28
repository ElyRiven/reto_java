package com.banco.users.infrastructure.web.dto;

import java.time.Instant;
import java.util.UUID;

public record PersonaResponseDTO(
        UUID id,
        String nombre,
        String genero,
        int edad,
        String identificacion,
        String direccion,
        String telefono,
        Instant createdAt,
        Instant updatedAt
) {}
