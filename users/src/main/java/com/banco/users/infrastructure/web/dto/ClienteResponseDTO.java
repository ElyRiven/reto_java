package com.banco.users.infrastructure.web.dto;

import java.time.Instant;
import java.util.UUID;

public record ClienteResponseDTO(
        UUID clienteId,
        String nombre,
        String genero,
        int edad,
        String identificacion,
        String direccion,
        String telefono,
        Boolean estado,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {}
