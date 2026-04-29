package com.banco.bank.infrastructure.web;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.exception.MovimientoNoEncontradoException;
import com.banco.bank.domain.exception.SaldoNoDisponibleException;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.port.in.ActualizarMovimientoUseCase;
import com.banco.bank.domain.port.in.ActualizarParcialMovimientoUseCase;
import com.banco.bank.domain.port.in.CrearMovimientoUseCase;
import com.banco.bank.domain.port.in.EliminarMovimientoUseCase;
import com.banco.bank.domain.port.in.ListarMovimientosCuentaUseCase;
import com.banco.bank.domain.port.in.ObtenerMovimientoUseCase;
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
class MovimientoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CrearMovimientoUseCase crearMovimientoUseCase;
    @Mock
    private ObtenerMovimientoUseCase obtenerMovimientoUseCase;
    @Mock
    private ListarMovimientosCuentaUseCase listarMovimientosCuentaUseCase;
    @Mock
    private ActualizarMovimientoUseCase actualizarMovimientoUseCase;
    @Mock
    private ActualizarParcialMovimientoUseCase actualizarParcialMovimientoUseCase;
    @Mock
    private EliminarMovimientoUseCase eliminarMovimientoUseCase;

    @InjectMocks
    private MovimientoController movimientoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(movimientoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void debe_Retornar201_Cuando_PostCrearMovimientoEsValido() throws Exception {
        var movimiento = buildMovimiento();
        when(crearMovimientoUseCase.execute(any(Movimiento.class))).thenReturn(movimiento);

        var request = """
                {
                  "cuentaId": "%s",
                  "tipoMovimiento": "Depósito",
                  "valor": 500.00
                }
                """.formatted(movimiento.getCuenta().getCuentaId());

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movimientoId").value(movimiento.getMovimientoId().toString()))
                .andExpect(jsonPath("$.tipoMovimiento").value("Depósito"));
    }

    @Test
    void debe_Retornar400_Cuando_PostCrearMovimientoSinCuentaId() throws Exception {
        var request = """
                {
                  "tipoMovimiento": "Depósito",
                  "valor": 500.00
                }
                """;

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe_Retornar400_Cuando_PostCrearMovimientoConCuentaNoEncontrada() throws Exception {
        when(crearMovimientoUseCase.execute(any(Movimiento.class)))
                .thenThrow(new CuentaNoEncontradaException("Cuenta no encontrada"));

        var request = """
                {
                  "cuentaId": "%s",
                  "tipoMovimiento": "Depósito",
                  "valor": 500.00
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Cuenta no encontrada"));
    }

    @Test
    void debe_Retornar400_Cuando_PostCrearMovimientoSaldoNoDisponible() throws Exception {
        when(crearMovimientoUseCase.execute(any(Movimiento.class)))
                .thenThrow(new SaldoNoDisponibleException("Saldo no disponible"));

        var request = """
                {
                  "cuentaId": "%s",
                  "tipoMovimiento": "Retiro",
                  "valor": -1000.00
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Saldo no disponible"));
    }

    @Test
    void debe_Retornar200_Cuando_GetObtenerMovimientoEsValido() throws Exception {
        var movimiento = buildMovimiento();
        when(obtenerMovimientoUseCase.execute(movimiento.getMovimientoId())).thenReturn(movimiento);

        mockMvc.perform(get("/api/v1/movimientos/{movimientoId}", movimiento.getMovimientoId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movimientoId").value(movimiento.getMovimientoId().toString()));
    }

    @Test
    void debe_Retornar404_Cuando_GetObtenerMovimientoNoExiste() throws Exception {
        var movimientoId = UUID.randomUUID();
        when(obtenerMovimientoUseCase.execute(movimientoId))
                .thenThrow(new MovimientoNoEncontradoException("Movimiento no encontrado"));

        mockMvc.perform(get("/api/v1/movimientos/{movimientoId}", movimientoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Movimiento no encontrado"));
    }

    @Test
    void debe_Retornar200_Cuando_GetListarMovimientosPorCuenta() throws Exception {
        var movimiento = buildMovimiento();
        var cuentaId = movimiento.getCuenta().getCuentaId();
        var page = new PageImpl<>(List.of(movimiento), PageRequest.of(0, 10), 1);

        when(listarMovimientosCuentaUseCase.execute(eq(cuentaId), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/movimientos/cuenta/{cuentaId}", cuentaId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void debe_Retornar200_Cuando_PutActualizarMovimientoEsValido() throws Exception {
        var movimiento = buildMovimiento();
        when(actualizarMovimientoUseCase.execute(eq(movimiento.getMovimientoId()), any(Movimiento.class))).thenReturn(movimiento);

        var request = """
                {
                  "tipoMovimiento": "Depósito",
                  "valor": 300.00
                }
                """;

        mockMvc.perform(put("/api/v1/movimientos/{movimientoId}", movimiento.getMovimientoId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(500.0));
    }

    @Test
    void debe_Retornar200_Cuando_PatchActualizarParcialMovimientoEsValido() throws Exception {
        var movimiento = buildMovimiento();
        when(actualizarParcialMovimientoUseCase.execute(eq(movimiento.getMovimientoId()), any(Movimiento.class))).thenReturn(movimiento);

        var request = """
                {
                  "valor": 200.00
                }
                """;

        mockMvc.perform(patch("/api/v1/movimientos/{movimientoId}", movimiento.getMovimientoId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movimientoId").value(movimiento.getMovimientoId().toString()));
    }

    @Test
    void debe_Retornar204_Cuando_DeleteEliminarMovimientoEsValido() throws Exception {
        var movimientoId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/movimientos/{movimientoId}", movimientoId))
                .andExpect(status().isNoContent());

        verify(eliminarMovimientoUseCase).execute(movimientoId);
    }

    @Test
    void debe_Retornar404_Cuando_DeleteMovimientoNoExiste() throws Exception {
        var movimientoId = UUID.randomUUID();
        doThrow(new MovimientoNoEncontradoException("Movimiento no encontrado"))
                .when(eliminarMovimientoUseCase).execute(movimientoId);

        mockMvc.perform(delete("/api/v1/movimientos/{movimientoId}", movimientoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Movimiento no encontrado"));
    }

    private Movimiento buildMovimiento() {
        var cuenta = new Cuenta();
        cuenta.setCuentaId(UUID.randomUUID());
        cuenta.setClienteId(UUID.randomUUID());
        cuenta.setNumeroCuenta("100200300");
        cuenta.setTipoCuenta("Corriente");
        cuenta.setSaldoInicial(new BigDecimal("1500.00"));
        cuenta.setEstado(true);

        var movimiento = new Movimiento();
        movimiento.setMovimientoId(UUID.randomUUID());
        movimiento.setCuenta(cuenta);
        movimiento.setFecha(Instant.now());
        movimiento.setTipoMovimiento("Depósito");
        movimiento.setValor(new BigDecimal("500.00"));
        movimiento.setSaldo(new BigDecimal("1500.00"));
        movimiento.setCreatedAt(Instant.now());
        return movimiento;
    }
}
