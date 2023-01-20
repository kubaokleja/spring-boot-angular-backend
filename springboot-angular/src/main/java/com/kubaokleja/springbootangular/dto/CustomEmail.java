package com.kubaokleja.springbootangular.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomEmail {

    String to;
    String subject;
    String email;
    String contentType;
}
