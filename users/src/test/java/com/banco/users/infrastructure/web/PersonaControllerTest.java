package com.banco.users.infrastructure.web;

import com.banco.users.application.usecase.ActualizarParcialPersonaUseCase;
import com.banco.users.application.usecase.ActualizarPersonaUseCase;
import com.banco.users.application.usecase.ConsultarPersonaUseCase;
import com.banco.users.application.usecase.EliminarPersonaUseCase;
import com.banco.users.application.usecase.PatchPersonaCommand;
import com.banco.users.application.usecase.RegistrarPersonaUseCase;
import com.banco.users.domain.exception.PersonaNotFoundException;
import com.banco.users.domain.exception.PersonaYaExisteException;
import com.banco.users.domain.model.Persona;
import com.banco.users.infrastructure.mapper.PersonaMapper;
import com.banco.users.infrastructure.web.dto.PersonaResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PersonaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RegistrarPersonaUseCase registrarPersonaUseCase;

    @Mock
    private ConsultarPersonaUseCase consultarPersonaUseCase;

    @Mock
    private ActualizarPersonaUseCase actualizarPersonaUseCase;

    @Mock
    private ActualizarParcialPersonaUseCase actualizarParcialPersonaUseCase;

    @Mock
    private EliminarPersonaUseCase eliminarPersonaUseCase;

    @Mock
    private PersonaMapper personaMapper;

    @InjectMocks
    private PersonaController personaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(personaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void debe_Retornar201_Cuando_PostRegistrarPersonaEsValido() throws Exception {
        var id = UUID.randomUUID();
        var response = new PersonaResponseDTO(id, "Ana", "F", 30, "123", "Dir", "300", Instant.now(), null, null);

        when(registrarPersonaUseCase.execute(any(Persona.class))).thenReturn(new Persona());
        when(personaMapper.toResponseDTO(any(Persona.class))).thenReturn(response);

        var request = """
                {
                  "nombre": "Ana",
                  "genero": "F",
                  "edad": 30,
                  "identificacion": "123",
                  "direccion": "Dir",
                  "telefono": "300"
                }
                """;

        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void debe_Retornar400_Cuando_PostRegistrarPersonaTieneCamposInvalidos() throws Exception {
        var request = """
                {
                  "nombre": "",
                  "genero": "F",
                  "edad": -1,
                  "identificacion": "",
                  "direccion": "Dir",
                  "telefono": "300"
                }
                """;

        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.edad").exists());
    }

    @Test
    void debe_Retornar409_Cuando_PostRegistrarPersonaTieneIdentificacionDuplicada() throws Exception {
        when(registrarPersonaUseCase.execute(any(Persona.class)))
                .thenThrow(new PersonaYaExisteException("El número de identificación ya existe: 123"));

        var request = """
                {
                  "nombre": "Ana",
                  "genero": "F",
                  "edad": 30,
                  "identificacion": "123",
                  "direccion": "Dir",
                  "telefono": "300"
                }
                """;

        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El número de identificación ya existe: 123"));
    }

    @Test
    void debe_Retornar200_Cuando_GetConsultarPersonaEsValido() throws Exception {
        var id = UUID.randomUUID();
        var response = new PersonaResponseDTO(id, "Ana", "F", 30, "123", "Dir", "300", Instant.now(), null, null);

        when(consultarPersonaUseCase.execute(id)).thenReturn(new Persona());
        when(personaMapper.toResponseDTO(any(Persona.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/personas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void debe_Retornar404_Cuando_GetConsultarPersonaNoExiste() throws Exception {
        var id = UUID.randomUUID();
        when(consultarPersonaUseCase.execute(id))
                .thenThrow(new PersonaNotFoundException("No existe una persona con el id indicado"));

        mockMvc.perform(get("/api/v1/personas/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe una persona con el id indicado"));
    }

    @Test
    void debe_Retornar200_Cuando_PutActualizarPersonaEsValido() throws Exception {
        var id = UUID.randomUUID();
        var response = new PersonaResponseDTO(id, "Ana", "F", 31, "123", "Dir", "300", Instant.now(), Instant.now(), null);

        when(actualizarPersonaUseCase.execute(eq(id), any(Persona.class))).thenReturn(new Persona());
        when(personaMapper.toResponseDTO(any(Persona.class))).thenReturn(response);

        var request = """
                {
                  "nombre": "Ana",
                  "genero": "F",
                  "edad": 31,
                  "identificacion": "123",
                  "direccion": "Dir",
                  "telefono": "300"
                }
                """;

        mockMvc.perform(put("/api/v1/personas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.edad").value(31));
    }

    @Test
    void debe_Retornar400_Cuando_PutActualizarPersonaTieneNombreVacio() throws Exception {
        var id = UUID.randomUUID();
        var request = """
                {
                  "nombre": "",
                  "genero": "F",
                  "edad": 31,
                  "identificacion": "123",
                  "direccion": "Dir",
                  "telefono": "300"
                }
                """;

        mockMvc.perform(put("/api/v1/personas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists());
    }

    @Test
    void debe_Retornar200_Cuando_PatchActualizarParcialPersonaEsValido() throws Exception {
        var id = UUID.randomUUID();
        var response = new PersonaResponseDTO(id, "Ana", "F", 30, "123", "Dir", "300", Instant.now(), Instant.now(), null);

        when(actualizarParcialPersonaUseCase.execute(eq(id), any(PatchPersonaCommand.class))).thenReturn(new Persona());
        when(personaMapper.toResponseDTO(any(Persona.class))).thenReturn(response);

        var request = """
                {
                  "direccion": "Nueva direccion"
                }
                """;

        mockMvc.perform(patch("/api/v1/personas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direccion").value("Dir"));
    }

    @Test
    void debe_Retornar204_Cuando_DeleteEliminarPersonaEsValido() throws Exception {
        var id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/personas/{id}", id))
                .andExpect(status().isNoContent());

        verify(eliminarPersonaUseCase).execute(id);
    }

    @Test
    void debe_Retornar404_Cuando_DeleteEliminarPersonaNoExiste() throws Exception {
        var id = UUID.randomUUID();
        doThrow(new PersonaNotFoundException("No existe una persona con el id indicado"))
                .when(eliminarPersonaUseCase).execute(id);

        mockMvc.perform(delete("/api/v1/personas/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe una persona con el id indicado"));
    }

    @Test
    void debe_PermitirAccesoSinToken_Cuando_NoHaySeguridadWebConfigurada() throws Exception {
        var id = UUID.randomUUID();
        var response = new PersonaResponseDTO(id, "Ana", "F", 30, "123", "Dir", "300", Instant.now(), null, null);

        when(consultarPersonaUseCase.execute(id)).thenReturn(new Persona());
        when(personaMapper.toResponseDTO(any(Persona.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/personas/{id}", id))
                .andExpect(status().isOk());
    }
}
