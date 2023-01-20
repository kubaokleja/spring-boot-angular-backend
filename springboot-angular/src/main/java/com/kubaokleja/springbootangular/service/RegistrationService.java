package com.kubaokleja.springbootangular.service;

import com.kubaokleja.springbootangular.dto.CustomEmail;
import com.kubaokleja.springbootangular.dto.UserDTO;
import com.kubaokleja.springbootangular.entity.EmailConfirmation;
import com.kubaokleja.springbootangular.entity.User;
import com.kubaokleja.springbootangular.exception.EmailExistException;
import com.kubaokleja.springbootangular.exception.UsernameExistException;
import com.kubaokleja.springbootangular.repository.RoleRepository;
import com.kubaokleja.springbootangular.repository.UserRepository;
import com.kubaokleja.springbootangular.service.email.EmailSender;
import com.kubaokleja.springbootangular.validation.UserValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.kubaokleja.springbootangular.constant.EmailConstant.*;
import static com.kubaokleja.springbootangular.enumeration.RoleEnum.ROLE_USER;

@Service
public class RegistrationService {

    private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final EmailConfirmationTokenService emailConfirmationTokenService;
    private final EmailSender emailSender;

    @Value("${app.url}")
    private String url;

    @Autowired
    public RegistrationService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, UserValidator userValidator, EmailConfirmationTokenService emailConfirmationTokenService, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
        this.emailConfirmationTokenService = emailConfirmationTokenService;
        this.emailSender = emailSender;
    }

    @Transactional(rollbackFor = Exception.class)
    public User register(UserDTO userDTO) throws UsernameExistException, EmailExistException{
        userValidator.validateUsernameAndEmail(userDTO.getUsername(), userDTO.getEmail());

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        User user = User.builder()
                        .userId(RandomStringUtils.randomNumeric(10))
                        .firstName(userDTO.getFirstName())
                        .lastName(userDTO.getLastName())
                        .username(userDTO.getUsername())
                        .email(userDTO.getEmail())
                        .joinDate(new Date())
                        .password(encodedPassword)
                        .isActive(false)
                        .isNotLocked(true)
                        .roles(List.of(roleRepository.findByName(ROLE_USER.name())))
                        .build();

        user = userRepository.save(user);
        LOGGER.info("User created: " + user.getUsername());

        sendConfirmationEmail(user);

        return user;
    }

    @Transactional
    public String confirmToken(String token) {
        EmailConfirmation confirmationToken = emailConfirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException(TOKEN_NOT_FOUND));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException(EMAIL_ALREADY_CONFIRMED);
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(TOKEN_EXPIRED);
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        emailConfirmationTokenService.saveConfirmationToken(confirmationToken);
        activateUser(confirmationToken);

        LOGGER.info(USER_SUCCESSFULLY_CONFIRMED);
        return USER_SUCCESSFULLY_CONFIRMED;
    }

    private User sendConfirmationEmail(User user) {
        String token = saveEmailConfirmationToken(user);

        String link = url + "/register/confirm?token=" + token;
        emailSender.send(new CustomEmail(
                user.getEmail(),
                CONFIRM_EMAIL_SUBJECT,
                buildEmail(user.getFirstName(), link),
                MediaType.TEXT_HTML_VALUE));
        return user;
    }

    private String saveEmailConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();
        EmailConfirmation emailConfirmationToken = new EmailConfirmation(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        emailConfirmationTokenService.saveConfirmationToken(emailConfirmationToken);
        return token;
    }

    private void activateUser(EmailConfirmation confirmationToken) {
        User user = confirmationToken.getUser();
        user.setIsActive(Boolean.TRUE);
        userRepository.save(user);
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
