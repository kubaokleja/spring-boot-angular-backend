package com.kubaokleja.springbootangular.user;

import com.kubaokleja.springbootangular.exception.EmailExistsException;
import com.kubaokleja.springbootangular.exception.UsernameExistsException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kubaokleja.springbootangular.exception.constant.ExceptionConstant.EMAIL_ALREADY_EXISTS;
import static com.kubaokleja.springbootangular.exception.constant.ExceptionConstant.USERNAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
class UserValidator {

    private final UserRepository userRepository;
    private final Pattern pattern = Pattern.compile("^(.+)@(.+)$");

    @Value("${validation.user.username.min-length}")
    private Integer usernameMinLength;

    @Value("${validation.user.username.max-length}")
    private Integer usernameMaxLength;

    @Value("${validation.user.name.max-length}")
    private Integer nameMaxLength;

    void validateUsernameAndEmail(String username, String email) throws UsernameExistsException, EmailExistsException {
        validateUsername(username);
        validateEmail(email);
    }

    void validateEmail(String email) throws EmailExistsException {
        Optional<User> userByEmailOptional = userRepository.findByEmailNativeSQLNamedParam(email);
        if (userByEmailOptional.isPresent()) {
            throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
        }
    }

    void validateUsername(String username) throws UsernameExistsException {
        Optional<User> userByUsernameOptional = userRepository.findUserByUsername(username);
        if (userByUsernameOptional.isPresent()) {
            throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
        }
    }

    StringBuilder validateUserInputFromFile(UserDTO userDTO) {
        StringBuilder validationResult = new StringBuilder();
        //Username
        if(StringUtils.isEmpty(userDTO.getUsername())) {
            validationResult.append("Username empty. ");
        }
        else {
            if(!StringUtils.isAlphanumeric(userDTO.getUsername()) ||
                    userDTO.getUsername().length() > usernameMaxLength ||
                    userDTO.getUsername().length() < usernameMinLength) {
                validationResult.append("Wrong username format. ");
            }
        }
        //First Name
        if(StringUtils.isEmpty(userDTO.getFirstName())) {
            validationResult.append("First name empty. ");
        }
        else {
            if(!StringUtils.isAlphanumeric(userDTO.getFirstName()) || userDTO.getFirstName().length() > nameMaxLength) {
                validationResult.append("Wrong first name format. ");
            }
        }
        //Last Name
        if(StringUtils.isEmpty(userDTO.getLastName())) {
            validationResult.append("Last name empty. ");
        }
        else {
            if(!StringUtils.isAlphanumeric(userDTO.getLastName()) || userDTO.getLastName().length() > nameMaxLength) {
                validationResult.append("Wrong last name format. ");
            }
        }
        //Email
        if(StringUtils.isEmpty(userDTO.getEmail())) {
            validationResult.append("Email empty. ");
        }
        else {
            Matcher matcher = pattern.matcher(userDTO.getEmail());
            if(!matcher.matches() && !StringUtils.isAlphanumeric(userDTO.getEmail())) {
                validationResult.append("Email wrong pattern. ");
            }
        }
        return validationResult;
    }
}
