package com.kubaokleja.springbootangular.service;

import com.kubaokleja.springbootangular.entity.EmailConfirmation;
import com.kubaokleja.springbootangular.repository.EmailConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailConfirmationTokenService {

    private final EmailConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public EmailConfirmationTokenService(EmailConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public void saveConfirmationToken(EmailConfirmation emailConfirmation) {
        confirmationTokenRepository.save(emailConfirmation);
    }

    public Optional<EmailConfirmation> getToken(String token) {
       return confirmationTokenRepository.findByToken(token);
    }
}
