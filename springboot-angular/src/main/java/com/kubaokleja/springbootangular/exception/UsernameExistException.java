package com.kubaokleja.springbootangular.exception;

public class UsernameExistException extends Exception {
    public UsernameExistException(String message) {
        super(message);
    }
}
