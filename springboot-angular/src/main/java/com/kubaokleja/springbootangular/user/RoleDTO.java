package com.kubaokleja.springbootangular.user;

import lombok.*;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO{
    private Long id;
    private String name;
    private Collection<AuthorityDTO> authorities;
}
