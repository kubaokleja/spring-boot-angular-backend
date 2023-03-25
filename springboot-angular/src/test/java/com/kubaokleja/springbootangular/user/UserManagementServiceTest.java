package com.kubaokleja.springbootangular.user;

import com.kubaokleja.springbootangular.email.EmailFacade;
import com.kubaokleja.springbootangular.exception.EmailExistsException;
import com.kubaokleja.springbootangular.exception.UserNotFoundException;
import com.kubaokleja.springbootangular.exception.UsernameExistsException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserValidator userValidator;
    @Mock
    private EmailFacade emailFacade;

    @InjectMocks
    private UserManagementService userManagementService;

    private UserDTO userDTO;
    private Role role;
    private User user;

    @BeforeEach
    public void setup(){
        userDTO = UserDTO.builder()
                .username("username")
                .email("test@test.pl")
                .build();

        role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .authorities(new ArrayList<>())
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

    @Test
    @DisplayName("User management - create user by admin (positive)")
    public void givenUserObject_whenCreateUser_thenReturnUserObject() throws EmailExistsException, UsernameExistsException {
        //given
        given(roleRepository.findByName(anyString())).willReturn(Optional.ofNullable(role));
        given(userRepository.save(any())).willReturn(user);

        //when
        UserDTO savedUser = userManagementService.createUser(userDTO);

        //then
        assertThat(savedUser).isNotNull();
    }

    @Test
    @DisplayName("User management - create user by admin. Username already exists.")
    public void givenUserObject_whenUpdateUserUsernameExists_thenThrowUsernameExistException() throws EmailExistsException, UsernameExistsException {
        //given
        doThrow(UsernameExistsException.class).when(userValidator).validateUsernameAndEmail(any(), any());
        //when
        assertThrows(UsernameExistsException.class, () ->{
            userManagementService.createUser(userDTO);
        });

        //then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User management - create user by admin. Email already exists.")
    public void givenUserObject_whenUpdateUserEmailExists_thenThrowEmailExistException() throws EmailExistsException, UsernameExistsException {
        //given
        doThrow(EmailExistsException.class).when(userValidator).validateUsernameAndEmail(any(), any());

        //when
        assertThrows(EmailExistsException.class, () ->{
            userManagementService.createUser(userDTO);
        });

        //then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User management - update user by admin (positive). ")
    public void givenUserObject_whenUpdateUser_thenReturnUpdatedObject() throws UserNotFoundException, EmailExistsException {
        //given
        given(userRepository.findUserByUsername(userDTO.getUsername())).willReturn(Optional.ofNullable(user));
        given(userRepository.save(any(User.class))).willReturn(user);
        userDTO.setFirstName("Updated");

        //when
        UserDTO updatedUser = userManagementService.updateUser(userDTO);

        //then
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("User management - delete user by admin (positive). ")
    public void givenUserId_whenDeleteUser_thenNothing() throws UserNotFoundException {
        //given
        String userId = RandomStringUtils.random(10);
        given(userRepository.findUserByUserId(userId)).willReturn(Optional.ofNullable(user));
        willDoNothing().given(userRepository).delete(user);

        //when
        userManagementService.deleteUser(userId);

        //then
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("User management - delete user by admin. User not found. ")
    public void givenUserId_whenDeleteUserNotFound_thenThrowUserNotFoundException() throws UserNotFoundException {
        //given
        String userId = RandomStringUtils.random(10);
        given(userRepository.findUserByUserId(userId)).willReturn(Optional.empty());

        //when
        assertThrows(UserNotFoundException.class, () ->{
            userManagementService.deleteUser(userId);
        });

        //then
        verify(userRepository, never()).delete(any());
    }
}