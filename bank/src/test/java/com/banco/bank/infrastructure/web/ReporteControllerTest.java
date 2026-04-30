package com.banco.bank.infrastructure.web;

import com.banco.bank.domain.model.reporte.EstadoCuentaReporte;
import com.banco.bank.domain.port.in.ObtenerEstadoCuentaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ObtenerEstadoCuentaUseCase obtenerEstadoCuentaUseCase;

    @InjectMocks
    private ReporteController reporteController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reporteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void debe_Retornar200ConReporte_Cuando_ParametrosValidos() throws Exception {
        var clienteId = UUID.randomUUID();
        var reporte = List.of(new EstadoCuentaReporte(
                LocalDate.of(2022, 2, 11),
                "Marianela Montalvo",
                "110834",
                "Ahorros",
                new BigDecimal("700.00"),
                true,
                new BigDecimal("-200.00"),
                new BigDecimal("500.00")
        ));

        when(obtenerEstadoCuentaUseCase.execute(eq(clienteId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reporte);

        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", clienteId.toString())
                        .param("fecha", "2022-02-01,2022-02-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]['Fecha']").value("11/02/2022"))
                .andExpect(jsonPath("$[0]['Cliente']").value("Marianela Montalvo"))
                .andExpect(jsonPath("$[0]['Saldo Inicial']").value(700.0))
                .andExpect(jsonPath("$[0]['Saldo Disponible']").value(500.0));
    }

    @Test
    void debe_Retornar200ConListaVacia_Cuando_NoHayMovimientos() throws Exception {
        var clienteId = UUID.randomUUID();
        when(obtenerEstadoCuentaUseCase.execute(eq(clienteId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", clienteId.toString())
                        .param("fecha", "2026-01-01,2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debe_Retornar400_Cuando_FaltaFecha() throws Exception {
        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("El parámetro fecha es obligatorio"));
    }

    @Test
    void debe_Retornar400_Cuando_FaltaClienteId() throws Exception {
        mockMvc.perform(get("/api/v1/reportes")
                        .param("fecha", "2022-02-01,2022-02-28"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("El parámetro clienteId es obligatorio"));
    }

    @Test
    void debe_Retornar400_Cuando_ClienteIdNoEsUUID() throws Exception {
        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", "invalid-id")
                        .param("fecha", "2022-02-01,2022-02-28"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("clienteId debe ser un UUID válido"));
    }

        @Test
        void debe_Retornar400_Cuando_FormatoFechaNoTieneSeparadorComa() throws Exception {
        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", UUID.randomUUID().toString())
                        .param("fecha", "2022/02/01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("El rango de fechas debe contener dos fechas separadas por coma"));
    }

        @Test
        void debe_Retornar400_Cuando_FormatoFechaEsInvalido() throws Exception {
                mockMvc.perform(get("/api/v1/reportes")
                                                .param("clienteId", UUID.randomUUID().toString())
                                                .param("fecha", "2022-02-01,2022/02/28"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.detail").value("Formato de fecha inválido. Use: YYYY-MM-DD,YYYY-MM-DD"));
        }

    @Test
    void debe_Retornar400_Cuando_RangoNoTieneDosFechas() throws Exception {
        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", UUID.randomUUID().toString())
                        .param("fecha", "2022-02-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("El rango de fechas debe contener dos fechas separadas por coma"));
    }

    @Test
    void debe_Retornar400_Cuando_FechaInicioMayorQueFin() throws Exception {
        mockMvc.perform(get("/api/v1/reportes")
                        .param("clienteId", UUID.randomUUID().toString())
                        .param("fecha", "2022-02-28,2022-02-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("La fecha de inicio no puede ser mayor que la fecha de fin"));
    }
}
