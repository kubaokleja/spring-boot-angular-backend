package com.kubaokleja.springbootangular.service;

import com.kubaokleja.springbootangular.dto.UserDTO;
import com.kubaokleja.springbootangular.entity.User;
import com.kubaokleja.springbootangular.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationService authenticationService;

    @Autowired
    public LoginService(CustomUserDetailsService userDetailsService, AuthenticationService authenticationService) {
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
    }

    UserPrincipal login(UserDTO userDTO) {
        authenticationService.authenticate(userDTO.getUsername(), userDTO.getPassword());
        User loggedUser = userDetailsService.findUserByUsername(userDTO.getUsername());
        return new UserPrincipal(loggedUser);
    }

    public HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        return authenticationService.getJwtHeader(userPrincipal);
    }
}
