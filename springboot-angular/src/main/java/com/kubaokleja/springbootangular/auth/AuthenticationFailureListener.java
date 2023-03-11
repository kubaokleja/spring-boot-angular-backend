package com.kubaokleja.springbootangular.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AuthenticationFailureListener {

    private final LoginAttemptService loginAttemptService;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        final Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String){
            String username = (String) principal;
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
