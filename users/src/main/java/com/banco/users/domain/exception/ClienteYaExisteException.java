package com.banco.users.domain.exception;

public class ClienteYaExisteException extends RuntimeException {

    public ClienteYaExisteException(String message) {
        super(message);
    }
}
