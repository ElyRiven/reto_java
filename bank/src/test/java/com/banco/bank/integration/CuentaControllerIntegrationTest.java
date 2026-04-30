package com.banco.bank.integration;

import com.banco.bank.infrastructure.entity.ClienteEntity;
import com.banco.bank.infrastructure.persistence.ClienteJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

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
class CuentaControllerIntegrationTest extends AbstractBankIntegrationTest {

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    private UUID clienteId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        var clienteEntity = new ClienteEntity();
        clienteEntity.setClienteId(clienteId);
        clienteEntity.setNombre("Cliente Integración");
        clienteEntity.setEstado(true);
        clienteEntity.setCreatedAt(Instant.now());
        clienteJpaRepository.save(clienteEntity);
    }

    @Test
    void debe_CrearCuenta_Cuando_DatosSonValidos() throws Exception {
        // Given
        var body = """
                {
                    "clienteId": "%s",
                    "numeroCuenta": "4781100001",
                    "tipoCuenta": "Ahorros",
                    "saldoInicial": 500.00,
                    "estado": true
                }
                """.formatted(clienteId);

        // When / Then
        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value("4781100001"))
                .andExpect(jsonPath("$.tipoCuenta").value("Ahorros"))
                .andExpect(jsonPath("$.saldoInicial").value(500.00))
                .andExpect(jsonPath("$.estado").value(true))
                .andExpect(jsonPath("$.cuentaId").isNotEmpty());
    }

    @Test
    void debe_Retornar400_Cuando_ClienteNoExiste() throws Exception {
        // Given
        var body = """
                {
                    "clienteId": "%s",
                    "numeroCuenta": "4781100002",
                    "tipoCuenta": "Ahorros",
                    "saldoInicial": 100.00,
                    "estado": true
                }
                """.formatted(UUID.randomUUID());

        // When / Then
        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe_Retornar409_Cuando_NumeroCuentaDuplicado() throws Exception {
        // Given - crear primera cuenta
        var body = """
                {
                    "clienteId": "%s",
                    "numeroCuenta": "4781100003",
                    "tipoCuenta": "Ahorros",
                    "saldoInicial": 200.00,
                    "estado": true
                }
                """.formatted(clienteId);

        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // When - intentar crear la misma cuenta / Then
        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void debe_Retornar400_Cuando_TipoCuentaInvalido() throws Exception {
        // Given
        var body = """
                {
                    "clienteId": "%s",
                    "numeroCuenta": "4781100004",
                    "tipoCuenta": "TipoInvalido",
                    "saldoInicial": 100.00,
                    "estado": true
                }
                """.formatted(clienteId);

        // When / Then
        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe_ObtenerCuenta_Cuando_ExisteEnBD() throws Exception {
        // Given - crear cuenta
        var body = """
                {
                    "clienteId": "%s",
                    "numeroCuenta": "4781100005",
                    "tipoCuenta": "Corriente",
                    "saldoInicial": 1000.00,
                    "estado": true
                }
                """.formatted(clienteId);

        var result = mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        var cuentaId = responseBody.replaceAll(".*\"cuentaId\":\"([^\"]+)\".*", "$1");

        // When / Then
        mockMvc.perform(get("/api/v1/cuentas/" + cuentaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroCuenta").value("4781100005"))
                .andExpect(jsonPath("$.tipoCuenta").value("Corriente"));
    }

    @Test
    void debe_Retornar404_Cuando_CuentaNoExiste() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/cuentas/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void debe_ListarCuentasPorCliente_Cuando_ClienteTieneCuentas() throws Exception {
        // Given - crear cuenta para el cliente
        var body = """
                {
                    "clienteId": "%s",
                    "numeroCuenta": "4781100006",
                    "tipoCuenta": "Ahorros",
                    "saldoInicial": 300.00,
                    "estado": true
                }
                """.formatted(clienteId);

        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // When / Then
        mockMvc.perform(get("/api/v1/cuentas/cliente/" + clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].numeroCuenta").value("4781100006"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void debe_ActualizarCuenta_Cuando_DatosSonValidos() throws Exception {
        // Given - crear cuenta
        var createBody = """
                {
                    "clienteId": "%s",
                    "numeroCuenta": "4781100007",
                    "tipoCuenta": "Ahorros",
                    "saldoInicial": 500.00,
                    "estado": true
                }
                """.formatted(clienteId);

        var createResult = mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var cuentaId = responseBody.replaceAll(".*\"cuentaId\":\"([^\"]+)\".*", "$1");

        var updateBody = """
                {
                    "tipoCuenta": "Corriente",
                    "saldoInicial": 750.00,
                    "estado": false
                }
                """;

        // When / Then
        mockMvc.perform(put("/api/v1/cuentas/" + cuentaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoCuenta").value("Corriente"))
                .andExpect(jsonPath("$.saldoInicial").value(750.00))
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    void debe_ActualizarParcialCuenta_Cuando_SoloCampoEstado() throws Exception {
        // Given - crear cuenta
        var createBody = """
                {
                    "clienteId": "%s",
                    "numeroCuenta": "4781100008",
                    "tipoCuenta": "Ahorros",
                    "saldoInicial": 600.00,
                    "estado": true
                }
                """.formatted(clienteId);

        var createResult = mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var cuentaId = responseBody.replaceAll(".*\"cuentaId\":\"([^\"]+)\".*", "$1");

        var patchBody = """
                {
                    "estado": false
                }
                """;

        // When / Then
        mockMvc.perform(patch("/api/v1/cuentas/" + cuentaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    void debe_EliminarCuenta_Cuando_NoTieneMovimientos() throws Exception {
        // Given - crear cuenta
        var createBody = """
                {
                    "clienteId": "%s",
                    "numeroCuenta": "4781100009",
                    "tipoCuenta": "Ahorros",
                    "saldoInicial": 100.00,
                    "estado": true
                }
                """.formatted(clienteId);

        var createResult = mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var cuentaId = responseBody.replaceAll(".*\"cuentaId\":\"([^\"]+)\".*", "$1");

        // When / Then
        mockMvc.perform(delete("/api/v1/cuentas/" + cuentaId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/cuentas/" + cuentaId))
                .andExpect(status().isNotFound());
    }
}
