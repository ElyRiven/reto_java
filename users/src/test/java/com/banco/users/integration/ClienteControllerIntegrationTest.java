package com.banco.users.integration;

import com.banco.users.domain.port.out.ClienteEventProducerPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClienteControllerIntegrationTest extends AbstractUsersIntegrationTest {

    @MockitoBean
    private ClienteEventProducerPort clienteEventProducerPort;

    private String personaId;

    @BeforeEach
    void setUp() throws Exception {
        doNothing().when(clienteEventProducerPort).publish(any());

        // Registrar persona base para los tests de cliente
        var personaBody = """
                {
                    "nombre": "Carlos Ruiz",
                    "genero": "Masculino",
                    "edad": 35,
                    "identificacion": "0987654321",
                    "direccion": "Av. Principal 200",
                    "telefono": "3109876543"
                }
                """;

        var result = mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personaBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        personaId = responseBody.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void debe_CrearCliente_Cuando_PersonaExisteYDatosSonValidos() throws Exception {
        // Given
        var clienteBody = """
                {
                    "personaId": "%s",
                    "contrasena": "Segura123!",
                    "estado": true
                }
                """.formatted(personaId);

        // When / Then
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").isNotEmpty())
                .andExpect(jsonPath("$.nombre").value("Carlos Ruiz"))
                .andExpect(jsonPath("$.estado").value(true));
    }

    @Test
    void debe_Retornar404_Cuando_PersonaNoExisteAlCrearCliente() throws Exception {
        // Given - personaId inexistente
        var clienteBody = """
                {
                    "personaId": "%s",
                    "contrasena": "Segura123!",
                    "estado": true
                }
                """.formatted(UUID.randomUUID());

        // When / Then
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void debe_Retornar409_Cuando_PersonaYaEsCliente() throws Exception {
        // Given - crear cliente primera vez
        var clienteBody = """
                {
                    "personaId": "%s",
                    "contrasena": "Segura123!",
                    "estado": true
                }
                """.formatted(personaId);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteBody))
                .andExpect(status().isCreated());

        // When - intentar crear el mismo cliente / Then
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteBody))
                .andExpect(status().isConflict());
    }

    @Test
    void debe_Retornar400_Cuando_ContrasenaEsDemasiadoCorta() throws Exception {
        // Given
        var clienteBody = """
                {
                    "personaId": "%s",
                    "contrasena": "corta",
                    "estado": true
                }
                """.formatted(personaId);

        // When / Then
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe_ConsultarCliente_Cuando_ExisteEnBD() throws Exception {
        // Given - crear cliente
        var clienteBody = """
                {
                    "personaId": "%s",
                    "contrasena": "Segura123!",
                    "estado": true
                }
                """.formatted(personaId);

        var createResult = mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var clienteId = responseBody.replaceAll(".*\"clienteId\":\"([^\"]+)\".*", "$1");

        // When / Then
        mockMvc.perform(get("/api/v1/clientes/" + clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos Ruiz"))
                .andExpect(jsonPath("$.estado").value(true));
    }

    @Test
    void debe_Retornar404_Cuando_ClienteNoExiste() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/clientes/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void debe_ActualizarCliente_Cuando_DatosSonValidos() throws Exception {
        // Given - crear cliente
        var clienteBody = """
                {
                    "personaId": "%s",
                    "contrasena": "Segura123!",
                    "estado": true
                }
                """.formatted(personaId);

        var createResult = mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var clienteId = responseBody.replaceAll(".*\"clienteId\":\"([^\"]+)\".*", "$1");

        var updateBody = """
                {
                    "nombre": "Carlos Alberto Ruiz",
                    "genero": "Masculino",
                    "edad": 36,
                    "identificacion": "0987654321",
                    "direccion": "Nueva Dirección 300",
                    "telefono": "3109876543",
                    "contrasena": "NuevaSegura456!",
                    "estado": false
                }
                """;

        // When / Then
        mockMvc.perform(put("/api/v1/clientes/" + clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos Alberto Ruiz"))
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    void debe_ActualizarParcialCliente_Cuando_SoloCampoEstado() throws Exception {
        // Given - crear cliente
        var clienteBody = """
                {
                    "personaId": "%s",
                    "contrasena": "Segura123!",
                    "estado": true
                }
                """.formatted(personaId);

        var createResult = mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var clienteId = responseBody.replaceAll(".*\"clienteId\":\"([^\"]+)\".*", "$1");

        var patchBody = """
                {
                    "estado": false
                }
                """;

        // When / Then
        mockMvc.perform(patch("/api/v1/clientes/" + clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    void debe_EliminarCliente_Cuando_Existe() throws Exception {
        // Given - crear cliente
        var clienteBody = """
                {
                    "personaId": "%s",
                    "contrasena": "Segura123!",
                    "estado": true
                }
                """.formatted(personaId);

        var createResult = mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var clienteId = responseBody.replaceAll(".*\"clienteId\":\"([^\"]+)\".*", "$1");

        // When / Then
        mockMvc.perform(delete("/api/v1/clientes/" + clienteId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/clientes/" + clienteId))
                .andExpect(status().isNotFound());
    }
}
