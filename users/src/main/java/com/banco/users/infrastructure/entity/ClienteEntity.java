package com.banco.users.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "clientes",
        indexes = {
                @Index(name = "idx_clientes_deleted_at", columnList = "deleted_at"),
                @Index(name = "idx_clientes_estado", columnList = "estado")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ClienteEntity {

    @Id
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID clienteId;

    @OneToOne
    @JoinColumn(name = "persona_id", unique = true, nullable = false)
    private PersonaEntity persona;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
