//// FILE: shared/security/JwtCustomerPrincipal.java
//package com.svrmslk.customer.shared.security;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import java.util.Collection;
//import java.util.Collections;
//
//public class JwtCustomerPrincipal implements UserDetails {
//
//    private final String customerId;
//    private final String email;
//    private final String type;
//    private final Collection<? extends GrantedAuthority> authorities;
//
//    public JwtCustomerPrincipal(String customerId, String email, String type) {
//        this.customerId = customerId;
//        this.email = email;
//        this.type = type;
//        this.authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + type));
//    }
//
//    public String getCustomerId() {
//        return customerId;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public boolean isCustomer() {
//        return "CUSTOMER".equals(type);
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        return null; // Not stored in this service
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}

// FILE: shared/security/JwtCustomerPrincipal.java
package com.svrmslk.customer.shared.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class JwtCustomerPrincipal {

    private final String authUserId; // JWT sub
    private final String email;
    private final List<String> roles;

    public JwtCustomerPrincipal(
            String authUserId,
            String email,
            List<String> roles
    ) {
        this.authUserId = authUserId;
        this.email = email;
        this.roles = roles;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}

