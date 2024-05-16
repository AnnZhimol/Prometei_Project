package com.example.prometei.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(nullable = false, unique = true)
    @Size(min = 5, max = 256)
    private String email;
    @Column(nullable = false)
    @Size(min = 10, max = 30)
    private String password;
    private String gender;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    @Size(min = 11)
    private String phoneNumber;
    private Date birthDate;
    private String passport;
    private String residenceCity;
    private String internationalPassportNum;
    private Date internationalPassportDate;
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Purchase> purchases;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Ticket> tickets;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ADMIN);
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
