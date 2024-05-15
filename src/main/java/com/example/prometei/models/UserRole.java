package com.example.prometei.models;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    UNAUTHORIZED,
    AUTHORIZED,
    ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
