package com.kubaokleja.springbootangular.user;

import com.kubaokleja.springbootangular.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.kubaokleja.springbootangular.exception.constant.ExceptionConstant.NO_USER_FOUND;
import static com.kubaokleja.springbootangular.exception.constant.ExceptionConstant.NO_USER_FOUND_BY_USERNAME;

@Service
@RequiredArgsConstructor
public class UserServiceFacade {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceFacade.class);
    private final UserRepository userRepository;

    public UserDTO getUser(String userId) throws UserNotFoundException {
        return userRepository.findUserByUserId(userId)
                .map(User::toDTO)
                .orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND));
    }

    public UserDTO findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .map(User::toDTO)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
    }

    public UserDTO save(UserDTO userDTO) {
        return userRepository.save(userDTO.toEntity()).toDTO();
    }

}
