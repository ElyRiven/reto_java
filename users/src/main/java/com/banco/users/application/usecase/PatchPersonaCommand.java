package com.banco.users.application.usecase;

public record PatchPersonaCommand(
        String nombre,
        String genero,
        Integer edad,
        String identificacion,
        String direccion,
        String telefono
) {}
