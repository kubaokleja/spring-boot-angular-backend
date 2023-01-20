package com.kubaokleja.springbootangular.service;

import com.kubaokleja.springbootangular.repository.UserRepository;
import com.kubaokleja.springbootangular.validation.UserValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LoginAttemptService loginAttemptService;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

}
