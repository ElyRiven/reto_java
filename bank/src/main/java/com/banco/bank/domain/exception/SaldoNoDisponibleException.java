package com.banco.bank.domain.exception;

public class SaldoNoDisponibleException extends RuntimeException {

    public SaldoNoDisponibleException(String message) {
        super(message);
    }
}
