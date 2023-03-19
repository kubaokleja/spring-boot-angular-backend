package com.kubaokleja.springbootangular.auth;

import com.kubaokleja.springbootangular.user.UserServiceFacade;
import com.kubaokleja.springbootangular.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
class AuthenticationService {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserServiceFacade userServiceFacade;
    private final LoginAttemptService loginAttemptService;

    void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SecurityConstant.JWT_TOKEN_HEADER, tokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    UserPrincipal login(UserDTO userDTO) {
        authenticate(userDTO.getUsername(), userDTO.getPassword());
        UserDTO loggedUser = validateLoginAttempt(userDTO);
        LOGGER.info("User: " + loggedUser.getUsername() + "logged in successfully.");
        return new UserPrincipal(loggedUser);
    }

    private UserDTO validateLoginAttempt(UserDTO user) {
        UserDTO loggedUser = userServiceFacade.findUserByUsername(user.getUsername());
        if(loggedUser.getIsNotLocked()){
            if(loginAttemptService.hasExceededMaxAttempts(loggedUser.getUsername())) {
                loggedUser.setIsNotLocked(false);
            }
            else {
                loggedUser.setLastLoginToDisplay(loggedUser.getLastLoginDate());
                loggedUser.setLastLoginDate(new Date());
                loggedUser.setIsNotLocked(true);
            }
            userServiceFacade.save(loggedUser);
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(loggedUser.getUsername());
        }
        return loggedUser;
    }

}
