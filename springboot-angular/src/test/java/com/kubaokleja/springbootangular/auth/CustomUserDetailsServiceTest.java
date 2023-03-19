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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserServiceFacade userServiceFacade;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

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
    @DisplayName("User details service test - load user by username successfully")
    void givenUsername_whenUserFound_thenReturnUserPrincipal() {
        //given
        String username = userDTO.getUsername();
        //when
        when(userServiceFacade.findUserByUsername(username)).thenReturn(userDTO);
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
        //then
        assertEquals(userPrincipal.getUsername(), username);
    }

    @Test
    @DisplayName("User details service test - user not found")
    void givenUsername_whenUserNotFound_thenThrowUsernameNotFoundException() {
        //given
        String username = userDTO.getUsername();
        //when
        when(userServiceFacade.findUserByUsername(username)).thenReturn(null);
        //then
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }
}