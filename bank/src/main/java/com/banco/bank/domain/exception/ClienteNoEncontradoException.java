package com.banco.bank.domain.exception;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(String message) {
        super(message);
    }
}
