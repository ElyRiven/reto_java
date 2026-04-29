package com.banco.bank.infrastructure.persistence;

import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.infrastructure.entity.MovimientoEntity;
import com.banco.bank.infrastructure.mapper.MovimientoMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimientoRepositoryAdapterTest {

    @Mock
    private MovimientoJpaRepository movimientoJpaRepository;

    @Mock
    private MovimientoMapper movimientoMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private MovimientoRepositoryAdapter adapter;

    @Test
    void debe_GuardarMovimiento_Cuando_SaveEsInvocado() {
        ReflectionTestUtils.setField(adapter, "entityManager", entityManager);

        var movimiento = new Movimiento();
        movimiento.setMovimientoId(UUID.randomUUID());
        var cuenta = new Cuenta();
        cuenta.setCuentaId(UUID.randomUUID());
        movimiento.setCuenta(cuenta);
        movimiento.setTipoMovimiento("Depósito");
        movimiento.setValor(new BigDecimal("100.00"));
        movimiento.setSaldo(new BigDecimal("100.00"));
        movimiento.setFecha(Instant.now());

        var entity = new MovimientoEntity();
        entity.setMovimientoId(movimiento.getMovimientoId());
        var savedEntity = new MovimientoEntity();
        savedEntity.setMovimientoId(movimiento.getMovimientoId());
        var savedDomain = new Movimiento();
        savedDomain.setMovimientoId(movimiento.getMovimientoId());

        when(movimientoMapper.toEntity(movimiento)).thenReturn(entity);
        when(movimientoJpaRepository.save(entity)).thenReturn(savedEntity);
        when(movimientoMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        var result = adapter.save(movimiento);

        assertNotNull(result);
        assertEquals(movimiento.getMovimientoId(), result.getMovimientoId());
        verify(entityManager).flush();
        verify(entityManager).refresh(savedEntity);
    }

    @Test
    void debe_RetornarMovimiento_Cuando_FindByIdEncuentraRegistro() {
        var movimientoId = UUID.randomUUID();
        var entity = new MovimientoEntity();
        entity.setMovimientoId(movimientoId);
        var domain = new Movimiento();
        domain.setMovimientoId(movimientoId);

        when(movimientoJpaRepository.findByIdFetched(movimientoId)).thenReturn(Optional.of(entity));
        when(movimientoMapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.findById(movimientoId);

        assertEquals(true, result.isPresent());
        assertEquals(movimientoId, result.orElseThrow().getMovimientoId());
    }

    @Test
    void debe_ListarMovimientosPorCuenta_Cuando_FindByCuentaIdConFiltros() {
        var cuentaId = UUID.randomUUID();
        var pageable = PageRequest.of(0, 10);
        var entity = new MovimientoEntity();
        entity.setMovimientoId(UUID.randomUUID());
        var domain = new Movimiento();
        domain.setMovimientoId(entity.getMovimientoId());

        var pageEntity = new PageImpl<>(List.of(entity), pageable, 1);
        when(movimientoJpaRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
                .thenReturn(pageEntity);
        when(movimientoMapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.findByCuentaId(cuentaId, pageable, null, null, null);

        assertEquals(1, result.getTotalElements());
        assertEquals(entity.getMovimientoId(), result.getContent().get(0).getMovimientoId());
    }

    @Test
    void debe_DelegarExistsByCuentaId_Cuando_SeConsultaExistencia() {
        var cuentaId = UUID.randomUUID();
        when(movimientoJpaRepository.existsByCuentaIdAndDeletedAtIsNull(cuentaId)).thenReturn(true);

        var result = adapter.existsByCuentaId(cuentaId);

        assertEquals(true, result);
    }

    @Test
    void debe_RealizarSoftDelete_Cuando_MovimientoExiste() {
        var movimientoId = UUID.randomUUID();
        var entity = new MovimientoEntity();
        entity.setMovimientoId(movimientoId);

        when(movimientoJpaRepository.findById(movimientoId)).thenReturn(Optional.of(entity));

        adapter.softDelete(movimientoId);

        assertNotNull(entity.getDeletedAt());
        verify(movimientoJpaRepository).save(entity);
    }
}
