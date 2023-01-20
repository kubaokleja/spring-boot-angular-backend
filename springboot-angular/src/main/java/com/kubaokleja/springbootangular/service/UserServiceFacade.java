package com.kubaokleja.springbootangular.service;

import com.kubaokleja.springbootangular.dto.UserDTO;
import com.kubaokleja.springbootangular.entity.User;
import com.kubaokleja.springbootangular.exception.EmailExistException;
import com.kubaokleja.springbootangular.exception.UserNotFoundException;
import com.kubaokleja.springbootangular.exception.UsernameExistException;
import com.kubaokleja.springbootangular.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserServiceFacade {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceFacade.class);

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final UserManagementService userManagementService;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public UserServiceFacade(LoginService loginService, RegistrationService registrationService, UserManagementService userManagementService, CustomUserDetailsService userDetailsService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
        this.userManagementService = userManagementService;
        this.userDetailsService = userDetailsService;
    }

    public UserPrincipal login(UserDTO userDTO) {
        return loginService.login(userDTO);
    }

    public HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        return loginService.getJwtHeader(userPrincipal);
    }

    public User register(UserDTO user) throws UsernameExistException, EmailExistException {
        return registrationService.register(user);
    }

    public User getUser(String userId) {
        return userDetailsService.findUserByUserId(userId);
    }

    public User updateUser(String userId, UserDTO userDTO) throws EmailExistException {
        return userDetailsService.updateUser(userId, userDTO);
    }

    public void deleteUser(String userId) {
        userDetailsService.deleteUser(userId);
    }

    public Page<User> getUsers(String keyword, int page, int size) {
        return userManagementService.getUsers(keyword, page, size);
    }

    public User createUserByAdmin(UserDTO userDTO) throws EmailExistException, UsernameExistException {
        return userManagementService.createUser(userDTO);
    }

    public User updateUserByAdmin(UserDTO userDTO) throws EmailExistException, UserNotFoundException {
        return userManagementService.updateUser(userDTO);
    }

    public void deleteUserByAdmin(String userId) throws UserNotFoundException {
        userManagementService.deleteUser(userId);
    }

    public String confirmRegistrationToken(String token) {
        return registrationService.confirmToken(token);
    }
}
