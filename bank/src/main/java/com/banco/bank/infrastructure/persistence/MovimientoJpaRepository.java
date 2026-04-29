package com.banco.bank.infrastructure.persistence;

import com.banco.bank.infrastructure.entity.MovimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MovimientoJpaRepository extends JpaRepository<MovimientoEntity, UUID>,
        JpaSpecificationExecutor<MovimientoEntity> {

    boolean existsByCuentaIdAndDeletedAtIsNull(UUID cuentaId);

    @Query("""
            SELECT m FROM MovimientoEntity m
            JOIN FETCH m.cuenta c
            JOIN FETCH c.cliente cl
            WHERE m.movimientoId = :movimientoId
            """)
    Optional<MovimientoEntity> findByIdFetched(@Param("movimientoId") UUID movimientoId);
}
