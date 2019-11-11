package com.rideal.api.ridealBackend.models;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rideal-users")
@Data
public class User implements UserDetails, Serializable {
    public static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotNull
    @DBRef
    private City city;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Integer points;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank @Length(min=8, max=256)
    private String password;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return name + "#" + id.substring(0, 4);
    }

    /**
     * Checks if Account has not expired.
     * @return true.
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Checks if Account is not locked.
     * @return true.
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Checks if credentials are not expired.
     * @return true.
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Checks if Account is enabled.
     * @return true.
     */
    @Override

    public boolean isEnabled() { return true; }


    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void encodePassword() {
        this.password = passwordEncoder.encode(this.password);
    }
}