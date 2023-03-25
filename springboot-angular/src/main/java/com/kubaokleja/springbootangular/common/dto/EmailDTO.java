package com.kubaokleja.springbootangular.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailDTO {

    String receiver;
    String subject;
    String email;
    String contentType;
}
