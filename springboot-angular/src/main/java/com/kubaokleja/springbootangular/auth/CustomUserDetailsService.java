package com.kubaokleja.springbootangular.auth;

import com.kubaokleja.springbootangular.user.UserServiceFacade;
import com.kubaokleja.springbootangular.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Qualifier("customUserDetailsService")
@RequiredArgsConstructor
class CustomUserDetailsService implements UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final LoginAttemptService loginAttemptService;
    private final UserServiceFacade userServiceFacade;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDTO user = userServiceFacade.findUserByUsername(username);
        if (user == null) {
            LOGGER.error("User not found by username: " + username);
            throw new UsernameNotFoundException("User not found by username: " + username);
        }
        else {
            validateLoginAttempt(user);
            userServiceFacade.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("Returning found user by username: " + username);
            return userPrincipal;
        }
    }

    private void validateLoginAttempt(UserDTO user) {
        if(user.getIsNotLocked()){
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())){
                user.setIsNotLocked(false);
            }
            else{
                user.setLastLoginToDisplay(user.getLastLoginDate());
                user.setLastLoginDate(new Date());
                user.setIsNotLocked(true);
            }
        } else{
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

}
