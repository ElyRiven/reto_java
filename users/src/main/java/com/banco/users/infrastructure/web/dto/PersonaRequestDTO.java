package com.banco.users.infrastructure.web.dto;

import com.banco.users.domain.model.Persona;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PersonaRequestDTO(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El género es obligatorio") String genero,
        @Min(value = 0, message = "La edad debe ser mayor o igual a 0") int edad,
        @Size(max = 10, message = "La identificación no puede tener más de 10 caracteres")
        @NotBlank(message = "La identificación es obligatoria")
        String identificacion,
        @NotBlank(message = "La dirección es obligatoria") String direccion,
        @NotBlank(message = "El teléfono es obligatorio") String telefono
) {
    public Persona toDomain() {
        var persona = new Persona();
        persona.setNombre(nombre);
        persona.setGenero(genero);
        persona.setEdad(edad);
        persona.setIdentificacion(identificacion);
        persona.setDireccion(direccion);
        persona.setTelefono(telefono);
        return persona;
    }
}
