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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MovimientoControllerIntegrationTest extends AbstractBankIntegrationTest {

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Autowired
    private CuentaJpaRepository cuentaJpaRepository;

    private UUID cuentaId;

    @BeforeEach
    void setUp() {
        var clienteId = UUID.randomUUID();
        var clienteEntity = new ClienteEntity();
        clienteEntity.setClienteId(clienteId);
        clienteEntity.setNombre("Cliente Movimientos Test");
        clienteEntity.setEstado(true);
        clienteEntity.setCreatedAt(Instant.now());
        clienteJpaRepository.save(clienteEntity);

        cuentaId = UUID.randomUUID();
        var cuentaEntity = new CuentaEntity();
        cuentaEntity.setCuentaId(cuentaId);
        cuentaEntity.setClienteId(clienteId);
        cuentaEntity.setNumeroCuenta("4781200001");
        cuentaEntity.setTipoCuenta(TipoCuentaEnum.AHORROS);
        cuentaEntity.setSaldoInicial(new BigDecimal("1000.00"));
        cuentaEntity.setEstado(true);
        cuentaEntity.setCreatedAt(Instant.now());
        cuentaJpaRepository.save(cuentaEntity);
    }

    @Test
    void debe_CrearMovimientoDeposito_Cuando_DatosSonValidos() throws Exception {
        // Given
        var body = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Depósito",
                    "valor": 200.00
                }
                """.formatted(cuentaId);

        // When / Then
        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMovimiento").value("Depósito"))
                .andExpect(jsonPath("$.valor").value(200.00))
                .andExpect(jsonPath("$.saldo").value(1200.00))
                .andExpect(jsonPath("$.movimientoId").isNotEmpty());
    }

    @Test
    void debe_CrearMovimientoRetiro_Cuando_SaldoEsSuficiente() throws Exception {
        // Given
        var body = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Retiro",
                    "valor": -300.00
                }
                """.formatted(cuentaId);

        // When / Then
        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMovimiento").value("Retiro"))
                .andExpect(jsonPath("$.saldo").value(700.00));
    }

    @Test
    void debe_Retornar400_Cuando_SaldoInsuficiente() throws Exception {
        // Given - intentar retirar más del saldo disponible
        var body = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Retiro",
                    "valor": -2000.00
                }
                """.formatted(cuentaId);

        // When / Then
        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe_Retornar404_Cuando_CuentaNoExistePararMovimiento() throws Exception {
        // Given
        var body = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Depósito",
                    "valor": 100.00
                }
                """.formatted(UUID.randomUUID());

        // When / Then
        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void debe_ObtenerMovimiento_Cuando_ExisteEnBD() throws Exception {
        // Given - crear movimiento primero
        var createBody = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Depósito",
                    "valor": 150.00
                }
                """.formatted(cuentaId);

        var createResult = mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var movimientoId = responseBody.replaceAll(".*\"movimientoId\":\"([^\"]+)\".*", "$1");

        // When / Then
        mockMvc.perform(get("/api/v1/movimientos/" + movimientoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimiento").value("Depósito"))
                .andExpect(jsonPath("$.valor").value(150.00));
    }

    @Test
    void debe_Retornar404_Cuando_MovimientoNoExiste() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/movimientos/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void debe_ListarMovimientosPorCuenta_Cuando_ExistenMovimientos() throws Exception {
        // Given - crear dos movimientos
        var body1 = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Depósito",
                    "valor": 100.00
                }
                """.formatted(cuentaId);

        var body2 = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Retiro",
                    "valor": -50.00
                }
                """.formatted(cuentaId);

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                .andExpect(status().isCreated());

        // When / Then
        mockMvc.perform(get("/api/v1/movimientos/cuenta/" + cuentaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void debe_ActualizarMovimiento_Cuando_DatosSonValidos() throws Exception {
        // Given - crear movimiento
        var createBody = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Depósito",
                    "valor": 200.00
                }
                """.formatted(cuentaId);

        var createResult = mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var movimientoId = responseBody.replaceAll(".*\"movimientoId\":\"([^\"]+)\".*", "$1");

        var updateBody = """
                {
                    "tipoMovimiento": "Depósito",
                    "valor": 250.00
                }
                """;

        // When / Then
        mockMvc.perform(put("/api/v1/movimientos/" + movimientoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(250.00));
    }

    @Test
    void debe_ActualizarParcialMovimiento_Cuando_SoloCampoValor() throws Exception {
        // Given - crear movimiento
        var createBody = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Depósito",
                    "valor": 100.00
                }
                """.formatted(cuentaId);

        var createResult = mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var movimientoId = responseBody.replaceAll(".*\"movimientoId\":\"([^\"]+)\".*", "$1");

        var patchBody = """
                {
                    "valor": 175.00
                }
                """;

        // When / Then
        mockMvc.perform(patch("/api/v1/movimientos/" + movimientoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(175.00));
    }

    @Test
    void debe_EliminarMovimiento_Cuando_Existe() throws Exception {
        // Given - crear movimiento
        var createBody = """
                {
                    "cuentaId": "%s",
                    "tipoMovimiento": "Depósito",
                    "valor": 50.00
                }
                """.formatted(cuentaId);

        var createResult = mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var movimientoId = responseBody.replaceAll(".*\"movimientoId\":\"([^\"]+)\".*", "$1");

        // When / Then
        mockMvc.perform(delete("/api/v1/movimientos/" + movimientoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/movimientos/" + movimientoId))
                .andExpect(status().isNotFound());
    }
}
