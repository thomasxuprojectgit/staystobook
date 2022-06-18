package com.laioffer.staybooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


/**
 * Use Spring security to do register and login
 * WebSecurityConfigurerAdapter set config for how to do the register and login(like which table store user...)
 * @EnableWebSecurity create objs for WebSecurity functions
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // use to encode password, we use MD5 in the twitch project
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // define what actions do not need login requirement
    // like. antMatchers(HttpMethod.POST, "/register/*").permitAll(), register does not need login first
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/register/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf()
                .disable();
    }

}
