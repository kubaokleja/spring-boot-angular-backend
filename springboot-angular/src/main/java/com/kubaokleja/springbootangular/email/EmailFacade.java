package com.kubaokleja.springbootangular.email;

import com.kubaokleja.springbootangular.common.dto.EmailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailFacade {

    private final EmailService emailService;
    private final EmailConfirmationTokenService emailConfirmationTokenService;

    public void send(EmailDTO emailDTO) {
        emailService.send(emailDTO);
    };

    public void saveConfirmationToken(EmailConfirmationDTO emailConfirmationToken) {
        emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);
    }

    public EmailConfirmationDTO getConfirmationToken(String token) {
        return emailConfirmationTokenService.getConfirmationToken(token);
    }
}
