package com.banco.users.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Persona {

    private UUID clienteId;
    private String contrasena;
    private Boolean estado;
    private Instant deletedAt;
}
