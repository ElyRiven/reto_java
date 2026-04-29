package com.banco.bank.infrastructure.persistence;

import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.model.TipoMovimientoEnum;
import com.banco.bank.domain.port.out.MovimientoRepositoryPort;
import com.banco.bank.infrastructure.entity.MovimientoEntity;
import com.banco.bank.infrastructure.mapper.MovimientoMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MovimientoRepositoryAdapter implements MovimientoRepositoryPort {

    private final MovimientoJpaRepository movimientoJpaRepository;
    private final MovimientoMapper movimientoMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Movimiento save(Movimiento movimiento) {
        var entity = movimientoMapper.toEntity(movimiento);
        var saved = movimientoJpaRepository.save(entity);
        entityManager.flush();
        entityManager.refresh(saved);
        return movimientoMapper.toDomain(saved);
    }

    @Override
    public Optional<Movimiento> findById(UUID movimientoId) {
        return movimientoJpaRepository.findByIdFetched(movimientoId)
                .map(movimientoMapper::toDomain);
    }

    @Override
    public Page<Movimiento> findByCuentaId(UUID cuentaId, Pageable pageable, Instant startDate, Instant endDate, String tipoMovimiento) {
        Specification<MovimientoEntity> spec = Specification
                .<MovimientoEntity>where((root, q, cb) -> cb.equal(root.get("cuentaId"), cuentaId))
                .and((root, q, cb) -> cb.isNull(root.get("deletedAt")));

        if (startDate != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("fecha"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("fecha"), endDate));
        }
        if (tipoMovimiento != null) {
            var tipoEnum = TipoMovimientoEnum.fromDisplayName(tipoMovimiento);
            spec = spec.and((root, q, cb) -> cb.equal(root.get("tipoMovimiento"), tipoEnum));
        }

        return movimientoJpaRepository.findAll(spec, pageable)
                .map(movimientoMapper::toDomain);
    }

    @Override
    public boolean existsByCuentaId(UUID cuentaId) {
        return movimientoJpaRepository.existsByCuentaIdAndDeletedAtIsNull(cuentaId);
    }

    @Override
    public void softDelete(UUID movimientoId) {
        movimientoJpaRepository.findById(movimientoId).ifPresent(entity -> {
            entity.setDeletedAt(Instant.now());
            movimientoJpaRepository.save(entity);
        });
    }
}
