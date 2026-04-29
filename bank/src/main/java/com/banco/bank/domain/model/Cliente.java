package com.banco.bank.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    private UUID clienteId;
    private String nombre;
    private Boolean estado;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
