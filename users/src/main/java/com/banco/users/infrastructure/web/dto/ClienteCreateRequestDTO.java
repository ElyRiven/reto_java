package com.banco.users.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ClienteCreateRequestDTO(
        @NotNull(message = "El personaId es obligatorio")
        UUID personaId,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String contrasena,

        @NotNull(message = "El estado es obligatorio")
        Boolean estado
) {}
