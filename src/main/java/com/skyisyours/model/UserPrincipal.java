package com.skyisyours.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements UserDetails {
    AppUser appUser = new AppUser();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        return appUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return appUser.getIsActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return appUser.getIsActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return appUser.getIsActive();
    }

    @Override
    public boolean isEnabled() {
        return appUser.getIsActive();
    }
}
