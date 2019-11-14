package com.rideal.api.ridealBackend.models;

import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.persistence.Transient;
import java.util.Collection;

@NoArgsConstructor
@Document(collection = "companies")
public class Company extends User {

    @Builder(builderMethodName = "companyBuilder")
    public Company(String name, String password, String email, City city) {
        super();
        this.name = name;
        this.surname = name;
        this.password = password;
        this.city = city;
        this.email = email;
    }

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_COMPANY,ROLE_USER");
    }
}
