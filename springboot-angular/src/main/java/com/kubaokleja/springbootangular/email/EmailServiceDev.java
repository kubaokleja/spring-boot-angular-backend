package com.kubaokleja.springbootangular.email;

import com.kubaokleja.springbootangular.common.dto.EmailDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Profile("!prod")
class EmailServiceDev implements EmailService{

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceDev.class);

    @Value("${admin.email}")
    private String adminEmail;

    private final JavaMailSender mailSender;
    private final PendingEmailRepository pendingEmailRepository;

    @Scheduled(fixedDelayString = "PT1M")
    void emailSendRetryScheduled() {
        List<PendingEmail> pendingEmailList = pendingEmailRepository.findAll();
        pendingEmailList.forEach(email -> send(email.toDTO(email)));
    }

    public void send(EmailDTO emailDTO) {
        Optional<PendingEmail> pendingEmail = pendingEmailRepository.findByReceiverAndSubject(emailDTO.getReceiver(), emailDTO.getSubject());
        try {
            MimeMessage mimeMessage = createMessage(emailDTO);
            mailSender.send(mimeMessage);
            pendingEmail.ifPresent(pendingEmailRepository::delete);
        } catch (MessagingException | MailSendException e) {
            if(pendingEmail.isEmpty()) {
                pendingEmailRepository.save(
                        mapToEmail(emailDTO));
            }
            LOGGER.error("Failed to send email", e);
        }
    }

    private MimeMessage createMessage(EmailDTO emailDTO) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.setContent(emailDTO.getEmail(), emailDTO.getContentType());
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setTo(emailDTO.getReceiver());
        helper.setSubject(emailDTO.getSubject());
        helper.setFrom(adminEmail);
        return mimeMessage;
    }
}
