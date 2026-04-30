package com.banco.bank.infrastructure.web;

import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.port.in.ActualizarMovimientoUseCase;
import com.banco.bank.domain.port.in.ActualizarParcialMovimientoUseCase;
import com.banco.bank.domain.port.in.CrearMovimientoUseCase;
import com.banco.bank.domain.port.in.EliminarMovimientoUseCase;
import com.banco.bank.domain.port.in.ListarMovimientosCuentaUseCase;
import com.banco.bank.domain.port.in.ObtenerMovimientoUseCase;
import com.banco.bank.infrastructure.web.dto.MovimientoCreateRequestDTO;
import com.banco.bank.infrastructure.web.dto.MovimientoPatchRequestDTO;
import com.banco.bank.infrastructure.web.dto.MovimientoResponseDTO;
import com.banco.bank.infrastructure.web.dto.MovimientoUpdateRequestDTO;
import com.banco.bank.infrastructure.web.dto.PagedMovimientoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final CrearMovimientoUseCase crearMovimientoUseCase;
    private final ObtenerMovimientoUseCase obtenerMovimientoUseCase;
    private final ListarMovimientosCuentaUseCase listarMovimientosCuentaUseCase;
    private final ActualizarMovimientoUseCase actualizarMovimientoUseCase;
    private final ActualizarParcialMovimientoUseCase actualizarParcialMovimientoUseCase;
    private final EliminarMovimientoUseCase eliminarMovimientoUseCase;

    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> crear(@Valid @RequestBody MovimientoCreateRequestDTO dto) {
        var movimiento = new Movimiento();
        var cuentaRef = new com.banco.bank.domain.model.Cuenta();
        cuentaRef.setCuentaId(dto.cuentaId());
        movimiento.setCuenta(cuentaRef);
        movimiento.setTipoMovimiento(dto.tipoMovimiento());
        movimiento.setValor(dto.valor());
        var created = crearMovimientoUseCase.execute(movimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimientoResponseDTO.from(created));
    }

    @GetMapping("/{movimientoId}")
    public ResponseEntity<MovimientoResponseDTO> obtener(@PathVariable UUID movimientoId) {
        var movimiento = obtenerMovimientoUseCase.execute(movimientoId);
        return ResponseEntity.ok(MovimientoResponseDTO.from(movimiento));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<PagedMovimientoResponseDTO> listarPorCuenta(
            @PathVariable UUID cuentaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(required = false) String tipoMovimiento) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "fecha"));
        var result = listarMovimientosCuentaUseCase.execute(cuentaId, pageable, startDate, endDate, tipoMovimiento);
        var response = new PagedMovimientoResponseDTO(
                result.getContent().stream().map(MovimientoResponseDTO::from).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{movimientoId}")
    public ResponseEntity<MovimientoResponseDTO> actualizar(
            @PathVariable UUID movimientoId,
            @RequestBody MovimientoUpdateRequestDTO dto) {
        var updates = new Movimiento();
        updates.setTipoMovimiento(dto.tipoMovimiento());
        updates.setValor(dto.valor());
        var updated = actualizarMovimientoUseCase.execute(movimientoId, updates);
        return ResponseEntity.ok(MovimientoResponseDTO.from(updated));
    }

    @PatchMapping("/{movimientoId}")
    public ResponseEntity<MovimientoResponseDTO> actualizarParcial(
            @PathVariable UUID movimientoId,
            @RequestBody MovimientoPatchRequestDTO dto) {
        var patches = new Movimiento();
        patches.setTipoMovimiento(dto.tipoMovimiento());
        patches.setValor(dto.valor());
        var updated = actualizarParcialMovimientoUseCase.execute(movimientoId, patches);
        return ResponseEntity.ok(MovimientoResponseDTO.from(updated));
    }

    @DeleteMapping("/{movimientoId}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID movimientoId) {
        eliminarMovimientoUseCase.execute(movimientoId);
        return ResponseEntity.noContent().build();
    }
}
