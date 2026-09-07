package com.example.Booking.Security;

import com.example.Booking.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class Userdetails implements UserDetails {

    private User users;

    public Userdetails(User users) {
        this.users = users;
    }

    public Long getUserid(){
        return users.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Avoid using .stream() since getRoles() is a single Enum, not a List
        if (users.getRoles() == null) {
            return List.of();
        }

        return List.of(new SimpleGrantedAuthority("ROLE_" + users.getRoles().name()));
    }


    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }
}
