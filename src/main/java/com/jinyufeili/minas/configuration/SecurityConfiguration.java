package com.jinyufeili.minas.configuration;

import com.jinyufeili.minas.account.service.UserService;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * Created by pw on 6/10/16.
 */
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private Http401AuthenticationEntryPoint authEntryPoint;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private RememberMeServices rememberMeServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public Http401AuthenticationEntryPoint http401AuthenticationEntryPoint() {
        return new Http401AuthenticationEntryPoint("the realm");
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        tokenRepository.setJdbcTemplate(jdbcTemplate);
        return tokenRepository;
    }

    @Bean
    public RememberMeServices tokenBasedRememberMeServices(UserDetailsService userDetailsService,
                                                           SecurityConfig securityConfig,
                                                           PersistentTokenRepository persistentTokenRepository) {
        PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices(
                securityConfig.getKey(), userDetailsService, persistentTokenRepository);

        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setUseSecureCookie(securityConfig.isSecurityCookie());
        rememberMeServices.setCookieName(securityConfig.getCookieName());
        rememberMeServices.setTokenValiditySeconds(securityConfig.getTokenValiditySeconds());
        return rememberMeServices;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.rememberMe().key(securityConfig.getKey()).rememberMeServices(rememberMeServices);

        http
                .csrf().disable()
                .authorizeRequests().antMatchers("/api/wechat/oauth2-callback", "/api/wechat/js_signature",
                "/api/wechat/handler")
                .permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(authEntryPoint);
    }

    @Configuration
    @ConfigurationProperties(prefix = "security")
    public static class SecurityConfig {

        private String key;

        private int tokenValiditySeconds;

        private boolean securityCookie;

        private String cookieName;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getTokenValiditySeconds() {
            return tokenValiditySeconds;
        }

        public void setTokenValiditySeconds(int tokenValiditySeconds) {
            this.tokenValiditySeconds = tokenValiditySeconds;
        }

        public boolean isSecurityCookie() {
            return securityCookie;
        }

        public void setSecurityCookie(boolean securityCookie) {
            this.securityCookie = securityCookie;
        }

        public String getCookieName() {
            return cookieName;
        }

        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }
    }
}