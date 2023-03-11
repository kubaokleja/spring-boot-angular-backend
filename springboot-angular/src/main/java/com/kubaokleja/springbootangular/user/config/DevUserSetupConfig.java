package com.kubaokleja.springbootangular.user.config;

import com.kubaokleja.springbootangular.user.UserServiceFacade;
import com.kubaokleja.springbootangular.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static com.kubaokleja.springbootangular.auth.SecurityConstant.ADMIN;

@Component
@Profile("!prod")
@RequiredArgsConstructor
@Transactional
class DevUserSetupConfig implements UserSetupConfig {

    @Value("${admin.password}")
    private String adminPassword;

    private final UserServiceFacade userServiceFacade;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @PostConstruct
    public void setup() {
        UserDTO admin = userServiceFacade.findUserByUsername(ADMIN);
        if("temporary".equalsIgnoreCase(admin.getPassword())) {
            admin.setPassword(bCryptPasswordEncoder.encode(adminPassword));
            userServiceFacade.save(admin);
        }
    }
}
