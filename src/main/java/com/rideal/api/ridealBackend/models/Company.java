package com.rideal.api.ridealBackend.models;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.persistence.Transient;
import java.util.Collection;

@Document(collection = "companies")
public class Company extends User {
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_COMPANY,ROLE_USER");
    }
}
