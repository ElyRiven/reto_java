package com.banco.users.integration;

import com.banco.users.domain.port.out.ClienteEventProducerPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PersonaControllerIntegrationTest extends AbstractUsersIntegrationTest {

    @MockitoBean
    private ClienteEventProducerPort clienteEventProducerPort;

    private static final String PERSONA_BODY = """
            {
                "nombre": "Ana Gómez",
                "genero": "Femenino",
                "edad": 30,
                "identificacion": "1234567890",
                "direccion": "Calle 10 # 20-30",
                "telefono": "3001234567"
            }
            """;

    @Test
    void debe_RegistrarPersona_Cuando_DatosSonValidos() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PERSONA_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Ana Gómez"))
                .andExpect(jsonPath("$.identificacion").value("1234567890"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void debe_Retornar409_Cuando_IdentificacionDuplicada() throws Exception {
        // Given - registrar persona primera vez
        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PERSONA_BODY))
                .andExpect(status().isCreated());

        // When - intentar registrar la misma identificación / Then
        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PERSONA_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void debe_Retornar400_Cuando_NombreEsNulo() throws Exception {
        // Given
        var bodyInvalido = """
                {
                    "nombre": "",
                    "genero": "Masculino",
                    "edad": 25,
                    "identificacion": "9876543210",
                    "direccion": "Av. Principal 100",
                    "telefono": "3009876543"
                }
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe_ConsultarPersona_Cuando_ExisteEnBD() throws Exception {
        // Given - registrar persona
        var result = mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PERSONA_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        var personaId = responseBody.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        // When / Then
        mockMvc.perform(get("/api/v1/personas/" + personaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana Gómez"))
                .andExpect(jsonPath("$.identificacion").value("1234567890"));
    }

    @Test
    void debe_Retornar404_Cuando_PersonaNoExiste() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/personas/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void debe_ActualizarPersona_Cuando_DatosSonValidos() throws Exception {
        // Given - registrar persona
        var result = mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PERSONA_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        var personaId = responseBody.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        var updateBody = """
                {
                    "nombre": "Ana María Gómez",
                    "genero": "Femenino",
                    "edad": 31,
                    "identificacion": "1234567890",
                    "direccion": "Nueva Dirección 456",
                    "telefono": "3001234567"
                }
                """;

        // When / Then
        mockMvc.perform(put("/api/v1/personas/" + personaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana María Gómez"))
                .andExpect(jsonPath("$.edad").value(31))
                .andExpect(jsonPath("$.direccion").value("Nueva Dirección 456"));
    }

    @Test
    void debe_ActualizarParcialPersona_Cuando_SoloCampoNombre() throws Exception {
        // Given - registrar persona
        var result = mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PERSONA_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        var personaId = responseBody.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        var patchBody = """
                {
                    "nombre": "Ana Patricia Gómez"
                }
                """;

        // When / Then
        mockMvc.perform(patch("/api/v1/personas/" + personaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana Patricia Gómez"));
    }

    @Test
    void debe_EliminarPersona_Cuando_Existe() throws Exception {
        // Given - registrar persona
        var result = mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PERSONA_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        var personaId = responseBody.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        // When / Then
        mockMvc.perform(delete("/api/v1/personas/" + personaId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/personas/" + personaId))
                .andExpect(status().isNotFound());
    }
}
