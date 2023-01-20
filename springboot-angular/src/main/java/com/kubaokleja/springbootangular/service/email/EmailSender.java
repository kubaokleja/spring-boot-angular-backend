package com.kubaokleja.springbootangular.service.email;

import com.kubaokleja.springbootangular.dto.CustomEmail;

public interface EmailSender {
    void send(CustomEmail customEmail);
}
