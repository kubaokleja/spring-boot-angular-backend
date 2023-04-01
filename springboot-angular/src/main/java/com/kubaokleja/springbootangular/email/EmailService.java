package com.kubaokleja.springbootangular.email;

import com.kubaokleja.springbootangular.common.dto.EmailDTO;

public interface EmailService {
    void send(EmailDTO emailDTO);

    default PendingEmail mapToEmail(EmailDTO emailDTO) {
        return PendingEmail.builder()
                .email(emailDTO.getEmail())
                .receiver(emailDTO.getReceiver())
                .contentType(emailDTO.getContentType())
                .subject(emailDTO.getSubject())
                .build();
    }
}
