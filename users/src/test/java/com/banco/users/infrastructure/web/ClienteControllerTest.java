package com.banco.users.infrastructure.web;

import com.banco.users.application.usecase.ActualizarClienteUseCase;
import com.banco.users.application.usecase.ActualizarParcialClienteUseCase;
import com.banco.users.application.usecase.ConsultarClienteUseCase;
import com.banco.users.application.usecase.CrearClienteUseCase;
import com.banco.users.application.usecase.EliminarClienteUseCase;
import com.banco.users.application.usecase.PatchClienteCommand;
import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.exception.ClienteYaExisteException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.infrastructure.mapper.ClienteMapper;
import com.banco.users.infrastructure.web.dto.ClienteResponseDTO;
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
class ClienteControllerTest {

    private MockMvc mockMvc;

        @Mock
    private CrearClienteUseCase crearClienteUseCase;

        @Mock
    private ConsultarClienteUseCase consultarClienteUseCase;

        @Mock
    private ActualizarClienteUseCase actualizarClienteUseCase;

        @Mock
    private ActualizarParcialClienteUseCase actualizarParcialClienteUseCase;

        @Mock
    private EliminarClienteUseCase eliminarClienteUseCase;

        @Mock
    private ClienteMapper clienteMapper;

        @InjectMocks
        private ClienteController clienteController;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
        }

    @Test
    void debe_Retornar201_Cuando_PostCrearClienteEsValido() throws Exception {
        var clienteId = UUID.randomUUID();
        var response = new ClienteResponseDTO(
                clienteId,
                "Ana",
                "F",
                30,
                "123",
                "Dir",
                "300",
                true,
                Instant.now(),
                null,
                null
        );

        when(crearClienteUseCase.execute(any(), any(), any())).thenReturn(new Cliente());
        when(clienteMapper.toResponseDTO(any())).thenReturn(response);

        var request = """
                {
                  "personaId": "%s",
                  "contrasena": "Password123",
                  "estado": true
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(clienteId.toString()))
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void debe_Retornar400_Cuando_PostCrearClienteTieneContrasenaInvalida() throws Exception {
        var request = """
                {
                  "personaId": "%s",
                  "contrasena": "123",
                  "estado": true
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.contrasena").exists());
    }

    @Test
    void debe_Retornar409_Cuando_PostCrearClienteTienePersonaYaRegistrada() throws Exception {
        when(crearClienteUseCase.execute(any(), any(), any()))
                .thenThrow(new ClienteYaExisteException("El personaId ya está registrado como cliente"));

        var request = """
                {
                  "personaId": "%s",
                  "contrasena": "Password123",
                  "estado": true
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El personaId ya está registrado como cliente"));
    }

    @Test
    void debe_Retornar200_Cuando_GetConsultarClienteEsValido() throws Exception {
        var clienteId = UUID.randomUUID();
        var response = new ClienteResponseDTO(clienteId, "Ana", "F", 30, "123", "Dir", "300", true,
                Instant.now(), Instant.now(), null);

        when(consultarClienteUseCase.execute(clienteId)).thenReturn(new Cliente());
        when(clienteMapper.toResponseDTO(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/{clienteId}", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(clienteId.toString()));
    }

    @Test
    void debe_Retornar404_Cuando_GetConsultarClienteNoExiste() throws Exception {
        var clienteId = UUID.randomUUID();
        when(consultarClienteUseCase.execute(clienteId))
                .thenThrow(new ClienteNoEncontradoException("Cliente no encontrado con clienteId: " + clienteId));

        mockMvc.perform(get("/api/v1/clientes/{clienteId}", clienteId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente no encontrado con clienteId: " + clienteId));
    }

    @Test
    void debe_Retornar200_Cuando_PutActualizarClienteEsValido() throws Exception {
        var clienteId = UUID.randomUUID();
        var response = new ClienteResponseDTO(clienteId, "Ana", "F", 31, "123", "Dir", "300", true,
                Instant.now(), Instant.now(), null);

        when(actualizarClienteUseCase.execute(eq(clienteId), any(Cliente.class))).thenReturn(new Cliente());
        when(clienteMapper.toResponseDTO(any())).thenReturn(response);

        var request = """
                {
                  "nombre": "Ana",
                  "genero": "F",
                  "edad": 31,
                  "identificacion": "123",
                  "direccion": "Dir",
                  "telefono": "300",
                  "contrasena": "Password123",
                  "estado": true
                }
                """;

        mockMvc.perform(put("/api/v1/clientes/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.edad").value(31));
    }

    @Test
    void debe_Retornar400_Cuando_PutActualizarClienteTieneNombreVacio() throws Exception {
        var clienteId = UUID.randomUUID();
        var request = """
                {
                  "nombre": "",
                  "genero": "F",
                  "edad": 31,
                  "identificacion": "123",
                  "direccion": "Dir",
                  "telefono": "300",
                  "contrasena": "Password123",
                  "estado": true
                }
                """;

        mockMvc.perform(put("/api/v1/clientes/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists());
    }

    @Test
    void debe_Retornar200_Cuando_PatchActualizarParcialClienteEsValido() throws Exception {
        var clienteId = UUID.randomUUID();
        var response = new ClienteResponseDTO(clienteId, "Ana", "F", 30, "123", "Dir", "300", false,
                Instant.now(), Instant.now(), null);

        when(actualizarParcialClienteUseCase.execute(eq(clienteId), any(PatchClienteCommand.class)))
                .thenReturn(new Cliente());
        when(clienteMapper.toResponseDTO(any())).thenReturn(response);

        var request = """
                {
                  "estado": false
                }
                """;

        mockMvc.perform(patch("/api/v1/clientes/{clienteId}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    void debe_Retornar204_Cuando_DeleteEliminarClienteEsValido() throws Exception {
        var clienteId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/clientes/{clienteId}", clienteId))
                .andExpect(status().isNoContent());

        verify(eliminarClienteUseCase).execute(clienteId);
    }

    @Test
    void debe_Retornar404_Cuando_DeleteEliminarClienteNoExiste() throws Exception {
        var clienteId = UUID.randomUUID();
        doThrow(new ClienteNoEncontradoException("Cliente no encontrado con clienteId: " + clienteId))
                .when(eliminarClienteUseCase).execute(clienteId);

        mockMvc.perform(delete("/api/v1/clientes/{clienteId}", clienteId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente no encontrado con clienteId: " + clienteId));
    }

    @Test
    void debe_PermitirAccesoSinToken_Cuando_NoHaySeguridadWebConfigurada() throws Exception {
        var clienteId = UUID.randomUUID();
        var response = new ClienteResponseDTO(clienteId, "Ana", "F", 30, "123", "Dir", "300", true,
                Instant.now(), null, null);

        when(consultarClienteUseCase.execute(clienteId)).thenReturn(new Cliente());
        when(clienteMapper.toResponseDTO(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/{clienteId}", clienteId))
                .andExpect(status().isOk());
    }
}
