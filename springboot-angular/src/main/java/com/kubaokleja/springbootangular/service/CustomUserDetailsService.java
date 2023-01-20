package com.kubaokleja.springbootangular.service;

import com.kubaokleja.springbootangular.dto.UserDTO;
import com.kubaokleja.springbootangular.entity.User;
import com.kubaokleja.springbootangular.exception.EmailExistException;
import com.kubaokleja.springbootangular.exception.UserNotFoundException;
import com.kubaokleja.springbootangular.repository.UserRepository;
import com.kubaokleja.springbootangular.security.UserPrincipal;
import com.kubaokleja.springbootangular.validation.UserValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import static com.kubaokleja.springbootangular.constant.SecurityConstant.ACCESS_DENIED_MESSAGE;
import static com.kubaokleja.springbootangular.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;

@Service
@Transactional
@Qualifier("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final UserValidator userValidator;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, LoginAttemptService loginAttemptService, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.userValidator = userValidator;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = findUserByUsername(username);
        if (user == null) {
            LOGGER.error("User not found by username: " + username);
            throw new UsernameNotFoundException("User not found by username: " + username);
        }
        else {
            validateLoginAttempt(user);
            user.setLastLoginToDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("Returning found user by username: " + username);
            return userPrincipal;
        }

    }

    User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    User findUserByUserId(String userId) throws AccessDeniedException {
        User user = userRepository.findUserByUserId(userId);
        validateUserActionPermission(user, userId);
        return userRepository.findUserByUserId(userId);
    }

    User updateUser(String userId, UserDTO userDTO) throws EmailExistException {
        User user = userRepository.findUserByUserId(userId);
        validateUserActionPermission(user, userId);

        if(!user.getEmail().equalsIgnoreCase(userDTO.getEmail())) {
            userValidator.validateEmail(userDTO.getEmail());
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        return userRepository.save(user);
    }

    void deleteUser(String userId) {
        User user = userRepository.findUserByUserId(userId);
        validateUserActionPermission(user, userId);

        userRepository.delete(user);
    }

    private void validateUserActionPermission(User user, String userId) {
        User loggedUser = getLoggedUser().orElseThrow(() -> new AccessDeniedException(ACCESS_DENIED_MESSAGE));
        validateIfLoggedUserHasSameId(loggedUser, userId);
    }

    private Optional<User> getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || StringUtils.isEmpty(authentication.getName())) {
            Optional.empty();
        }
        return Optional.of(findUserByUsername(authentication.getName()));
    }

    private void validateIfLoggedUserHasSameId(User loggedUser, String userId) {
        if (!userId.equalsIgnoreCase(loggedUser.getUserId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    private void validateLoginAttempt(User user) {
        if(user.getIsNotLocked()){
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())){
                user.setIsNotLocked(false);
            }
            else{
                user.setIsNotLocked(true);
            }
        } else{
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

}
