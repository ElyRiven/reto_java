package com.banco.bank.infrastructure.mapper;

import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.model.TipoMovimientoEnum;
import com.banco.bank.infrastructure.entity.MovimientoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovimientoMapper {

    private final CuentaMapper cuentaMapper;

    public MovimientoEntity toEntity(Movimiento movimiento) {
        var entity = new MovimientoEntity();
        entity.setMovimientoId(movimiento.getMovimientoId());
        entity.setCuentaId(movimiento.getCuenta().getCuentaId());
        entity.setFecha(movimiento.getFecha());
        entity.setTipoMovimiento(TipoMovimientoEnum.fromDisplayName(movimiento.getTipoMovimiento()));
        entity.setValor(movimiento.getValor());
        entity.setSaldo(movimiento.getSaldo());
        entity.setCreatedAt(movimiento.getCreatedAt());
        entity.setUpdatedAt(movimiento.getUpdatedAt());
        entity.setDeletedAt(movimiento.getDeletedAt());
        return entity;
    }

    public Movimiento toDomain(MovimientoEntity entity) {
        var movimiento = new Movimiento();
        movimiento.setMovimientoId(entity.getMovimientoId());
        if (entity.getCuenta() != null) {
            movimiento.setCuenta(cuentaMapper.toDomain(entity.getCuenta()));
        } else {
            var cuentaRef = new com.banco.bank.domain.model.Cuenta();
            cuentaRef.setCuentaId(entity.getCuentaId());
            movimiento.setCuenta(cuentaRef);
        }
        movimiento.setFecha(entity.getFecha());
        movimiento.setTipoMovimiento(entity.getTipoMovimiento().getDisplayName());
        movimiento.setValor(entity.getValor());
        movimiento.setSaldo(entity.getSaldo());
        movimiento.setCreatedAt(entity.getCreatedAt());
        movimiento.setUpdatedAt(entity.getUpdatedAt());
        movimiento.setDeletedAt(entity.getDeletedAt());
        return movimiento;
    }
}
