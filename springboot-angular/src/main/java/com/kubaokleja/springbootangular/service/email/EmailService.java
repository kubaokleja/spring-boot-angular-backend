package com.kubaokleja.springbootangular.service.email;

import com.kubaokleja.springbootangular.dto.CustomEmail;
import com.kubaokleja.springbootangular.service.EmailConfirmationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService implements EmailSender {

    public static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Value("${admin.email}")
    private String adminEmail;

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void send(CustomEmail customEmail) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            mimeMessage.setContent(customEmail.getEmail(), customEmail.getContentType());
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(customEmail.getTo());
            helper.setSubject(customEmail.getSubject());
            helper.setFrom(adminEmail);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email", e);
        }

    }
}
