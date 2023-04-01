package com.kubaokleja.springbootangular.email;

import com.kubaokleja.springbootangular.common.dto.EmailDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Profile("prod")
public class EmailServiceImpl implements EmailService{

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceDev.class);

    @Value("${admin.email}")
    private String adminEmail;

    private final MailSender mailSender;
    private final PendingEmailRepository pendingEmailRepository;

    @Scheduled(fixedDelayString = "PT1M")
    void emailSendRetryScheduled() {
        List<PendingEmail> pendingEmailList = pendingEmailRepository.findAll();
        pendingEmailList.forEach(email -> send(email.toDTO(email)));
    }

    @Override
    public void send(EmailDTO emailDTO) {
        Optional<PendingEmail> pendingEmail = pendingEmailRepository.findByReceiverAndSubject(emailDTO.getReceiver(), emailDTO.getSubject());
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(adminEmail);
            simpleMailMessage.setTo(emailDTO.getReceiver());
            simpleMailMessage.setSubject(emailDTO.getSubject());
            simpleMailMessage.setText(emailDTO.getEmail());
            mailSender.send(simpleMailMessage);
        } catch (MailSendException e) {
            if(pendingEmail.isEmpty()) {
                pendingEmailRepository.save(
                        mapToEmail(emailDTO));
            }
            LOGGER.error("Failed to send email", e);
        }
    }
}
