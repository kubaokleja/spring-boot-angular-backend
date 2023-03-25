package com.kubaokleja.springbootangular.exception;

public class UsernameExistsException extends Exception {
    public UsernameExistsException(String message) {
        super(message);
    }
}
