package com.banco.bank.infrastructure.persistence;

import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.infrastructure.entity.CuentaEntity;
import com.banco.bank.infrastructure.mapper.CuentaMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaRepositoryAdapterTest {

    @Mock
    private CuentaJpaRepository cuentaJpaRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private CuentaRepositoryAdapter adapter;

    @Test
    void debe_RetornarCuenta_Cuando_FindByCuentaIdEncuentraRegistro() {
        var id = UUID.randomUUID();
        var entity = new CuentaEntity();
        entity.setCuentaId(id);
        var domain = new Cuenta();
        domain.setCuentaId(id);

        when(cuentaJpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(cuentaMapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.findByCuentaId(id);

        assertEquals(true, result.isPresent());
        assertEquals(id, result.orElseThrow().getCuentaId());
    }

    @Test
    void debe_ListarPorClientePaginado_Cuando_FindByClienteId() {
        var clienteId = UUID.randomUUID();
        var pageable = PageRequest.of(0, 10);
        var entity = new CuentaEntity();
        entity.setCuentaId(UUID.randomUUID());
        var domain = new Cuenta();
        domain.setCuentaId(entity.getCuentaId());

        var pageEntity = new PageImpl<>(List.of(entity), pageable, 1);
        when(cuentaJpaRepository.findByClienteIdAndDeletedAtIsNull(clienteId, pageable)).thenReturn(pageEntity);
        when(cuentaMapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.findByClienteId(clienteId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(entity.getCuentaId(), result.getContent().get(0).getCuentaId());
    }

    @Test
    void debe_RetornarCuenta_Cuando_FindByNumeroCuentaEncuentraRegistro() {
        var entity = new CuentaEntity();
        entity.setNumeroCuenta("123456");
        var domain = new Cuenta();
        domain.setNumeroCuenta("123456");

        when(cuentaJpaRepository.findByNumeroCuenta("123456")).thenReturn(Optional.of(entity));
        when(cuentaMapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.findByNumeroCuenta("123456");

        assertEquals(true, result.isPresent());
        assertEquals("123456", result.orElseThrow().getNumeroCuenta());
    }

    @Test
    void debe_GuardarCuenta_Cuando_SaveEsInvocado() {
        var cuenta = new Cuenta();
        cuenta.setCuentaId(UUID.randomUUID());
        var entity = new CuentaEntity();
        entity.setCuentaId(cuenta.getCuentaId());
        var savedEntity = new CuentaEntity();
        savedEntity.setCuentaId(cuenta.getCuentaId());
        var savedDomain = new Cuenta();
        savedDomain.setCuentaId(cuenta.getCuentaId());

        when(cuentaMapper.toEntity(cuenta)).thenReturn(entity);
        when(cuentaJpaRepository.save(entity)).thenReturn(savedEntity);
        when(cuentaMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        var result = adapter.save(cuenta);

        assertNotNull(result);
        assertEquals(cuenta.getCuentaId(), result.getCuentaId());
    }

    @Test
    void debe_RealizarSoftDelete_Cuando_DeleteEncuentraCuenta() {
        var cuentaId = UUID.randomUUID();
        var entity = new CuentaEntity();
        entity.setCuentaId(cuentaId);
        entity.setEstado(true);

        when(cuentaJpaRepository.findById(cuentaId)).thenReturn(Optional.of(entity));

        adapter.delete(cuentaId);

        assertEquals(false, entity.getEstado());
        assertNotNull(entity.getDeletedAt());
        verify(cuentaJpaRepository).save(entity);
    }

    @Test
    void debe_DelegarExistsByClienteId_Cuando_SeConsultaExistenciaCliente() {
        var clienteId = UUID.randomUUID();
        when(cuentaJpaRepository.existsByClienteId(clienteId)).thenReturn(true);

        var result = adapter.existsByClienteId(clienteId);

        assertEquals(true, result);
    }

    @Test
    void debe_DelegarExistsByNumeroCuentaAndDeletedAtIsNull_Cuando_SeConsultaDuplicado() {
        when(cuentaJpaRepository.existsByNumeroCuentaAndDeletedAtIsNull("123")).thenReturn(true);

        var result = adapter.existsByNumeroCuentaAndDeletedAtIsNull("123");

        assertEquals(true, result);
        verify(cuentaJpaRepository).existsByNumeroCuentaAndDeletedAtIsNull("123");
    }
}
