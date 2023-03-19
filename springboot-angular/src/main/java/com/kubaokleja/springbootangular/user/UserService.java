package com.kubaokleja.springbootangular.user;

import com.kubaokleja.springbootangular.common.dto.EmailDTO;
import com.kubaokleja.springbootangular.email.EmailConfirmationDTO;
import com.kubaokleja.springbootangular.email.EmailFacade;
import com.kubaokleja.springbootangular.exception.EmailExistsException;
import com.kubaokleja.springbootangular.exception.EmailNotFoundException;
import com.kubaokleja.springbootangular.exception.UserNotFoundException;
import com.kubaokleja.springbootangular.exception.UsernameExistsException;
import com.kubaokleja.springbootangular.user.enumeration.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kubaokleja.springbootangular.auth.SecurityConstant.ACCESS_DENIED_MESSAGE;
import static com.kubaokleja.springbootangular.email.EmailConstant.*;
import static com.kubaokleja.springbootangular.exception.constant.ExceptionConstant.*;

@Service
@RequiredArgsConstructor
class UserService {


    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final EmailFacade emailFacade;

    @Value("${app.url}")
    private String url;

    UserDTO create(UserDTO userDTO) throws UsernameExistsException, EmailExistsException {
        userValidator.validateUsernameAndEmail(userDTO.getUsername(), userDTO.getEmail());
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER.name())
                .orElse(Role.builder()
                    .name(RoleEnum.ROLE_USER.name())
                    .build());

        User user = User.builder()
                .userId(RandomStringUtils.randomNumeric(10))
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .joinDate(new Date())
                .password(encodedPassword)
                .isActive(false)
                .isNotLocked(true)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);

        UserDTO savedUserDTO = user.toDTO();
        sendConfirmationEmail(savedUserDTO);

        LOGGER.info("User created: " + savedUserDTO.getUsername());

        return savedUserDTO;
    }

    String confirmToken(String token) throws UserNotFoundException {
        EmailConfirmationDTO confirmationToken = emailFacade.getConfirmationToken(token);

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException(EMAIL_ALREADY_CONFIRMED);
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(TOKEN_EXPIRED);
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        emailFacade.saveConfirmationToken(confirmationToken);
        activateUser(confirmationToken);

        LOGGER.info(USER_SUCCESSFULLY_CONFIRMED);
        return USER_SUCCESSFULLY_CONFIRMED;
    }

    UserDTO find(String userId) throws AccessDeniedException, UserNotFoundException {
        UserDTO user = userRepository.findUserByUserId(userId)
                .map(User::toDTO)
                .orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND));
        validateUserActionPermission(user, userId);
        return user;
    }

    UserDTO update(String userId, UserDTO userDTO) throws EmailExistsException, UserNotFoundException {
        UserDTO user = userRepository.findUserByUserId(userId)
                .map(User::toDTO)
                .orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND));
        validateUserActionPermission(user, userId);

        if(!user.getEmail().equalsIgnoreCase(userDTO.getEmail())) {
            userValidator.validateEmail(userDTO.getEmail());
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        userRepository.save(user.toEntity());

        return user;
    }

    void delete(String userId) throws UserNotFoundException {
        UserDTO user = userRepository.findUserByUserId(userId)
                .map(User::toDTO)
                .orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND));
        validateUserActionPermission(user, userId);

        userRepository.delete(user.toEntity());
    }

    void resetPassword(String email) throws EmailNotFoundException {
        UserDTO user = userRepository.findByEmailNativeSQLNamedParam(email)
                .map(User::toDTO)
                .orElseThrow(() -> new EmailNotFoundException(NO_EMAIL_FOUND));
        String password = RandomStringUtils.randomAlphanumeric(10);
        emailFacade.send(EmailDTO.builder()
                .receiver(email)
                .subject(PASSWORD_RESET)
                .email(buildResetPasswordEmail(user.getFirstName(), password))
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .build());

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user.toEntity());
    }

    void changePassword(String password) {
        UserDTO user = getLoggedUser().orElseThrow(() -> new AccessDeniedException(ACCESS_DENIED_MESSAGE));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user.toEntity());
    }

    private Collection<Role> mapToRoles(Collection<RoleDTO> roles) {
        return roles.stream()
                .map(roleDTO -> Role.builder()
                        .name(roleDTO.getName())
                        .authorities(mapToAuthorities(roleDTO.getAuthorities()))
                        .build())
                .collect(Collectors.toSet());
    }

    private Collection<Authority> mapToAuthorities(Collection<AuthorityDTO> authorities) {
        return authorities.stream()
                .map(authorityDTO -> Authority.builder()
                        .name(authorityDTO.getName())
                        .build())
                .collect(Collectors.toSet());
    }

    private void sendConfirmationEmail(UserDTO user) {
        String token = saveEmailConfirmationToken(user);

        String link = url + "/register/confirm?token=" + token;
        emailFacade.send(EmailDTO.builder()
                .receiver(user.getEmail())
                .subject(CONFIRM_EMAIL_SUBJECT)
                .email(buildEmail(user.getFirstName(), link))
                .contentType(MediaType.TEXT_HTML_VALUE)
                .build());
    }

    private String saveEmailConfirmationToken(UserDTO user) {
        String token = UUID.randomUUID().toString();

        EmailConfirmationDTO emailConfirmationToken = EmailConfirmationDTO.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .userId(user.getUserId())
                .build();
        emailFacade.saveConfirmationToken(emailConfirmationToken);
        return token;
    }

    private void activateUser(EmailConfirmationDTO confirmationToken) throws UserNotFoundException {
        UserDTO user = userRepository.findUserByUserId(confirmationToken.getUserId())
                .map(User::toDTO)
                .orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND));
        user.setIsActive(Boolean.TRUE);
        userRepository.save(user.toEntity());
    }

    private void validateUserActionPermission(UserDTO user, String userId) {
        UserDTO loggedUser = getLoggedUser().orElseThrow(() -> new AccessDeniedException(ACCESS_DENIED_MESSAGE));
        validateIfLoggedUserHasSameId(loggedUser, userId);
    }

    private Optional<UserDTO> getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || StringUtils.isEmpty(authentication.getName())) {
            return Optional.empty();
        }
        return userRepository.findUserByUsername(authentication.getName()).map(User::toDTO);
    }

    private void validateIfLoggedUserHasSameId(UserDTO loggedUser, String userId) {
        if (!userId.equalsIgnoreCase(loggedUser.getUserId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    private String buildResetPasswordEmail(String firstName, String password) {
        return "Hello " + firstName + "!\n\nYour new account password is: " + password + "\n\nThe Support Team";
    }

    private String buildEmail(String name, String link) {
        return "Hello " + name + "!\n\nYour activation link is: " + link + "\n\nThe Support Team";
    }
}
