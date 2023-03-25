package com.kubaokleja.springbootangular.user;

import com.kubaokleja.springbootangular.email.EmailConfirmationDTO;
import com.kubaokleja.springbootangular.email.EmailFacade;
import com.kubaokleja.springbootangular.exception.EmailExistsException;
import com.kubaokleja.springbootangular.exception.EmailNotFoundException;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.kubaokleja.springbootangular.email.EmailConstant.USER_SUCCESSFULLY_CONFIRMED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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
    @Mock
    private Authentication auth;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private Role role;
    private User user;

    @BeforeEach
    void setup() {
        userDTO = UserDTO.builder()
                .username("username")
                .password("password")
                .email("test@test.pl")
                .firstName("first_name")
                .lastName("last_name")
                .build();
        role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .authorities(new ArrayList<>())
                .build();
        user = User.builder()
                .userId(RandomStringUtils.randomNumeric(10))
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .joinDate(new Date())
                .isActive(false)
                .isNotLocked(true)
                .roles(List.of(role))
                .build();
    }

    @Test
    @DisplayName("User service test - create user (positive scenario)")
    void givenUserDTO_whenCreateUser_thenReturnSavedUser() throws UsernameExistsException, EmailExistsException {
        //given
        String encodedPassword = "encodedPassword";
        //when
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn(encodedPassword);
        when(roleRepository.findByName(any())).thenReturn(Optional.ofNullable(role));

        UserDTO createdUser = userService.create(userDTO);
        //then
        assertThat(createdUser.getUserId()).isNotNull();
        assertEquals(encodedPassword, createdUser.getPassword());
    }

    @Test
    @DisplayName("User service test - confirm token (Token already confirmed)")
    void givenToken_whenTokenConfirmed_thenThrowException() {
        //given
        String token = "token";
        EmailConfirmationDTO emailConfirmationToken = EmailConfirmationDTO.builder()
                .token(token).confirmedAt(LocalDateTime.now()).build();
        //when
        when(emailFacade.getConfirmationToken(token)).thenReturn(emailConfirmationToken);

        //then
        assertThrows(IllegalStateException.class, () -> userService.confirmToken(token));
    }

    @Test
    @DisplayName("User service test - confirm token (Token expired)")
    void givenToken_whenTokenExpired_thenThrowException() {
        //given
        String token = "token";
        EmailConfirmationDTO emailConfirmationToken = EmailConfirmationDTO.builder()
                .token(token).expiresAt(LocalDateTime.now().minusMinutes(15)).build();
        //when
        when(emailFacade.getConfirmationToken(token)).thenReturn(emailConfirmationToken);

        //then
        assertThrows(IllegalStateException.class, () -> userService.confirmToken(token));
    }

    @Test
    @DisplayName("User service test - confirm token (Token not found)")
    void givenToken_whenTokenNotFound_thenThrowException() throws UserNotFoundException {
        //given
        String token = "token";
        //when
        when(emailFacade.getConfirmationToken(token)).thenThrow(IllegalStateException.class);
        //then
        assertThrows(IllegalStateException.class, () -> userService.confirmToken(token));
    }

    @Test
    @DisplayName("User service test - confirm token (positive scenario)")
    void givenToken_whenTokenConfirmed_thenReturnSuccessfulMessage() throws UserNotFoundException {
        //given
        String token = "token";
        String userId = "123";
        EmailConfirmationDTO emailConfirmationToken = EmailConfirmationDTO.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .userId(userId)
                .build();
        //when
        when(emailFacade.getConfirmationToken(token)).thenReturn(emailConfirmationToken);
        when(userRepository.findUserByUserId(userId)).thenReturn(Optional.ofNullable(user));
        String result = userService.confirmToken(token);
        //then
        assertEquals(result, USER_SUCCESSFULLY_CONFIRMED);
    }

    @Test
    @DisplayName("User service test - find user")
    void givenUserId_whenFindUser_thenReturnUser() throws UserNotFoundException {
        //given
        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findUserByUserId(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findUserByUsername("username")).thenReturn(Optional.ofNullable(user));
        UserDTO foundUser = userService.find(user.getUserId());
        //then
        assertEquals(foundUser.getUserId(), user.getUserId());
    }

    @Test
    @DisplayName("User service test - user not found by userId")
    void givenUserId_whenUserNotFoundById_thenThrowUserNotFoundException() {
        //given
        //when
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.empty());
        //then
        assertThrows(UserNotFoundException.class, () -> userService.find(user.getUserId()));
    }

    @Test
    @DisplayName("User service test - find user (user not logged)")
    void givenUserId_whenFindUserAndUserNotLogged_thenThrowAccessDeniedException() throws UserNotFoundException {
        //given
        //when
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.ofNullable(user));
        //then
        assertThrows(AccessDeniedException.class, () -> userService.find(user.getUserId()));
    }

    @Test
    @DisplayName("User service test - find user (found user and logged user are different users)")
    void givenUserId_whenFindUserAndDifferentUserLogged_thenThrowAccessDeniedException() throws UserNotFoundException {
        //given
        User differentUser = User.builder()
                .userId(RandomStringUtils.randomNumeric(10))
                .firstName(userDTO.getFirstName() + "2")
                .lastName(userDTO.getLastName() + "2")
                .username(userDTO.getUsername() + "2")
                .email("newmail@mail.pl")
                .joinDate(new Date())
                .isActive(false)
                .isNotLocked(true)
                .roles(List.of(role))
                .build();
        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findUserByUserId(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findUserByUsername("username")).thenReturn(Optional.ofNullable(differentUser));
        //then
        assertThrows(AccessDeniedException.class, () -> userService.find(user.getUserId()));
    }

    @Test
    @DisplayName("User service test - update user")
    void givenUserIdAndUserDTO_whenUpdateUser_thenReturnUpdatedUser() throws UserNotFoundException, EmailExistsException {
        //given
        userDTO.setFirstName("Updated");
        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findUserByUserId(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findUserByUsername("username")).thenReturn(Optional.ofNullable(user));
        UserDTO updatedUser = userService.update(user.getUserId(), userDTO);
        //then
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("User service test - update user")
    void givenUserIdAndUserDTO_whenUpdateUserAndNewEmailTaken_thenThrow() throws UserNotFoundException, EmailExistsException {
        //given
        userDTO.setFirstName("Updated");
        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findUserByUserId(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findUserByUsername("username")).thenReturn(Optional.ofNullable(user));
        UserDTO updatedUser = userService.update(user.getUserId(), userDTO);
        //then
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("User service test - user update email already exists")
    public void givenUserObject_whenUpdateUserEmailExists_thenThrowEmailExistException() throws EmailExistsException {
        //given
        doThrow(EmailExistsException.class).when(userValidator).validateEmail(any());
        userDTO.setEmail("fake@mail.pl");
        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findUserByUserId(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findUserByUsername("username")).thenReturn(Optional.ofNullable(user));

        //then
        assertThrows(EmailExistsException.class, () ->{
            userService.update(user.getUserId(), userDTO);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User details - user delete")
    void givenExistingUser_whenUserExists_thenDeleteUser() throws UserNotFoundException {
        //given
        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findUserByUserId(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findUserByUsername("username")).thenReturn(Optional.ofNullable(user));

        userService.delete(user.getUserId());

        //then
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    @DisplayName("User service test - reset password - email does not exists")
    void givenFakeEmail_WhenUserNotFound_thanThrowEmailNotFoundException() {
        //given
        String email = "FakeEmail";
        //when
        when(userRepository.findByEmailNativeSQLNamedParam(email)).thenReturn(Optional.empty());
        assertThrows(EmailNotFoundException.class,
                () -> userService.resetPassword(email));
        //then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User service test - reset password")
    void givenEmail_WhenUserFound_thanResetPassword() throws EmailNotFoundException {
        //given
        //when
        when(userRepository.findByEmailNativeSQLNamedParam(user.getEmail())).thenReturn(Optional.of(user));
        userService.resetPassword(user.getEmail());
        //then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("User details - change password - positive scenario")
    void givenPassword_WhenChangePassword_ThenSaveUserWithNewPassword() {
        //given
        //when
        when(auth.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findUserByUsername("username")).thenReturn(Optional.ofNullable(user));

        userService.changePassword("password");
        //then
        verify(userRepository, times(1)).save(any(User.class));
    }
}