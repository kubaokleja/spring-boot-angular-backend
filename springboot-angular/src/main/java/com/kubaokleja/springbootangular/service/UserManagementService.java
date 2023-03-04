package com.kubaokleja.springbootangular.service;

import com.kubaokleja.springbootangular.dto.CustomEmail;
import com.kubaokleja.springbootangular.dto.UserDTO;
import com.kubaokleja.springbootangular.entity.User;
import com.kubaokleja.springbootangular.exception.EmailExistException;
import com.kubaokleja.springbootangular.exception.UserNotFoundException;
import com.kubaokleja.springbootangular.exception.UsernameExistException;
import com.kubaokleja.springbootangular.repository.RoleRepository;
import com.kubaokleja.springbootangular.repository.UserRepository;
import com.kubaokleja.springbootangular.service.email.EmailSender;
import com.kubaokleja.springbootangular.utility.CSVHelper;
import com.kubaokleja.springbootangular.validation.UserValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.kubaokleja.springbootangular.constant.EmailConstant.NEW_ACCOUNT_SUBJECT;
import static com.kubaokleja.springbootangular.constant.UserImplConstant.*;
import static com.kubaokleja.springbootangular.enumeration.RoleEnum.ROLE_USER;
import static org.springframework.data.domain.PageRequest.of;

@Service
public class UserManagementService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserManagementService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final EmailSender emailSender;

    @Autowired
    public UserManagementService(UserRepository userRepository, RoleRepository roleRepository,
                                 BCryptPasswordEncoder passwordEncoder, UserValidator userValidator, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
        this.emailSender = emailSender;
    }

    Page<User> getUsers(String keyword, int page, int size) {
        LOGGER.info("Fetching users for page {} of size {}", page, size);
        return userRepository.findUsersByFilterKeyword(keyword, of(page, size));
    }

    User createUser(UserDTO userDTO) throws EmailExistException, UsernameExistException {
        userValidator.validateUsernameAndEmail(userDTO.getUsername(), userDTO.getEmail());

        String password = RandomStringUtils.randomAlphabetic(10);
        
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .userId(RandomStringUtils.randomNumeric(10))
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .joinDate(new Date())
                .password(encodedPassword)
                .isActive(true)
                .isNotLocked(true)
                .roles(List.of(roleRepository.findByName(ROLE_USER.name())))
                .build();
        user = userRepository.save(user);
        LOGGER.info("User created: " + user.getUsername());

        sendEmailWithPassword(user, password);
        
        return user;
    }

    User updateUser(UserDTO userDTO) throws EmailExistException, UserNotFoundException {

        User existingUser = userRepository.findUserByUsername(userDTO.getUsername());
        if(existingUser == null) {
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + userDTO.getUsername());
        }
        if(!existingUser.getEmail().equalsIgnoreCase(userDTO.getEmail())) {
            userValidator.validateEmail(userDTO.getEmail());
        }

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setIsNotLocked(userDTO.getIsNotLocked());
        existingUser.setIsActive(userDTO.getIsActive());

        User updatedUser = userRepository.save(existingUser);
        LOGGER.info("User updated: " + existingUser.getUsername());
        return updatedUser;
    }

    void deleteUser(String userId) throws UserNotFoundException {
        User user = userRepository.findUserByUserId(userId);
        if(user != null) {
            userRepository.delete(user);
        } else {
            throw new UserNotFoundException(NO_USER_FOUND);
        }
    }

    void uploadUserFromCSV(MultipartFile multipartFile) throws IOException{
        List<UserDTO> users = CSVHelper.csvToUserDTO(multipartFile.getInputStream());

        String validationResult;
        for(UserDTO userDTO: users) {
            try {
                validationResult = userValidator.validateUserInputFromFile(userDTO);
                if(StringUtils.isEmpty(validationResult)) {
                    createUser(userDTO);
                }
            } catch (EmailExistException e) {
                //validationResult
            } catch (UsernameExistException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendEmailWithPassword(User user, String password) {
        emailSender.send(new CustomEmail(
                user.getEmail(),
                NEW_ACCOUNT_SUBJECT,
                buildEmail(user.getFirstName(), password),
                MediaType.TEXT_PLAIN_VALUE));
    }


    private String buildEmail(String firstName, String password) {
        return "Hello " + firstName + "!\n\nYour new account password is: " + password + "\n\nThe Support Team";
    }

}
