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
    @Autowired
    private AddiConfiguration addìConfiguration;

    @PostConstruct
    public void doIt() {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(addìConfiguration.getLdapAdmin(),
                        addìConfiguration.getLdapAdminPassword()));
        if (auth.getPrincipal() instanceof LdapUserDetails) {
            log.info("Autenticato LDAP con ruoli: {}", auth.getAuthorities());
        }

        log.info("search in Administrators: {}", getAllPersonNames("Administrators", "Builtin"));
        log.info("search in Users:          {}", getAllPersonNames("Users", "Builtin"));
        log.info("search in Domain Users:   {}", getAllPersonNames("Domain Users", "Users"));
        log.info("search in DsnAdmins:      {}", getAllPersonNames("DnsAdmins", "Users"));
    }

    public List<String> getAllPersonNames(String group, String type) {
        LdapQueryBuilder query = query();
        ContainerCriteria criteria = query.where("objectclass").is("person");
        criteria.and("memberof").is("CN=" + group + ",CN=" + type + "," + addìConfiguration.getLdapDomainDC());
        return ldapTemplate.search(criteria,
                new AttributesMapper<String>() {
                    public String mapFromAttributes(Attributes attrs) throws NamingException {
                        return attrs.get("cn").get().toString();
                    }
                });
    }
}
