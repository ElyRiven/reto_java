package com.banco.bank.application.usecase;

import com.banco.bank.domain.model.reporte.EstadoCuentaReporte;
import com.banco.bank.domain.port.out.ReporteRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerEstadoCuentaUseCaseImplTest {

    @Mock
    private ReporteRepositoryPort reporteRepositoryPort;

    @InjectMocks
    private ObtenerEstadoCuentaUseCaseImpl useCase;

    @Test
    void debe_RetornarListaConResultados_Cuando_ExistenMovimientos() {
        var clienteId = UUID.randomUUID();
        var inicio = LocalDate.of(2022, 2, 1);
        var fin = LocalDate.of(2022, 2, 28);
        var expected = List.of(
                new EstadoCuentaReporte(
                        LocalDate.of(2022, 2, 11),
                        "Marianela Montalvo",
                        "110834",
                        "Ahorros",
                        new BigDecimal("700.00"),
                        true,
                        new BigDecimal("-200.00"),
                        new BigDecimal("500.00")
                )
        );

        when(reporteRepositoryPort.findEstadoCuenta(clienteId, inicio, fin)).thenReturn(expected);

        var result = useCase.execute(clienteId, inicio, fin);

        assertEquals(1, result.size());
        assertEquals("110834", result.get(0).numeroCuenta());
        verify(reporteRepositoryPort).findEstadoCuenta(clienteId, inicio, fin);
    }

    @Test
    void debe_RetornarListaVacia_Cuando_NoHayMovimientosEnRango() {
        var clienteId = UUID.randomUUID();
        var inicio = LocalDate.of(2026, 1, 1);
        var fin = LocalDate.of(2026, 1, 31);

        when(reporteRepositoryPort.findEstadoCuenta(clienteId, inicio, fin)).thenReturn(List.of());

        var result = useCase.execute(clienteId, inicio, fin);

        assertEquals(0, result.size());
        verify(reporteRepositoryPort).findEstadoCuenta(clienteId, inicio, fin);
    }
}
