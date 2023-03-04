package com.kubaokleja.springbootangular.controller;

import com.kubaokleja.springbootangular.dto.HttpResponse;
import com.kubaokleja.springbootangular.dto.UserDTO;
import com.kubaokleja.springbootangular.entity.User;
import com.kubaokleja.springbootangular.exception.*;
import com.kubaokleja.springbootangular.mapper.UserMapper;
import com.kubaokleja.springbootangular.security.UserPrincipal;
import com.kubaokleja.springbootangular.service.UserServiceFacade;
import com.kubaokleja.springbootangular.utility.CSVHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user")
public class UserController extends ExceptionHandling {

    private final UserServiceFacade userService;

    @Autowired
    public UserController(UserServiceFacade userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO) {
        UserPrincipal loggedUserPrincipal = userService.login(userDTO);
        HttpHeaders jwtHeader = userService.getJwtHeader(loggedUserPrincipal);
        UserDTO loggedUserDTO = UserMapper.mapUserToUserDTO(loggedUserPrincipal.getUser());
        return new ResponseEntity<>(loggedUserDTO, jwtHeader, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO user) throws UsernameExistException, EmailExistException {
        User newUser = userService.register(user);
        UserDTO newUserDTO = UserMapper.mapUserToUserDTO(newUser);
        return new ResponseEntity<>(newUserDTO, HttpStatus.CREATED);
    }

    @GetMapping("/register/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return new ResponseEntity<>(userService.confirmRegistrationToken(token), OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("userId") String userId) {
        User userDetails = userService.getUser(userId);
        UserDTO userDetailsDTO = UserMapper.mapUserToUserDTO(userDetails);
        return new ResponseEntity<>(userDetailsDTO, OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("userId") String userId, @RequestBody UserDTO userDTO) throws EmailExistException {
        User updatedUser = userService.updateUser(userId, userDTO);
        UserDTO userDetailsDTO = UserMapper.mapUserToUserDTO(updatedUser);
        return new ResponseEntity<>(userDetailsDTO, OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return response(OK, "User deleted successfully");
    }

    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/list")
    public ResponseEntity<Page<UserDTO>> getUsers(@RequestParam Optional<String> keyword,
                                                  @RequestParam Optional<Integer> page,
                                                  @RequestParam Optional<Integer> size) {
        Page<UserDTO> users = userService.getUsers(keyword.orElse(""), page.orElse(0), size.orElse(10))
                .map(UserMapper::mapUserToUserDTO);
        return new ResponseEntity<>(users, OK);
    }

    @PreAuthorize("hasAuthority('user:create')")
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUserByAdmin(@Valid @RequestBody UserDTO userDTO) throws EmailExistException, UsernameExistException {
        User createdUser = userService.createUserByAdmin(userDTO);
        UserDTO newUserDTO = UserMapper.mapUserToUserDTO(createdUser);
        return new ResponseEntity<>(newUserDTO, OK);
    }

    @PreAuthorize("hasAuthority('user:update')")
    @PostMapping("/update")
    public ResponseEntity<UserDTO> updateUserByAdmin(@RequestBody UserDTO userDTO) throws EmailExistException, UserNotFoundException {
        User createdUser = userService.updateUserByAdmin(userDTO);
        UserDTO newUserDTO = UserMapper.mapUserToUserDTO(createdUser);
        return new ResponseEntity<>(newUserDTO, OK);
    }

    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<HttpResponse> deleteUserByAdmin(@PathVariable("userId") String userId) throws UserNotFoundException {
        userService.deleteUserByAdmin(userId);
        return response(OK, "User deleted successfully");
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, "Password has been sent to email");
    }

    @PostMapping("/change-password/{password}")
    public ResponseEntity<HttpResponse> changePassword(@PathVariable("password") String password) {
        userService.changePassword(password);
        return response(OK, "Password has been changed");
    }

    @PostMapping("/upload")
    public ResponseEntity<HttpResponse> uploadFiles(@RequestParam("file")MultipartFile multipartFile) throws IOException {
        if(multipartFile == null || !CSVHelper.hasCSVFormat(multipartFile)) {
            return response(HttpStatus.BAD_REQUEST, "Please upload a csv file!");
        }
        userService.uploadUsersFromCSV(multipartFile);
        return response(OK, "File uploaded: " + StringUtils.cleanPath(multipartFile.getOriginalFilename()));
    }

    @GetMapping
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }

}
