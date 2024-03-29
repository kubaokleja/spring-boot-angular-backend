package com.kubaokleja.springbootangular.auth;

public class SecurityConstant {
    public static final long EXPIRATION_TIME =  3_600_000L;// 1 hour
    public static final String TOKEN_HEADER =  "Bearer ";
    public static final String JWT_TOKEN_HEADER =  "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED =  "Token cannot be verified";
    public static final String TOKEN_PROVIDER =  "Kuba";
    public static final String KUBA_ADMINISTRATION =  "Spring Angular App";
    public static final String AUTHORITIES =  "Authorities";
    public static final String FORBIDDEN_MESSAGE =  "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {
            "/auth/login", "/users/confirm/**", "/users/reset-password/**"
    };
    public static final String ADMIN = "admin";
}
