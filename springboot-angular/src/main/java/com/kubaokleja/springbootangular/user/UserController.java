package com.kubaokleja.springbootangular.user;

import com.kubaokleja.springbootangular.common.dto.HttpResponse;
import com.kubaokleja.springbootangular.exception.EmailExistsException;
import com.kubaokleja.springbootangular.exception.EmailNotFoundException;
import com.kubaokleja.springbootangular.exception.UserNotFoundException;
import com.kubaokleja.springbootangular.exception.UsernameExistsException;
import com.kubaokleja.springbootangular.exception.handler.ExceptionHandling;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController extends ExceptionHandling {

    private final UserService userService;

    @PostMapping("")
    ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO userRequest) throws UsernameExistsException, EmailExistsException {
        UserDTO userResponse = userService.create(userRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/confirm")
    ResponseEntity<String> confirmRegistrationToken(@RequestParam("token") String token) throws UserNotFoundException {
        return new ResponseEntity<>(userService.confirmToken(token), OK);
    }

    @GetMapping("/{userId}")
    ResponseEntity<UserDTO> find(@PathVariable("userId") String userId) throws UserNotFoundException {
        UserDTO userResponse = userService.find(userId);
        return new ResponseEntity<>(userResponse, OK);
    }

    @PutMapping("/{userId}")
    ResponseEntity<UserDTO> updateUser(@PathVariable("userId") String userId, @RequestBody UserDTO userRequest) throws EmailExistsException, UserNotFoundException {
        UserDTO userResponse = userService.update(userId, userRequest);
        return new ResponseEntity<>(userResponse, OK);
    }

    @DeleteMapping("/{userId}")
    ResponseEntity deleteUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        userService.delete(userId);
        return response(OK,"User has been deleted successfully");
    }

    @PostMapping("/reset-password/{email}")
    ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, "Password has been sent to email");
    }

    @PostMapping("/change-password/{password}")
    ResponseEntity<HttpResponse> changePassword(@PathVariable("password") String password) {
        userService.changePassword(password);
        return response(OK, "Password has been changed");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
}
