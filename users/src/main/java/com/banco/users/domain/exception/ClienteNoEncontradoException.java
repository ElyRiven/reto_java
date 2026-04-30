package com.banco.users.domain.exception;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(String message) {
        super(message);
    }
}
