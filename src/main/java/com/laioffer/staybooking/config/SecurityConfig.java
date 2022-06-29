package com.laioffer.staybooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import javax.sql.DataSource;

import org.springframework.security.authentication.AuthenticationManager;

import com.laioffer.staybooking.filter.JwtFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Use Spring security to do register and login
 * WebSecurityConfigurerAdapter set config for how to do the register and login(like which table store user...)
 * @EnableWebSecurity create objs (AuthenticationManager) for WebSecurity functions per the config methods in this class
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Spring will auto generate datasource per config in application.properties
    // use to connect with database
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JwtFilter jwtFilter;

    // use to encode password, we use MD5 in the twitch project
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // config method, tell Spring what to do, Spring need these config set itself
    // we won't call this method
    // define what actions do not need login requirement
    // check whether it has authority to do some actions for this user type
    // like. antMatchers(HttpMethod.POST, "/register/*").permitAll(), register does not need login first
    //                 .antMatchers("/stays").hasAuthority("ROLE_HOST") means this URL need host role
    //                .antMatchers("/stays/*").hasAuthority("ROLE_HOST") means this URL need host role
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/register/*").permitAll()
                .antMatchers(HttpMethod.POST, "/authenticate/*").permitAll()
                .antMatchers("/stays").hasAuthority("ROLE_HOST")
                .antMatchers("/stays/*").hasAuthority("ROLE_HOST")
                .antMatchers("/search").hasAuthority("ROLE_GUEST")
                .antMatchers("/reservations").hasAuthority("ROLE_GUEST")
                .antMatchers("/reservations/*").hasAuthority("ROLE_GUEST")
                .anyRequest().authenticated()
                .and()
                .csrf()
                .disable();

        // STATELESS: does not use session based, instead, use token based
        // addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class): use our filter (jwtFilter) before default filter
        // UsernamePasswordAuthenticationFilter.class will be skipped
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }

    // config method, tell Spring what to do, Spring need these config set itself
    // we won't call this method
    // check whether user can log in
    // authenticate http request -> username & password
    // find username and password from database
    // then check user's info match with the info in database
    // 使用JDBC直接调用SQL语句，没有使用ORM
    // use AuthenticationManagerBuilder check username and password
    // .jdbcAuthentication() use to check username and password in database
    // .usersByUsernameQuery and .authoritiesByUsernameQuery will do the auto match,
    // we do not need to manually write code (like if User.username == providedUserName)
    // .authoritiesByUsernameQuery will get authority type
    // "username, password, enabled" SQL must match with fields in User class
    // .passwordEncoder will encode password from http request, then do match compare
    // @Override WebSecurityConfigurerAdapter's  AuthenticationManagerBuilder method to config an Authentication Manager
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("SELECT username, password, enabled FROM user WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT username, authority FROM authority WHERE username = ?");
    }

    // caller will use AuthenticationManager obj's authenticate method to check user name password
    // we need to call this method to do check username and password
    // the configure methods above are just config, we will not directly call them
    // but config is important as AuthenticationManager need the config info to do check
    // @Bean creates an AuthenticationManager
    // @Override WebSecurityConfigurerAdapter's authenticationManagerBean() method to creates an AuthenticationManager
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
