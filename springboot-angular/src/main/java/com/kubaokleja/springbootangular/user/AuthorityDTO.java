package com.kubaokleja.springbootangular.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDTO {
    private Long id;
    private String name;
}
