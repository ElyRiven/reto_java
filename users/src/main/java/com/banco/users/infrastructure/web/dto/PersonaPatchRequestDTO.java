package com.banco.users.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record PersonaPatchRequestDTO(
        String nombre,
        String genero,
        @Min(value = 0, message = "La edad debe ser mayor o igual a 0") Integer edad,
        @Size(max = 10, message = "La identificación no puede tener más de 10 caracteres")
        String identificacion,
        String direccion,
        String telefono
) {}
