package com.banco.bank.infrastructure.web;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.exception.CuentaTieneMovimientosException;
import com.banco.bank.domain.exception.NumeroCuentaDuplicadoException;
import com.banco.bank.domain.exception.TipoCuentaInvalidoException;
import com.banco.bank.domain.model.Cliente;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.port.in.ActualizarCuentaUseCase;
import com.banco.bank.domain.port.in.ActualizarParcialCuentaUseCase;
import com.banco.bank.domain.port.in.CrearCuentaUseCase;
import com.banco.bank.domain.port.in.EliminarCuentaUseCase;
import com.banco.bank.domain.port.in.ListarCuentasClienteUseCase;
import com.banco.bank.domain.port.in.ObtenerCuentaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
class CuentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CrearCuentaUseCase crearCuentaUseCase;
    @Mock
    private ObtenerCuentaUseCase obtenerCuentaUseCase;
    @Mock
    private ListarCuentasClienteUseCase listarCuentasClienteUseCase;
    @Mock
    private ActualizarCuentaUseCase actualizarCuentaUseCase;
    @Mock
    private ActualizarParcialCuentaUseCase actualizarParcialCuentaUseCase;
    @Mock
    private EliminarCuentaUseCase eliminarCuentaUseCase;

    @InjectMocks
    private CuentaController cuentaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cuentaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void debe_Retornar201_Cuando_PostCrearCuentaEsValido() throws Exception {
        var cuenta = buildCuenta();
        when(crearCuentaUseCase.execute(any(Cuenta.class))).thenReturn(cuenta);

        var request = """
                {
                  "clienteId": "%s",
                  "numeroCuenta": "123456789",
                  "tipoCuenta": "Ahorros",
                  "saldoInicial": 1500.99,
                  "estado": true
                }
                """.formatted(cuenta.getCliente().getClienteId());

        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cuentaId").value(cuenta.getCuentaId().toString()))
                .andExpect(jsonPath("$.numeroCuenta").value("123456789"));
    }

    @Test
    void debe_Retornar400_Cuando_PostCrearCuentaTieneNumeroCuentaInvalido() throws Exception {
        var request = """
                {
                  "clienteId": "%s",
                  "numeroCuenta": "ABC123",
                  "tipoCuenta": "Ahorros",
                  "saldoInicial": 1500.99,
                  "estado": true
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe_Retornar409_Cuando_PostCrearCuentaTieneNumeroDuplicado() throws Exception {
        when(crearCuentaUseCase.execute(any(Cuenta.class)))
                .thenThrow(new NumeroCuentaDuplicadoException("Número de cuenta ya existe"));

        var request = """
                {
                  "clienteId": "%s",
                  "numeroCuenta": "123456789",
                  "tipoCuenta": "Ahorros",
                  "saldoInicial": 1500.99,
                  "estado": true
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Número de cuenta ya existe"));
    }

    @Test
    void debe_Retornar200_Cuando_GetObtenerCuentaEsValido() throws Exception {
        var cuenta = buildCuenta();
        when(obtenerCuentaUseCase.execute(cuenta.getCuentaId())).thenReturn(cuenta);

        mockMvc.perform(get("/api/v1/cuentas/{cuentaId}", cuenta.getCuentaId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cuentaId").value(cuenta.getCuentaId().toString()))
                .andExpect(jsonPath("$.tipoCuenta").value("Ahorros"));
    }

    @Test
    void debe_Retornar404_Cuando_GetObtenerCuentaNoExiste() throws Exception {
        var cuentaId = UUID.randomUUID();
        when(obtenerCuentaUseCase.execute(cuentaId)).thenThrow(new CuentaNoEncontradaException("Cuenta no encontrada"));

        mockMvc.perform(get("/api/v1/cuentas/{cuentaId}", cuentaId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Cuenta no encontrada"));
    }

    @Test
    void debe_Retornar200_Cuando_GetListarPorCliente() throws Exception {
        var cuenta = buildCuenta();
        var clienteId = cuenta.getCliente().getClienteId();
        var page = new PageImpl<>(List.of(cuenta), PageRequest.of(0, 10), 1);

        when(listarCuentasClienteUseCase.execute(eq(clienteId), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/cuentas/cliente/{clienteId}", clienteId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].numeroCuenta").value("123456789"));
    }

    @Test
    void debe_Retornar200_Cuando_GetListarPorClienteSinCuentas() throws Exception {
        var clienteId = UUID.randomUUID();
        var page = new PageImpl<Cuenta>(List.of(), PageRequest.of(0, 10), 0);

        when(listarCuentasClienteUseCase.execute(eq(clienteId), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/cuentas/cliente/{clienteId}", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debe_Retornar200_Cuando_PutActualizarCuentaEsValido() throws Exception {
        var cuenta = buildCuenta();
        cuenta.setTipoCuenta("Corriente");
        when(actualizarCuentaUseCase.execute(eq(cuenta.getCuentaId()), any(Cuenta.class))).thenReturn(cuenta);

        var request = """
                {
                  "tipoCuenta": "Corriente",
                  "saldoInicial": 1700.00,
                  "estado": true
                }
                """;

        mockMvc.perform(put("/api/v1/cuentas/{cuentaId}", cuenta.getCuentaId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoCuenta").value("Corriente"));
    }

    @Test
    void debe_Retornar400_Cuando_PutActualizarCuentaTieneTipoInvalido() throws Exception {
        var cuentaId = UUID.randomUUID();
        when(actualizarCuentaUseCase.execute(eq(cuentaId), any(Cuenta.class)))
                .thenThrow(new TipoCuentaInvalidoException("Tipo de cuenta debe ser 'Ahorros' o 'Corriente'"));

        var request = """
                {
                  "tipoCuenta": "VIP"
                }
                """;

        mockMvc.perform(put("/api/v1/cuentas/{cuentaId}", cuentaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Tipo de cuenta debe ser 'Ahorros' o 'Corriente'"));
    }

    @Test
    void debe_Retornar200_Cuando_PatchActualizarParcialCuentaEsValido() throws Exception {
        var cuenta = buildCuenta();
        cuenta.setEstado(false);
        when(actualizarParcialCuentaUseCase.execute(eq(cuenta.getCuentaId()), any(Cuenta.class))).thenReturn(cuenta);

        var request = """
                {
                  "estado": false
                }
                """;

        mockMvc.perform(patch("/api/v1/cuentas/{cuentaId}", cuenta.getCuentaId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    void debe_Retornar400_Cuando_PatchEsVacio() throws Exception {
        var cuentaId = UUID.randomUUID();
        when(actualizarParcialCuentaUseCase.execute(eq(cuentaId), any(Cuenta.class)))
                .thenThrow(new IllegalArgumentException("Al menos un campo debe ser proporcionado"));

        mockMvc.perform(patch("/api/v1/cuentas/{cuentaId}", cuentaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Al menos un campo debe ser proporcionado"));
    }

    @Test
    void debe_Retornar204_Cuando_DeleteEliminarCuentaEsValido() throws Exception {
        var cuentaId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/cuentas/{cuentaId}", cuentaId))
                .andExpect(status().isNoContent());

        verify(eliminarCuentaUseCase).execute(cuentaId);
    }

    @Test
    void debe_Retornar409_Cuando_DeleteCuentaTieneMovimientos() throws Exception {
        var cuentaId = UUID.randomUUID();
        doThrow(new CuentaTieneMovimientosException("La cuenta tiene movimientos asociados y no puede ser eliminada"))
                .when(eliminarCuentaUseCase).execute(cuentaId);

        mockMvc.perform(delete("/api/v1/cuentas/{cuentaId}", cuentaId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("La cuenta tiene movimientos asociados y no puede ser eliminada"));
    }

    private Cuenta buildCuenta() {
        var cuenta = new Cuenta();
        cuenta.setCuentaId(UUID.randomUUID());
        var cliente = new Cliente();
        cliente.setClienteId(UUID.randomUUID());
        cliente.setNombre("Ana");
        cliente.setEstado(true);
        cliente.setCreatedAt(Instant.now());
        cuenta.setCliente(cliente);
        cuenta.setNumeroCuenta("123456789");
        cuenta.setTipoCuenta("Ahorros");
        cuenta.setSaldoInicial(new BigDecimal("1500.99"));
        cuenta.setEstado(true);
        cuenta.setCreatedAt(Instant.now());
        return cuenta;
    }
}
