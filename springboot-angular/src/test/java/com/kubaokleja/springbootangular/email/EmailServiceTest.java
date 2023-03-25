package com.kubaokleja.springbootangular.email;

import com.kubaokleja.springbootangular.common.dto.EmailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private PendingEmailRepository pendingEmailRepository;

    @InjectMocks
    private EmailService emailService;

    private EmailDTO emailDTO;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(emailService, "adminEmail", "test@test.pl");
        mimeMessage = new MimeMessage((Session)null);
        emailDTO = EmailDTO.builder()
                .receiver("test@test.pl")
                .subject("test")
                .email("test")
                .contentType("whatever")
                .build();
    }

    @Test
    @DisplayName("Email service test - positive scenario")
    void givenEmail_whenSendEmailCorrectly_ThenDoNotSavePendingEmail() {
        //given
        //when
        when(pendingEmailRepository.findByReceiverAndSubject(emailDTO.getReceiver(), emailDTO.getSubject()))
                .thenReturn(Optional.empty());
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService.send(emailDTO);
        //then
        verify(pendingEmailRepository, never()).save(any(PendingEmail.class));
    }

    @Test
    @DisplayName("Email service test - remove pending email")
    void givenEmail_whenPendingEmailNotEmpty_ThenDeleteAfterSavingEmail() {
        //given
        PendingEmail pendingEmail = PendingEmail.builder()
                .email(emailDTO.getEmail())
                .receiver(emailDTO.getReceiver())
                .contentType(emailDTO.getContentType())
                .subject(emailDTO.getSubject())
                .build();
        //when
        when(pendingEmailRepository.findByReceiverAndSubject(emailDTO.getReceiver(), emailDTO.getSubject()))
                .thenReturn(Optional.of(pendingEmail));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService.send(emailDTO);
        //then
        verify(pendingEmailRepository, times(1)).delete(pendingEmail);
    }

    @Test
    @DisplayName("Email service test - do nothing when exception is thrown and pending email exists")
    void givenEmailAndPendingEmail_whenMailSenderNotWorkingAndPendingEmailExists_ThenDoNothing() {
        //given
        PendingEmail pendingEmail = PendingEmail.builder()
                .email(emailDTO.getEmail())
                .receiver(emailDTO.getReceiver())
                .contentType(emailDTO.getContentType())
                .subject(emailDTO.getSubject())
                .build();
        //when
        when(pendingEmailRepository.findByReceiverAndSubject(emailDTO.getReceiver(), emailDTO.getSubject()))
                .thenReturn(Optional.of(pendingEmail));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(MailSendException.class).when(mailSender).send(mimeMessage);
        emailService.send(emailDTO);
        //then
        verify(pendingEmailRepository, never()).save(any());
    }

    @Test
    @DisplayName("Email service test - save pending email when exception is thrown and pending email does not exist")
    void givenEmail_whenMailSenderNotWorking_ThenSavePendingEmail() {
        //given
        //when
        when(pendingEmailRepository.findByReceiverAndSubject(emailDTO.getReceiver(), emailDTO.getSubject()))
                .thenReturn(Optional.empty());
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(MailSendException.class).when(mailSender).send(mimeMessage);
        emailService.send(emailDTO);
        //then
        verify(pendingEmailRepository, times(1)).save(any());
    }
}