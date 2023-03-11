package com.kubaokleja.springbootangular.exception.constant;

import java.util.Map;

public class ExceptionConstant {
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String NO_EMAIL_FOUND = "Email not found";
    public static final String NO_USER_FOUND_BY_USERNAME = "No user found by username: ";
    public static final String NO_USER_FOUND = "No user found";

    public static final Map<String, String> userValidationFieldMap = Map.of(
            "username", "Username",
            "firstName", "First name",
            "lastName", "Last name",
            "password", "Password",
            "email", "Email"
    );
}
