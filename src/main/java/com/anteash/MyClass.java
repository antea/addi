package com.anteash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class MyClass {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostConstruct
    public void doIt() {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("prova", "Pr0vaPr0va1"));
        if (auth.getPrincipal() instanceof LdapUserDetails) {
            log.info("Autenticato LDAP con ruoli: {}", auth.getAuthorities());
        }
    }
}
