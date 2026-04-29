package com.banco.bank.infrastructure.mapper;

import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.model.TipoCuentaEnum;
import com.banco.bank.infrastructure.entity.CuentaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CuentaMapper {

    private final ClienteMapper clienteMapper;

    public CuentaEntity toEntity(Cuenta cuenta) {
        var entity = new CuentaEntity();
        entity.setCuentaId(cuenta.getCuentaId());
        entity.setClienteId(cuenta.getCliente().getClienteId());
        entity.setNumeroCuenta(cuenta.getNumeroCuenta());
        entity.setTipoCuenta(TipoCuentaEnum.fromDisplayName(cuenta.getTipoCuenta()));
        entity.setSaldoInicial(cuenta.getSaldoInicial());
        entity.setEstado(cuenta.getEstado());
        entity.setCreatedAt(cuenta.getCreatedAt());
        entity.setUpdatedAt(cuenta.getUpdatedAt());
        entity.setDeletedAt(cuenta.getDeletedAt());
        return entity;
    }

    public Cuenta toDomain(CuentaEntity entity) {
        var cuenta = new Cuenta();
        cuenta.setCuentaId(entity.getCuentaId());
        if (entity.getCliente() != null) {
            cuenta.setCliente(clienteMapper.toDomain(entity.getCliente()));
        }
        cuenta.setNumeroCuenta(entity.getNumeroCuenta());
        cuenta.setTipoCuenta(entity.getTipoCuenta().getDisplayName());
        cuenta.setSaldoInicial(entity.getSaldoInicial());
        cuenta.setEstado(entity.getEstado());
        cuenta.setCreatedAt(entity.getCreatedAt());
        cuenta.setUpdatedAt(entity.getUpdatedAt());
        cuenta.setDeletedAt(entity.getDeletedAt());
        return cuenta;
    }
}
