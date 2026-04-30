package com.banco.users.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ClientePatchRequestDTO(
        String nombre,
        String genero,

        @Min(value = 0, message = "La edad debe ser mayor o igual a 0")
        Integer edad,

        String identificacion,
        String direccion,
        String telefono,

        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String contrasena,

        Boolean estado
) {}
