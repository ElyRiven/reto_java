package com.banco.bank.infrastructure.entity;

import com.banco.bank.domain.model.TipoCuentaEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cuentas")
@Getter
@Setter
@NoArgsConstructor
public class CuentaEntity {

    @Id
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID cuentaId;

    @Column(name = "cliente_id", nullable = false, updatable = false)
    private UUID clienteId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", insertable = false, updatable = false)
    private ClienteEntity cliente;

    @Column(name = "numero_cuenta", nullable = false, unique = true, updatable = false)
    private String numeroCuenta;

    @Column(name = "tipo_cuenta", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoCuentaEnum tipoCuenta;

    @Column(name = "saldo_inicial", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
