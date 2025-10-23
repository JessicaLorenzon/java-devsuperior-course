package com.lorenzon.desafio_03.services.exceptions;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException() {
        super("Client not found");
    }
}
