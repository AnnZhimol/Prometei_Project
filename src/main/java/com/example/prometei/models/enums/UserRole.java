package com.example.prometei.models.enums;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_CLIENT,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
