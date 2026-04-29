package com.banco.bank.infrastructure.web;

import com.banco.bank.domain.exception.ClienteNoEncontradoException;
import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.exception.CuentaTieneMovimientosException;
import com.banco.bank.domain.exception.MovimientoNoEncontradoException;
import com.banco.bank.domain.exception.NumeroCuentaDuplicadoException;
import com.banco.bank.domain.exception.NumeroCuentaInvalidoException;
import com.banco.bank.domain.exception.SaldoNoDisponibleException;
import com.banco.bank.domain.exception.TipoCuentaInvalidoException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CuentaNoEncontradaException.class)
    public ProblemDetail handleCuentaNoEncontrada(CuentaNoEncontradaException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Cuenta no encontrada");
        problem.setType(URI.create("/errors/cuenta-no-encontrada"));
        return problem;
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ProblemDetail handleClienteNoEncontrado(ClienteNoEncontradoException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Cliente no encontrado");
        problem.setType(URI.create("/errors/cliente-no-encontrado"));
        return problem;
    }

    @ExceptionHandler(NumeroCuentaDuplicadoException.class)
    public ProblemDetail handleNumeroCuentaDuplicado(NumeroCuentaDuplicadoException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Número de cuenta duplicado");
        problem.setType(URI.create("/errors/numero-cuenta-duplicado"));
        return problem;
    }

    @ExceptionHandler(NumeroCuentaInvalidoException.class)
    public ProblemDetail handleNumeroCuentaInvalido(NumeroCuentaInvalidoException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Número de cuenta inválido");
        problem.setType(URI.create("/errors/numero-cuenta-invalido"));
        return problem;
    }

    @ExceptionHandler(TipoCuentaInvalidoException.class)
    public ProblemDetail handleTipoCuentaInvalido(TipoCuentaInvalidoException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Tipo de cuenta inválido");
        problem.setType(URI.create("/errors/tipo-cuenta-invalido"));
        return problem;
    }

    @ExceptionHandler(CuentaTieneMovimientosException.class)
    public ProblemDetail handleCuentaTieneMovimientos(CuentaTieneMovimientosException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Cuenta con movimientos asociados");
        problem.setType(URI.create("/errors/cuenta-tiene-movimientos"));
        return problem;
    }

    @ExceptionHandler(SaldoNoDisponibleException.class)
    public ProblemDetail handleSaldoNoDisponible(SaldoNoDisponibleException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Saldo no disponible");
        problem.setType(URI.create("/errors/saldo-no-disponible"));
        return problem;
    }

    @ExceptionHandler(MovimientoNoEncontradoException.class)
    public ProblemDetail handleMovimientoNoEncontrado(MovimientoNoEncontradoException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Movimiento no encontrado");
        problem.setType(URI.create("/errors/movimiento-no-encontrado"));
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Solicitud inválida");
        problem.setType(URI.create("/errors/solicitud-invalida"));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        var detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "; " + b);
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle("Error de validación");
        problem.setType(URI.create("/errors/validacion"));
        return problem;
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ProblemDetail handleDataAccessError(InvalidDataAccessResourceUsageException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Solicitud inválida. Verifique los parámetros enviados.");
        problem.setTitle("Error de acceso a datos");
        problem.setType(URI.create("/errors/error-datos"));
        return problem;
    }
}
