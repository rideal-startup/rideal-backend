package com.rideal.api.ridealBackend.configuration;

import com.rideal.api.ridealBackend.components.RestAuthenticationEntryPoint;
import com.rideal.api.ridealBackend.handler.SuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final static Logger LOGGER = Logger.getLogger(SecurityConfiguration.class.getName());

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private SuccessHandler successHandler;

    private SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    @Value("${spring.security.user.password}")
    private String adminPass;

    @Value("${rideal.passwords.db_manager}")
    private String dbManagerPass;

    @Value("${rideal.passwords.developer}")
    private String developerPass;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("admin").password(encoder().encode(adminPass)).roles("ADMIN")
            .and()
            .withUser("db_manager").password(encoder().encode(dbManagerPass)).roles("DB_MANAGER")
            .and()
            .withUser("developer").password(encoder().encode(developerPass)).roles("DEVELOPER");
        LOGGER.info("Profiles created:\n" +
                "\t- ADMIN: username: admin, password: " + adminPass +"\n" +
                "\t- DB_MANAGER: username: db_manager, password: " + dbManagerPass + "\n" +
                "\t- DEVELOPER: username: developer, password: " + developerPass);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/**")
                .permitAll();
    }
}