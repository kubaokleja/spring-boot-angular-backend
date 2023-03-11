package com.kubaokleja.springbootangular.user;

import com.kubaokleja.springbootangular.common.dto.EmailDTO;
import com.kubaokleja.springbootangular.common.helper.CSVHelper;
import com.kubaokleja.springbootangular.email.EmailFacade;
import com.kubaokleja.springbootangular.exception.EmailExistsException;
import com.kubaokleja.springbootangular.exception.UserNotFoundException;
import com.kubaokleja.springbootangular.exception.UsernameExistsException;
import com.kubaokleja.springbootangular.user.enumeration.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kubaokleja.springbootangular.email.EmailConstant.NEW_ACCOUNT_SUBJECT;
import static com.kubaokleja.springbootangular.exception.constant.ExceptionConstant.*;
import static org.springframework.data.domain.PageRequest.of;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserManagementService.class);

    static String[] HEADERS = { "username","email","firstName","lastName"};
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final EmailFacade emailFacade;


    Page<UserDTO> getUsers(String keyword, int page, int size) {
        LOGGER.info("Fetching users for page {} of size {}", page, size);
        return userRepository.findUsersByFilterKeyword(keyword, of(page, size))
                .map(User::toDTO);
    }

    UserDTO createUser(UserDTO userDTO) throws EmailExistsException, UsernameExistsException {
        userValidator.validateUsernameAndEmail(userDTO.getUsername(), userDTO.getEmail());

        String password = RandomStringUtils.randomAlphabetic(10);
        String encodedPassword = passwordEncoder.encode(password);

        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER.name())
                .orElse(Role.builder()
                        .name(RoleEnum.ROLE_USER.name())
                        .build());

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
                .roles(List.of(userRole)) //In the future user will probably have many different roles
                .build();
        userRepository.save(user);
        LOGGER.info("User created: " + userDTO.getUsername());

        sendEmailWithPassword(userDTO, password);
        
        return userDTO;
    }
//TODO: Remove repetitive code to helper
    UserDTO updateUser(UserDTO userDTO) throws EmailExistsException, UserNotFoundException {

        UserDTO existingUser = userRepository.findUserByUserId(userDTO.getUserId())
                .map(User::toDTO)
                .orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND));

        if(!existingUser.getEmail().equalsIgnoreCase(userDTO.getEmail())) {
            userValidator.validateEmail(userDTO.getEmail());
        }

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setIsNotLocked(userDTO.getIsNotLocked());
        existingUser.setIsActive(userDTO.getIsActive());

        userRepository.save(existingUser.toEntity());
        LOGGER.info("User updated: " + existingUser.getUsername());
        return userDTO;
    }

    void deleteUser(String userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByUserId(userId);
        if(user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            throw new UserNotFoundException(NO_USER_FOUND);
        }
    }

    List<UploadUserResultDTO> uploadUserFromCSV(MultipartFile multipartFile) throws IOException{
        List<UserDTO> users = CSVHelper.mapCSVFileToRecordList(multipartFile.getInputStream(), HEADERS)
                .stream()
                    .map(csvRecord -> UserDTO.builder()
                            .username(csvRecord.get("username"))
                            .email(csvRecord.get("email"))
                            .firstName(csvRecord.get("firstName"))
                            .lastName(csvRecord.get("lastName"))
                            .build())
                    .collect(Collectors.toList());
        return validateUploadUser(users);
    }

    private void sendEmailWithPassword(UserDTO user, String password) {
        emailFacade.send(EmailDTO.builder()
                .receiver(user.getEmail())
                .subject(NEW_ACCOUNT_SUBJECT)
                .email(buildEmail(user.getFirstName(), password))
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .build());
    }

    private String buildEmail(String firstName, String password) {
        return "Hello " + firstName + "!\n\nYour new account password is: " + password + "\n\nThe Support Team";
    }

    private List<UploadUserResultDTO> validateUploadUser(List<UserDTO> users) {
        List<UploadUserResultDTO> resultList = new ArrayList<>();
        for(int count = 1 ; count <= users.size(); count++) {
            UserDTO userDTO = users.get(count - 1);
            StringBuilder validationResult = new StringBuilder();
            try {
                validationResult = userValidator.validateUserInputFromFile(userDTO);
                if(StringUtils.isEmpty(validationResult)) {
                    createUser(userDTO);
                }
            } catch (EmailExistsException e) {
                validationResult.append(EMAIL_ALREADY_EXISTS);
            } catch (UsernameExistsException e) {
                validationResult.append(USERNAME_ALREADY_EXISTS);
            }
            if(validationResult.length() > 0) {
                UploadUserResultDTO uploadUserResultDTO = UploadUserResultDTO
                        .builder()
                        .row(count)
                        .message(validationResult.toString())
                        .build();
                resultList.add(uploadUserResultDTO);
            }
        }
        return resultList;
    }
}
