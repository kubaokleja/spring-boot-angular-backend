package com.kubaokleja.springbootangular.exception;

public class EmailExistException extends Exception {
    public EmailExistException(String message) {
        super(message);
    }
}
