package com.kubaokleja.springbootangular.validation;

import com.kubaokleja.springbootangular.dto.UserDTO;
import com.kubaokleja.springbootangular.entity.User;
import com.kubaokleja.springbootangular.exception.EmailExistException;
import com.kubaokleja.springbootangular.exception.UsernameExistException;
import com.kubaokleja.springbootangular.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kubaokleja.springbootangular.constant.UserImplConstant.EMAIL_ALREADY_EXISTS;
import static com.kubaokleja.springbootangular.constant.UserImplConstant.USERNAME_ALREADY_EXISTS;

@Component
public class UserValidator {

    private final UserRepository userRepository;
    private Pattern pattern = Pattern.compile("^(.+)@(.+)$");
    @Autowired
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUsernameAndEmail(String username, String email) throws UsernameExistException, EmailExistException {
        validateUsername(username);
        validateEmail(email);
    }

    public void validateEmail(String email) throws EmailExistException {
        User userByEmail = userRepository.findByEmailNativeSQLNamedParam(email);
        if (userByEmail != null) {
            throw new EmailExistException(EMAIL_ALREADY_EXISTS);
        }
    }

    public void validateUsername(String username) throws UsernameExistException {
        User userByUsername = userRepository.findUserByUsername(username);
        if (userByUsername != null) {
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
    }

    public String validateUserInputFromFile(UserDTO userDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        if(StringUtils.isEmpty(userDTO.getUsername())) {
            stringBuilder.append("Username empty. ");
        }
        else {
            //TODO: Size shouldn't be hardcoded
            if(StringUtils.isAlphanumeric(userDTO.getFirstName()) || userDTO.getUsername().length() > 20 || userDTO.getUsername().length() <8) {
                stringBuilder.append("Wrong username format. ");
            }
        }

        if(StringUtils.isEmpty(userDTO.getFirstName())) {
            stringBuilder.append("First name empty. ");
        }
        else {
            //TODO: Size shouldn't be hardcoded
            if(StringUtils.isAlphanumeric(userDTO.getFirstName()) || userDTO.getFirstName().length() > 255) {
                stringBuilder.append("Wrong first name format. ");
            }
        }

        if(StringUtils.isEmpty(userDTO.getLastName())) {
            stringBuilder.append("Last name empty. ");
        }
        else {
            //TODO: Size shouldn't be hardcoded
            if(StringUtils.isAlphanumeric(userDTO.getLastName()) || userDTO.getLastName().length() > 255) {
                stringBuilder.append("Wrong last name format. ");
            }
        }

        if(StringUtils.isEmpty(userDTO.getEmail())) {
            stringBuilder.append("Email empty. ");
        }
        else {
            Matcher matcher = pattern.matcher(userDTO.getEmail());
            if(!matcher.matches()) {
                stringBuilder.append("Email wrong pattern. ");
            }
        }

        return stringBuilder.toString();
    }
}
