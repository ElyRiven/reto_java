package com.banco.users.infrastructure.mapper;

import com.banco.users.domain.model.Persona;
import com.banco.users.infrastructure.entity.PersonaEntity;
import com.banco.users.infrastructure.web.dto.PersonaResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {

    public PersonaEntity toEntity(Persona persona) {
        var entity = new PersonaEntity();
        entity.setId(persona.getId());
        entity.setNombre(persona.getNombre());
        entity.setGenero(persona.getGenero());
        entity.setEdad(persona.getEdad());
        entity.setIdentificacion(persona.getIdentificacion());
        entity.setDireccion(persona.getDireccion());
        entity.setTelefono(persona.getTelefono());
        entity.setCreatedAt(persona.getCreatedAt());
        entity.setUpdatedAt(persona.getUpdatedAt());
        return entity;
    }

    public Persona toDomain(PersonaEntity entity) {
        var persona = new Persona();
        persona.setId(entity.getId());
        persona.setNombre(entity.getNombre());
        persona.setGenero(entity.getGenero());
        persona.setEdad(entity.getEdad());
        persona.setIdentificacion(entity.getIdentificacion());
        persona.setDireccion(entity.getDireccion());
        persona.setTelefono(entity.getTelefono());
        persona.setCreatedAt(entity.getCreatedAt());
        persona.setUpdatedAt(entity.getUpdatedAt());
        return persona;
    }

    public PersonaResponseDTO toResponseDTO(Persona persona) {
        return new PersonaResponseDTO(
                persona.getId(),
                persona.getNombre(),
                persona.getGenero(),
                persona.getEdad(),
                persona.getIdentificacion(),
                persona.getDireccion(),
                persona.getTelefono(),
                persona.getCreatedAt(),
                persona.getUpdatedAt()
        );
    }
}
