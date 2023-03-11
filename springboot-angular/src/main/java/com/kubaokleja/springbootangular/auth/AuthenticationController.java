package com.kubaokleja.springbootangular.auth;

import com.kubaokleja.springbootangular.exception.UserNotFoundException;
import com.kubaokleja.springbootangular.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController("/auth")
@RequiredArgsConstructor
class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO) throws UserNotFoundException {
        final UserPrincipal loggedUserPrincipal = authenticationService.login(userDTO);
        HttpHeaders jwtHeader = authenticationService.getJwtHeader(loggedUserPrincipal);
        UserDTO loggedUserDTO = loggedUserPrincipal.getUser();
        return new ResponseEntity<>(loggedUserDTO, jwtHeader, OK);
    }
}
