package com.kubaokleja.springbootangular.user;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Builder
class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String userId; // id for frontend exposure
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    private Date lastLoginDate;
    private Date lastLoginToDisplay; //the previous login
    private Date joinDate;
    private Boolean isActive;
    private Boolean isNotLocked;
    private LocalDateTime expirationDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public UserDTO toDTO() {
        return UserMapper.toDTO(this);
    }
}

class UserMapper {

    static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
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
                .expirationDate(user.getExpirationDate())
                .roles(mapUserRolesToRolesDTO(user.getRoles()))
                .build();
    }

    private static Collection<RoleDTO> mapUserRolesToRolesDTO(Collection<Role> roles) {
        List<RoleDTO> roleDTOList = new ArrayList<>();
        roles
                .stream()
                .map(UserMapper::mapUserRoleToRoleDTO)
                .forEach(roleDTOList::add);
        return roleDTOList;
    }

    private static RoleDTO mapUserRoleToRoleDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .authorities(mapUserRoleToAuthoritiesDTO(role.getAuthorities()))
                .build();
    }

    private static Collection<AuthorityDTO> mapUserRoleToAuthoritiesDTO(Collection<Authority> authorities) {
        List<AuthorityDTO> authorityDTOList = new ArrayList<>();
        authorities
                .stream()
                .map(UserMapper::mapUserAuthorityToAuthorityDTO)
                .forEach(authorityDTOList::add);
        return authorityDTOList;
    }

    private static AuthorityDTO mapUserAuthorityToAuthorityDTO(Authority authority) {
        return AuthorityDTO.builder()
                .id(authority.getId())
                .name(authority.getName())
                .build();
    }

}
