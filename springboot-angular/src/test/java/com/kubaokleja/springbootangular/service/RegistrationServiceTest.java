package com.kubaokleja.springbootangular.service;

import com.kubaokleja.springbootangular.dto.UserDTO;
import com.kubaokleja.springbootangular.entity.Role;
import com.kubaokleja.springbootangular.entity.User;
import com.kubaokleja.springbootangular.exception.EmailExistException;
import com.kubaokleja.springbootangular.exception.UsernameExistException;
import com.kubaokleja.springbootangular.repository.RoleRepository;
import com.kubaokleja.springbootangular.repository.UserRepository;
import com.kubaokleja.springbootangular.validation.UserValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private RegistrationService registrationService;

    private UserDTO userDTO;

    @BeforeEach
    public void setup(){
        userDTO = UserDTO.builder()
                .username("username")
                .email("test@test.pl")
                .build();
    }

    @Test
    @DisplayName("User registration - positive scenario")
    public void givenUserObject_whenRegisterUser_thenReturnUserObject() throws EmailExistException, UsernameExistException {
        //given
        Role role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        User user = User.builder()
                .id(1L)
                .userId(RandomStringUtils.randomNumeric(10))
                .username("username")
                .password("password")
                .email("test@test.pl")
                .firstName("first_name")
                .lastName("last_name")
                .joinDate(new Date())
                .isActive(true)
                .isNotLocked(true)
                .roles(List.of(role))
                .build();

        given(roleRepository.findByName(anyString())).willReturn(role);
        given(userRepository.save(any(User.class))).willReturn(user);

        //when
        User savedUser = registrationService.register(userDTO);

        //then
        assertThat(savedUser).isNotNull();

    }

    @Test
    @DisplayName("User registration - username exists")
    public void givenUserObject_whenRegisterUserUsernameExists_thenThrowUsernameExistException() throws EmailExistException, UsernameExistException {
        //given
        doThrow(UsernameExistException.class).when(userValidator).validateUsernameAndEmail(any(), any());

        //when
        assertThrows(UsernameExistException.class, () ->{
            registrationService.register(userDTO);
        });

        //then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User registration - email exists")
    public void givenUserObject_whenRegisterUserEmailExists_thenThrowEmailExistException() throws EmailExistException, UsernameExistException {
        //given
        doThrow(EmailExistException.class).when(userValidator).validateUsernameAndEmail(any(), any());

        //when
        assertThrows(EmailExistException.class, () ->{
            registrationService.register(userDTO);
        });

        //then
        verify(userRepository, never()).save(any(User.class));
    }
}