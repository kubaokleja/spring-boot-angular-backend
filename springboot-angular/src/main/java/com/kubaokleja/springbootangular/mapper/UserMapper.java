package com.kubaokleja.springbootangular.mapper;

import com.kubaokleja.springbootangular.dto.AuthorityDTO;
import com.kubaokleja.springbootangular.dto.RoleDTO;
import com.kubaokleja.springbootangular.dto.UserDTO;
import com.kubaokleja.springbootangular.entity.Authority;
import com.kubaokleja.springbootangular.entity.Role;
import com.kubaokleja.springbootangular.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserMapper {

    public static UserDTO mapUserToUserDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .lastLoginDate(user.getLastLoginDate())
                .lastLoginToDisplay(user.getLastLoginToDisplay())
                .joinDate(user.getJoinDate())
                .isActive(user.getIsActive())
                .isNotLocked(user.getIsNotLocked())
                .roles(mapUserRolesToRolesDTO(user.getRoles()))
                .build();
    }

    private static Collection<RoleDTO> mapUserRolesToRolesDTO(Collection<Role> roles) {
        List<RoleDTO> roleDTOList = new ArrayList<>();
        roles
            .stream()
            .map(role -> mapUserRoleToRoleDTO(role))
            .forEach(roleDTO -> roleDTOList.add(roleDTO));
        return roleDTOList;
    }

    private static RoleDTO mapUserRoleToRoleDTO(Role role) {
        return RoleDTO.builder()
                .name(role.getName())
                .authorities(mapUserRoleToAuthoritiesDTO(role.getAuthorities()))
                .build();
    }

    private static Collection<AuthorityDTO> mapUserRoleToAuthoritiesDTO(Collection<Authority> authorities) {
        List<AuthorityDTO> authorityDTOList = new ArrayList<>();
        authorities
            .stream()
            .map(authority -> mapUserAuthorityToAuthorityDTO(authority))
            .forEach(authorityDTO -> authorityDTOList.add(authorityDTO));
        return authorityDTOList;
    }

    private static AuthorityDTO mapUserAuthorityToAuthorityDTO(Authority authority) {
        return AuthorityDTO.builder()
                .name(authority.getName())
                .build();
    }

}
