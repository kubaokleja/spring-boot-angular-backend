package com.kubaokleja.springbootangular.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.kubaokleja.springbootangular.common.annotation.Password;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@Data
@Builder
public class UserDTO{

    public static final String NUMBER_AND_LETTER_REGEX = "[A-Za-z0-9]+";

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String userId;
    @Pattern(regexp = NUMBER_AND_LETTER_REGEX, message = "Only letters and numbers are allowed")
    @NotBlank(message = "First name is mandatory")
    @Size(max = 255, message = "Max size is 255")
    private String firstName;
    @Pattern(regexp = NUMBER_AND_LETTER_REGEX, message = "Only letters and numbers are allowed")
    @NotBlank(message = "Last name is mandatory")
    @Size(max = 255, message = "Max size is 255")
    private String lastName;
    @Pattern(regexp = NUMBER_AND_LETTER_REGEX, message = "Only letters and numbers are allowed")
    @NotBlank(message = "Username is mandatory")
    @Size(min = 4, max = 20, message = "Valid username size is between 4 and 20")
    private String username;
    @Password
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;
    private Date lastLoginDate;
    private Date lastLoginToDisplay;
    private Date joinDate;
    private Boolean isActive;
    private Boolean isNotLocked;
    private LocalDateTime expirationDate;
    private Collection<RoleDTO> roles;

    public User toEntity() {
        return UserDTOMapper.toEntity(this);
    }
}

class UserDTOMapper {

    static User toEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .userId(userDTO.getUserId())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .lastLoginDate(userDTO.getLastLoginDate())
                .lastLoginToDisplay(userDTO.getLastLoginToDisplay())
                .joinDate(userDTO.getJoinDate())
                .isActive(userDTO.getIsActive())
                .isNotLocked(userDTO.getIsNotLocked())
                .roles(mapUserDTORolesToRoles(userDTO.getRoles()))
                .build();
    }

    private static Collection<Role> mapUserDTORolesToRoles(Collection<RoleDTO> roles) {
        List<Role> roleList = new ArrayList<>();
        roles
                .stream()
                .map(UserDTOMapper::mapUserRoleDTOToRole)
                .forEach(roleList::add);
        return roleList;
    }

    private static Role mapUserRoleDTOToRole(RoleDTO role) {
        return Role.builder()
                .name(role.getName())
                .authorities(mapUserRoleDTOToAuthorities(role.getAuthorities()))
                .build();
    }

    private static Collection<Authority> mapUserRoleDTOToAuthorities(Collection<AuthorityDTO> authorities) {
        List<Authority> authorityList = new ArrayList<>();
        authorities
                .stream()
                .map(UserDTOMapper::mapUserAuthorityDTOToAuthority)
                .forEach(authorityList::add);
        return authorityList;
    }

    private static Authority mapUserAuthorityDTOToAuthority(AuthorityDTO authority) {
        return Authority.builder()
                .name(authority.getName())
                .build();
    }
}