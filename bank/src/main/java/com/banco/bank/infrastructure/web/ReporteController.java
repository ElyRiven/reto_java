package com.banco.bank.infrastructure.web;

import com.banco.bank.domain.port.in.ObtenerEstadoCuentaUseCase;
import com.banco.bank.infrastructure.web.dto.EstadoCuentaReporteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ObtenerEstadoCuentaUseCase obtenerEstadoCuentaUseCase;

    @GetMapping
    public ResponseEntity<List<EstadoCuentaReporteDTO>> obtenerEstadoCuenta(
            @RequestParam(name = "clienteId", required = false) String clienteId,
            @RequestParam(name = "fecha", required = false) String fecha) {

        var clienteUUID = validarClienteId(clienteId);
        var rango = validarFecha(fecha);

        var reporte = obtenerEstadoCuentaUseCase.execute(clienteUUID, rango[0], rango[1]);
        var response = reporte.stream()
                .map(EstadoCuentaReporteDTO::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    private UUID validarClienteId(String clienteId) {
        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException("El parámetro clienteId es obligatorio");
        }
        try {
            return UUID.fromString(clienteId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("clienteId debe ser un UUID válido");
        }
    }

    private LocalDate[] validarFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException("El parámetro fecha es obligatorio");
        }
        var partes = fecha.split(",", -1);
        if (partes.length != 2 || partes[0].isBlank() || partes[1].isBlank()) {
            throw new IllegalArgumentException("El rango de fechas debe contener dos fechas separadas por coma");
        }
        LocalDate fechaInicio;
        LocalDate fechaFin;
        try {
            fechaInicio = LocalDate.parse(partes[0].trim());
            fechaFin = LocalDate.parse(partes[1].trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use: YYYY-MM-DD,YYYY-MM-DD");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser mayor que la fecha de fin");
        }
        return new LocalDate[]{fechaInicio, fechaFin};
    }
}
