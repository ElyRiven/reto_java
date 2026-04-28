package com.banco.users.domain.exception;

public class PersonaYaExisteException extends RuntimeException {

    public PersonaYaExisteException(String message) {
        super(message);
    }
}
