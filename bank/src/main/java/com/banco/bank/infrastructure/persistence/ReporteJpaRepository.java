package com.banco.bank.infrastructure.persistence;

import com.banco.bank.infrastructure.entity.MovimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReporteJpaRepository extends JpaRepository<MovimientoEntity, UUID> {

    @Query("""
            SELECT m FROM MovimientoEntity m
            JOIN FETCH m.cuenta c
            JOIN FETCH c.cliente cl
            WHERE c.clienteId = :clienteId
            AND m.fecha >= :fechaInicio
            AND m.fecha <= :fechaFin
            AND m.deletedAt IS NULL
            AND c.deletedAt IS NULL
            AND cl.deletedAt IS NULL
            ORDER BY m.fecha DESC, m.movimientoId DESC
            """)
    List<MovimientoEntity> findMovimientosByClienteIdAndFecha(
            @Param("clienteId") UUID clienteId,
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin);
}
