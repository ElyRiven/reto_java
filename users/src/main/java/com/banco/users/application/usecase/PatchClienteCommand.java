package com.banco.users.application.usecase;

public record PatchClienteCommand(
        String nombre,
        String genero,
        Integer edad,
        String identificacion,
        String direccion,
        String telefono,
        String contrasena,
        Boolean estado
) {}
