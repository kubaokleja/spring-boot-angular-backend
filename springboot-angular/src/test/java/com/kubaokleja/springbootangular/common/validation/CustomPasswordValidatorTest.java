package com.kubaokleja.springbootangular.common.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CustomPasswordValidatorTest {

    private ConstraintValidatorContext context;
    private CustomPasswordValidator customPasswordValidator;

    @BeforeEach
    void setup() {
        context = Mockito.mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);
        when(builder.addConstraintViolation())
                .thenReturn(context);
        customPasswordValidator = new CustomPasswordValidator();
    }

    @Test
    @DisplayName("Password validation - valid password")
    void passwordCorrect() {
        assertTrue(customPasswordValidator.isValid("Password1!", context));
    }

    @Test
    @DisplayName("Password validation - password too long")
    void passwordTooLong() {
        assertFalse(customPasswordValidator.isValid("Password1!asdscsasvadsvdasvdsavdsavdsavdsvdsavasdvsad", context));
    }

    @Test
    @DisplayName("Password validation - password too short")
    void passwordTooShort() {
        assertFalse(customPasswordValidator.isValid("Prd1!", context));
    }

    @Test
    @DisplayName("Password validation - password without uppercase")
    void passwordWithoutUpperCase() {
        assertFalse(customPasswordValidator.isValid("password1!", context));
    }

    @Test
    @DisplayName("Password validation - password without lowercase")
    void passwordWithoutLowerCase() {
        assertFalse(customPasswordValidator.isValid("PASSWORD1!", context));
    }

    @Test
    @DisplayName("Password validation - password without digit")
    void passwordWithoutDigit() {
        assertFalse(customPasswordValidator.isValid("Password!", context));
    }

    @Test
    @DisplayName("Password validation - password without special char")
    void passwordWithoutSpecialChar() {
        assertFalse(customPasswordValidator.isValid("Password1", context));
    }

    @Test
    @DisplayName("Password validation - password with whitespace")
    void passwordWithWhitespace() {
        assertFalse(customPasswordValidator.isValid("Pass  word1", context));
    }
}