package com.kubaokleja.springbootangular.auth;

import com.kubaokleja.springbootangular.user.RoleDTO;
import com.kubaokleja.springbootangular.user.UserDTO;
import com.kubaokleja.springbootangular.user.UserServiceFacade;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private UserServiceFacade userServiceFacade;
    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserDTO userDTO;

    @BeforeEach
    void setup() {
        RoleDTO role = RoleDTO.builder()
                .name("ROLE_USER")
                .build();
        userDTO = UserDTO.builder()
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

    @Test
    @DisplayName("User login test - attempt number is exceeded")
    void givenUserDTO_whenLoginAttemptExceeded_thenReturnUserLocked() {
        //given
        //when
        when(userServiceFacade.findUserByUsername(userDTO.getUsername())).thenReturn(userDTO);
        when(loginAttemptService.hasExceededMaxAttempts(userDTO.getUsername())).thenReturn(Boolean.TRUE);
        UserPrincipal loggedUser = authenticationService.login(userDTO);
        //then
        assertFalse(loggedUser::isAccountNonLocked);
    }

    @Test
    @DisplayName("User login test - attempt number is exceeded")
    void givenUserDTO_whenLoginAttemptNotExceeded_thenReturnUserWithNewLastLoginDates() {
        //given
        Date lastLoginDate = new Date();
        userDTO.setLastLoginDate(lastLoginDate);
        //when
        when(userServiceFacade.findUserByUsername(userDTO.getUsername())).thenReturn(userDTO);
        when(loginAttemptService.hasExceededMaxAttempts(userDTO.getUsername())).thenReturn(Boolean.FALSE);
        UserPrincipal loggedUser = authenticationService.login(userDTO);
        //then
        assertEquals(loggedUser.getUser().getLastLoginToDisplay(), lastLoginDate);
    }
}