package com.project.greatcloud13.ClimbingWith.security;

import com.project.greatcloud13.ClimbingWith.dto.UserDetailDTO;
import com.project.greatcloud13.ClimbingWith.entity.Role;
import com.project.greatcloud13.ClimbingWith.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    @Getter
    private Long userId;

    private String username;
    private String password;
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public CustomUserDetails(Long userId, String username, String password, Role role){
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
