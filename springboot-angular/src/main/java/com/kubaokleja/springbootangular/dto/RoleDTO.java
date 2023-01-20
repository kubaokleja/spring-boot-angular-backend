package com.kubaokleja.springbootangular.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class RoleDTO{

    private String name;

    private Collection<AuthorityDTO> authorities;
}
