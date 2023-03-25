package com.kubaokleja.springbootangular.auth;

import com.kubaokleja.springbootangular.user.UserDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO) {
        final UserPrincipal loggedUserPrincipal = authenticationService.login(userDTO);
        HttpHeaders jwtHeader = authenticationService.getJwtHeader(loggedUserPrincipal);
        UserDTO loggedUserDTO = loggedUserPrincipal.getUser();
        return new ResponseEntity<>(loggedUserDTO, jwtHeader, OK);
    }
}
