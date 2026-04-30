package com.banco.bank.integration;

import com.banco.bank.infrastructure.entity.ClienteEntity;
import com.banco.bank.infrastructure.entity.CuentaEntity;
import com.banco.bank.infrastructure.persistence.ClienteJpaRepository;
import com.banco.bank.infrastructure.persistence.CuentaJpaRepository;
import com.banco.bank.domain.model.TipoCuentaEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReporteControllerIntegrationTest extends AbstractBankIntegrationTest {

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Autowired
    private CuentaJpaRepository cuentaJpaRepository;

    private UUID clienteId;
    private UUID cuentaId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        var clienteEntity = new ClienteEntity();
        clienteEntity.setClienteId(clienteId);
        clienteEntity.setNombre("Cliente Reporte Test");
        clienteEntity.setEstado(true);
        clienteEntity.setCreatedAt(Instant.now());
        clienteJpaRepository.save(clienteEntity);

        cuentaId = UUID.randomUUID();
        var cuentaEntity = new CuentaEntity();
        cuentaEntity.setCuentaId(cuentaId);
        cuentaEntity.setClienteId(clienteId);
        cuentaEntity.setNumeroCuenta("4781300001");
        cuentaEntity.setTipoCuenta(TipoCuentaEnum.AHORROS);
        cuentaEntity.setSaldoInicial(new BigDecimal("1000.00"));
        cuentaEntity.setEstado(true);
        cuentaEntity.setCreatedAt(Instant.now());
        cuentaJpaRepository.save(cuentaEntity);
    }

    @Test
    void debe_ObtenerReporte_Cuando_ClienteConMovimientos() throws Exception {
        // Given - crear un movimiento para el cliente
        var movimientoBody = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Depósito",
                    "valor": 500.00
                }
                """.formatted(cuentaId);

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movimientoBody))
                .andExpect(status().isCreated());

        var fechaInicio = LocalDate.now().minusDays(1).toString();
        var fechaFin = LocalDate.now().plusDays(1).toString();

        // When / Then
        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", clienteId.toString())
                        .param("fecha", fechaInicio + "," + fechaFin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]['Numero Cuenta']").value("4781300001"));
    }

    @Test
    void debe_ObtenerReporteVacio_Cuando_NoHayMovimientosEnRango() throws Exception {
        // Given - rango de fechas pasadas sin movimientos
        var fechaInicio = LocalDate.now().minusDays(30).toString();
        var fechaFin = LocalDate.now().minusDays(15).toString();

        // When / Then
        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", clienteId.toString())
                        .param("fecha", fechaInicio + "," + fechaFin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void debe_Retornar400_Cuando_ClienteIdEsNulo() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/reportes")
                        .param("fecha", "2026-01-01,2026-12-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe_Retornar400_Cuando_FechaEsNula() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", clienteId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe_Retornar400_Cuando_FormatoFechaInvalido() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", clienteId.toString())
                        .param("fecha", "fecha-invalida"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe_Retornar400_Cuando_FechaInicioEsMayorQueFechaFin() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", clienteId.toString())
                        .param("fecha", "2026-12-31,2026-01-01"))
                .andExpect(status().isBadRequest());
    }
}
