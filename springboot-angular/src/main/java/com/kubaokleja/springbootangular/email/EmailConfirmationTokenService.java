package com.kubaokleja.springbootangular.email;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kubaokleja.springbootangular.email.EmailConstant.TOKEN_NOT_FOUND;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class EmailConfirmationTokenService {

    private final EmailConfirmationTokenRepository confirmationTokenRepository;

    void saveConfirmationToken(EmailConfirmationDTO emailConfirmationDTO) {
        confirmationTokenRepository.save(
                EmailConfirmation.builder()
                        .id(emailConfirmationDTO.getId())
                        .token(emailConfirmationDTO.getToken())
                        .confirmedAt(emailConfirmationDTO.getConfirmedAt())
                        .createdAt(emailConfirmationDTO.getCreatedAt())
                        .expiresAt(emailConfirmationDTO.getExpiresAt())
                        .userId(emailConfirmationDTO.getUserId())
                        .build());
    }

    EmailConfirmationDTO getConfirmationToken(String token) {
       return confirmationTokenRepository.findByToken(token).
               map(e -> EmailConfirmationDTO.builder()
                       .id(e.getId())
                       .token(e.getToken())
                       .confirmedAt(e.getConfirmedAt())
                       .createdAt(e.getCreatedAt())
                       .expiresAt(e.getExpiresAt())
                       .userId(e.getUserId())
                       .build())
               .orElseThrow(() -> new IllegalStateException(TOKEN_NOT_FOUND));
    }
}
