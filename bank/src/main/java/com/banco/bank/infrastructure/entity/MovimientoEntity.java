package com.banco.bank.infrastructure.entity;

import com.banco.bank.domain.model.TipoMovimientoEnum;
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
@Table(name = "movimientos")
@Getter
@Setter
@NoArgsConstructor
public class MovimientoEntity {

    @Id
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID movimientoId;

    @Column(name = "cuenta_id", nullable = false, updatable = false)
    private UUID cuentaId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cuenta_id", insertable = false, updatable = false)
    private CuentaEntity cuenta;

    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoMovimientoEnum tipoMovimiento;

    @Column(name = "valor", nullable = false, precision = 18, scale = 2)
    private BigDecimal valor;

    @Column(name = "saldo", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
