package com.kubaokleja.springbootangular.user;

import lombok.Builder;
import lombok.Getter;

import java.util.Collection;

@Getter
@Builder
public class RoleDTO{
    private String name;
    private Collection<AuthorityDTO> authorities;
}
