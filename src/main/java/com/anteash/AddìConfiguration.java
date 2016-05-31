package com.anteash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Arrays;
import java.util.Collection;

@Slf4j
@Configuration
public class AddìConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${ldap.domain:dominio.prova}")
    private String ldapDomain;
    @Value("${ldap.url:ldap://192.168.10.13/}")
    public String ldapUrl;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.asList(adAuthenticationProvider()));
    }

    @Bean
    public AuthenticationProvider adAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider result =
                new ActiveDirectoryLdapAuthenticationProvider(ldapDomain, ldapUrl);
        result.setUserDetailsContextMapper(userDetailMapper());
        result.setConvertSubErrorCodesToExceptions(true);
        result.setUseAuthenticationRequestCredentials(true);
        result.setAuthoritiesMapper(authoritiesMapper());
        return result;
    }

    @Bean
    public LdapUserDetailsMapper userDetailMapper() {
        return new LdapUserDetailsMapper() {
            @Override
            public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                    Collection<? extends GrantedAuthority> authorities) {
                UserDetails result = super.mapUserFromContext(ctx, username, authorities);
                Object mail = ctx.getObjectAttribute("mail");
                if (null != mail) {
                    log.info("User {} has mail {}.", username, mail);
                }
                return result;
            }
        };
    }

    @Bean
    public SimpleAuthorityMapper authoritiesMapper() {
        SimpleAuthorityMapper result = new SimpleAuthorityMapper();
        result.setConvertToUpperCase(true);
        return result;
    }
}
