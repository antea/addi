package com.anteash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Slf4j
@Component
public class MyClass {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private LdapTemplate ldapTemplate;

    @PostConstruct
    public void doIt() {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("prova", "Pr0vaPr0va1"));
        if (auth.getPrincipal() instanceof LdapUserDetails) {
            log.info("Autenticato LDAP con ruoli: {}", auth.getAuthorities());
        }

        List<String> search = getAllPersonNames();
        log.info("search: {}", search);
    }

    public List<String> getAllPersonNames() {
        LdapQueryBuilder query = query();
        ContainerCriteria criteria = query.where("objectclass").is("person");
        criteria.and("memberof").is("CN=Administrators,CN=Builtin,DC=dominio,DC=prova");
        return ldapTemplate.search(criteria,
                new AttributesMapper<String>() {
                    public String mapFromAttributes(Attributes attrs) throws NamingException {
                        return attrs.get("cn").get().toString();
                    }
                });
    }
}
