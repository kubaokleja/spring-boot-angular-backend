package com.kubaokleja.springbootangular.service;

import com.kubaokleja.springbootangular.dto.UserDTO;
import com.kubaokleja.springbootangular.entity.Role;
import com.kubaokleja.springbootangular.entity.User;
import com.kubaokleja.springbootangular.exception.EmailExistException;
import com.kubaokleja.springbootangular.exception.EmailNotFoundException;
import com.kubaokleja.springbootangular.repository.UserRepository;
import com.kubaokleja.springbootangular.service.email.EmailSender;
import com.kubaokleja.springbootangular.validation.UserValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private Authentication auth;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LoginAttemptService loginAttemptService;
    @Mock
    private UserValidator userValidator;
    @Mock
    private EmailSender emailSender;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserDTO userDTO;
    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        userDTO = UserDTO.builder()
                .username("username")
                .email("test@test.pl")
                .build();

        role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        user = User.builder()
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

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("User details - loadUserByUsername throw exception when username not found")
    void givenFakeUsername_whenLoadUserByUsername_thenThrowUsernameNotFoundException() {
        //given
        String username = "FakeUsername";
        given(userRepository.findUserByUsername(username)).willReturn(null);
        //when
        assertThrows(UsernameNotFoundException.class, () ->{
            customUserDetailsService.loadUserByUsername(username);
        });
        //then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User details - loadUserByUsername positive scenario")
    void givenUsername_whenLoadUserByUsername_thenUserFound() {
        //given
        String username = "username";
        given(userRepository.findUserByUsername(username)).willReturn(user);
        //when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        //then
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    @DisplayName("User details - user update posistive scenario")
    void givenUserIdAndUserDTO_whenUserUpdate_thenReturnUpdatedUser() throws EmailExistException {
        //given
        given(userRepository.findUserByUsername("username")).willReturn(user);
        given(userRepository.findUserByUserId(user.getUserId())).willReturn(user);
        given(userRepository.save(any(User.class))).willReturn(user);
        userDTO.setFirstName("Updated");

        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);
        User updatedUser = customUserDetailsService.updateUser(user.getUserId(), userDTO);

        //then
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
    }


    @Test
    @DisplayName("User details - user update email already exists")
    public void givenUserObject_whenUpdateUserEmailExists_thenThrowEmailExistException() throws EmailExistException {
        //given
        given(userRepository.findUserByUsername("username")).willReturn(user);
        given(userRepository.findUserByUserId(user.getUserId())).willReturn(user);
        doThrow(EmailExistException.class).when(userValidator).validateEmail(any());
        userDTO.setEmail("fake@mail.pl");
        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThrows(EmailExistException.class, () ->{
            customUserDetailsService.updateUser(user.getUserId(), userDTO);
        });

        //then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User details - user delete")
    void givenExistingUser_whenUserExists_thenDeleteUser() {
        //given
        given(userRepository.findUserByUsername("username")).willReturn(user);
        given(userRepository.findUserByUserId(user.getUserId())).willReturn(user);

        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);
        customUserDetailsService.deleteUser(user.getUserId());

        //then
        verify(userRepository, times(1)).delete(any(User.class));

    }

    @Test
    @DisplayName("User details - reset password - email not exists")
    void givenFakeEmail_WhenUserNotFound_thanThrowEmailNotFoundException() {
        //given
        String email = "FakeEmail";
        given(userRepository.findByEmailNativeSQLNamedParam(email)).willReturn(null);
        //when
        assertThrows(EmailNotFoundException.class, () ->{
            customUserDetailsService.resetPassword(email);
        });
        //then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User details - reset password - email not exists")
    void givenEmail_WhenUserFound_thanResetPassword() throws EmailNotFoundException {
        //given
        given(userRepository.findByEmailNativeSQLNamedParam(user.getEmail())).willReturn(user);
        //when
        customUserDetailsService.resetPassword(user.getEmail());
        //then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("User details - change password - positive scenario")
    void givenPassword_WhenChangePassword_ThenSaveUserWithNewPassword() {
        //given
        given(userRepository.findUserByUsername("username")).willReturn(user);

        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);
        customUserDetailsService.changePassword("password");
        //then
        verify(userRepository, times(1)).save(any(User.class));
    }
}