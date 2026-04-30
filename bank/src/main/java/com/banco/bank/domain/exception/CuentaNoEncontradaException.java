package com.banco.bank.domain.exception;

public class CuentaNoEncontradaException extends RuntimeException {

    public CuentaNoEncontradaException(String message) {
        super(message);
    }
}
