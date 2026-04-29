package com.banco.bank.infrastructure.web;

import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.port.in.ActualizarCuentaUseCase;
import com.banco.bank.domain.port.in.ActualizarParcialCuentaUseCase;
import com.banco.bank.domain.port.in.CrearCuentaUseCase;
import com.banco.bank.domain.port.in.EliminarCuentaUseCase;
import com.banco.bank.domain.port.in.ListarCuentasClienteUseCase;
import com.banco.bank.domain.port.in.ObtenerCuentaUseCase;
import com.banco.bank.infrastructure.web.dto.CuentaCreateRequestDTO;
import com.banco.bank.infrastructure.web.dto.CuentaPatchRequestDTO;
import com.banco.bank.infrastructure.web.dto.CuentaResponseDTO;
import com.banco.bank.infrastructure.web.dto.CuentaUpdateRequestDTO;
import com.banco.bank.infrastructure.web.dto.PagedCuentaResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CrearCuentaUseCase crearCuentaUseCase;
    private final ObtenerCuentaUseCase obtenerCuentaUseCase;
    private final ListarCuentasClienteUseCase listarCuentasClienteUseCase;
    private final ActualizarCuentaUseCase actualizarCuentaUseCase;
    private final ActualizarParcialCuentaUseCase actualizarParcialCuentaUseCase;
    private final EliminarCuentaUseCase eliminarCuentaUseCase;

    @PostMapping
    public ResponseEntity<CuentaResponseDTO> crear(@Valid @RequestBody CuentaCreateRequestDTO dto) {
        var cuenta = new Cuenta();
        var clienteRef = new com.banco.bank.domain.model.Cliente();
        clienteRef.setClienteId(dto.clienteId());
        cuenta.setCliente(clienteRef);
        cuenta.setNumeroCuenta(dto.numeroCuenta());
        cuenta.setTipoCuenta(dto.tipoCuenta());
        cuenta.setSaldoInicial(dto.saldoInicial());
        cuenta.setEstado(dto.estado());
        var created = crearCuentaUseCase.execute(cuenta);
        return ResponseEntity.status(HttpStatus.CREATED).body(CuentaResponseDTO.from(created));
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<CuentaResponseDTO> obtener(@PathVariable UUID cuentaId) {
        var cuenta = obtenerCuentaUseCase.execute(cuentaId);
        return ResponseEntity.ok(CuentaResponseDTO.from(cuenta));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<PagedCuentaResponseDTO> listarPorCliente(
            @PathVariable UUID clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = listarCuentasClienteUseCase.execute(clienteId, pageable);
        var response = new PagedCuentaResponseDTO(
                result.getContent().stream().map(CuentaResponseDTO::from).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cuentaId}")
    public ResponseEntity<CuentaResponseDTO> actualizar(
            @PathVariable UUID cuentaId,
            @RequestBody CuentaUpdateRequestDTO dto) {
        var updates = new Cuenta();
        updates.setTipoCuenta(dto.tipoCuenta());
        updates.setSaldoInicial(dto.saldoInicial());
        updates.setEstado(dto.estado());
        var updated = actualizarCuentaUseCase.execute(cuentaId, updates);
        return ResponseEntity.ok(CuentaResponseDTO.from(updated));
    }

    @PatchMapping("/{cuentaId}")
    public ResponseEntity<CuentaResponseDTO> actualizarParcial(
            @PathVariable UUID cuentaId,
            @RequestBody CuentaPatchRequestDTO dto) {
        var patchData = new Cuenta();
        patchData.setTipoCuenta(dto.tipoCuenta());
        patchData.setSaldoInicial(dto.saldoInicial());
        patchData.setEstado(dto.estado());
        var updated = actualizarParcialCuentaUseCase.execute(cuentaId, patchData);
        return ResponseEntity.ok(CuentaResponseDTO.from(updated));
    }

    @DeleteMapping("/{cuentaId}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID cuentaId) {
        eliminarCuentaUseCase.execute(cuentaId);
        return ResponseEntity.noContent().build();
    }
}
