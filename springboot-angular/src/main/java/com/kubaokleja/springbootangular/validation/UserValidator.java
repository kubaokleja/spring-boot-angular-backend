package com.kubaokleja.springbootangular.validation;

import com.kubaokleja.springbootangular.entity.User;
import com.kubaokleja.springbootangular.exception.EmailExistException;
import com.kubaokleja.springbootangular.exception.UsernameExistException;
import com.kubaokleja.springbootangular.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.kubaokleja.springbootangular.constant.UserImplConstant.EMAIL_ALREADY_EXISTS;
import static com.kubaokleja.springbootangular.constant.UserImplConstant.USERNAME_ALREADY_EXISTS;

@Component
public class UserValidator {

    private final UserRepository userRepository;

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
}
