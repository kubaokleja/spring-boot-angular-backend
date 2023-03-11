package com.kubaokleja.springbootangular.auth;

import com.kubaokleja.springbootangular.user.AuthorityDTO;
import com.kubaokleja.springbootangular.user.RoleDTO;
import com.kubaokleja.springbootangular.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class UserPrincipal implements UserDetails {

    private final UserDTO user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(RoleDTO::getAuthorities)
                .flatMap(Collection::stream)
                .map(AuthorityDTO::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getExpirationDate() == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getIsNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.getIsActive();
    }

    UserDTO getUser() {
        return user;
    }
}
