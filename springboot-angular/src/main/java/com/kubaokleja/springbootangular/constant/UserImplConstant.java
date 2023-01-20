package com.kubaokleja.springbootangular.constant;

import java.util.Map;

public class UserImplConstant {
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String NO_USER_FOUND_BY_USERNAME = "No user found by username: ";
    public static final String NO_USER_FOUND = "No user found";

    //TODO : Think about the better way
    public static final Map<String, String> userValidationFieldMap = Map.of(
            "username", "Username",
            "firstName", "First name",
            "lastName", "Last name",
            "password", "Password",
            "email", "Email"
    );
}
