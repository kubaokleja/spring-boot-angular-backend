package com.kubaokleja.springbootangular.user;

import com.kubaokleja.springbootangular.exception.EmailExistsException;
import com.kubaokleja.springbootangular.exception.UsernameExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserValidator userValidator;

    @Test
    @DisplayName("User validator test - email exists")
    void givenEmail_whenEmailFound_thenThrowEmailExistsException() {
        //given
        String email = "notFree@occupied.com";
        //when
        when(userRepository.findByEmailNativeSQLNamedParam(email)).thenReturn(Optional.of(new User()));
        //then
        assertThrows(EmailExistsException.class, () -> userValidator.validateEmail(email));
    }

    @Test
    @DisplayName("User validator test - username exists")
    void givenUsername_whenEmailFound_thenThrowUsernameExistsException() {
        //given
        String username = "busy";
        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(new User()));
        //then
        assertThrows(UsernameExistsException.class, () -> userValidator.validateUsername(username));
    }


    @Test
    @DisplayName("User validator test - validate user input from file (empty fields)")
    void givenEmptyFields_whenValidateUserInputFromFile_thenReturnMessageAboutEmptyFields() {
        //given
        UserDTO userDTO = UserDTO.builder()
                .username("")
                .email("")
                .firstName("")
                .lastName("")
                .build();
        //when
        StringBuilder validationResult = userValidator.validateUserInputFromFile(userDTO);
        //then
        assertTrue(validationResult.indexOf("Username empty.") != -1);
        assertTrue(validationResult.indexOf("First name empty.") != -1);
        assertTrue(validationResult.indexOf("Last name empty.") != -1);
        assertTrue(validationResult.indexOf("Email empty.") != -1);
    }

    @Test
    @DisplayName("User validator test - username exists")
    void givenFieldsWithWrongFormat_whenValidateUserInputFromFile_thenReturnMessageAboutEmptyFields() {
        //given
        ReflectionTestUtils.setField(userValidator, "usernameMinLength", 4);
        ReflectionTestUtils.setField(userValidator, "usernameMaxLength", 6);
        ReflectionTestUtils.setField(userValidator, "nameMaxLength", 20);

        UserDTO userDTO = UserDTO.builder()
                .username("use")
                .email("testtest.pl")
                .firstName("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii")
                .lastName("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii")
                .build();
        //when
        StringBuilder validationResult = userValidator.validateUserInputFromFile(userDTO);
        //then
        assertTrue(validationResult.indexOf("Wrong username format.") != -1);
        assertTrue(validationResult.indexOf("Wrong first name format. ") != -1);
        assertTrue(validationResult.indexOf("Wrong last name format.") != -1);
        assertTrue(validationResult.indexOf("Email wrong pattern.") != -1);
    }
}