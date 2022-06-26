package com.laioffer.staybooking.filter;

import com.laioffer.staybooking.repository.AuthorityRepository;
import com.laioffer.staybooking.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.laioffer.staybooking.model.Authority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Take care of filter function
 * 1. Hold URL/http request first
 * 2. If http request has token
 * 2.1 check whether token is valid
 * 2.2 get username from token
 * 3. get user's authority per username we get
 * 4. Save username and authority in current thread(each thread has SecurityContextHolder class to store info per Spring),
 * each user has their own thread
 * 5. Check whether user has the authority for this URL
 * Specific user has specific authority to specific URL(see setting in SecurityConfig's configure method)
 * 6. Continue other authorization(not in this class)
 * OncePerRequestFilter: once run the filter, do not do it again, (Client <-> Filter <-> Servlet: client's http request
 * pass the Filter, then no need to do filter again, when servlet response to client, no need to filter again)
 */
@Component
public class JwtFilter extends OncePerRequestFilter{

    // use to check whether http request has Authorization header
    private final String HEADER = "Authorization";

    // use to check whether http request's Authorization header begin with "Bearer"
    private final String PREFIX = "Bearer ";

    // use to get Authority per username
    private AuthorityRepository authorityRepository;

    // use to get info from token
    private JwtUtil jwtUtil;

    // use JwtUtil and AuthorityRepository do filter
    @Autowired
    public JwtFilter(AuthorityRepository authorityRepository, JwtUtil jwtUtil) {
        this.authorityRepository = authorityRepository;
        this.jwtUtil = jwtUtil;
    }

    //  FilterChain: use to call next filter, FilterChainProxy (see image below Spring Filter Recap in class document)
    //  HttpServletRequest: can get and parse info from http request
    //  HttpServletResponse: edit Http response
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // get "Authorization" header
        final String authorizationHeader = httpServletRequest.getHeader(HEADER);

        String jwt = null;
        // if "Authorization" header is null or not start with PREFIX, fail the Filter
        // Otherwise, get the token(jwt) as String
        if (authorizationHeader != null && authorizationHeader.startsWith(PREFIX)) {
            jwt = authorizationHeader.substring(PREFIX.length());
        }

        // validate the token once
        // 1. token string is not null
        // 2. token is valid by using method jwtUtil.validateToken
        // 3. Check token has been valid before(not to process token again for further request)
        //    If token has been validated, the user has authority to some URL already, no need to analyze token again
        //    SecurityContextHolder is under SecurityContextPersistenceFilter under FilterChainProxy (all validated token
        //    and other validated info will be saved under SecurityContextHolder)
        //    So we only need to validate token once (when user request to login)!!!
        //  Each user will have different SecurityContextHolder in different threads
        if (jwt != null && jwtUtil.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            // get username from token
            String username = jwtUtil.extractUsername(jwt);

            // get authority per username by the method from AuthorityRepository(from Authority Table from database)
            Authority authority = authorityRepository.findById(username).orElse(null);

            // next we need to save username and authorities to UsernamePasswordAuthenticationToken
            // then save UsernamePasswordAuthenticationToken to Authentication under SecurityContextHolder(which saved
            // info to thread for this user)
            if (authority != null) {
                // GrantedAuthority - Represents one of  authorities granted to an Authentication object.
                // Authentication - Represents the token for an authentication request or for an authenticated principal
                // once the request has been processed by the AuthenticationManager.authenticate(Authentication) method.
                //  authority.getAuthority() will extract the name of the authority (String - Role_Host or Role_Guest),
                //  then use SimpleGrantedAuthor to convert name of the authority to an array of GrantedAuthority (see configure under
                //  SecurityConfig to see which role has which authority)
                List<GrantedAuthority> grantedAuthorities = Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(authority.getAuthority())});
                // UsernamePasswordAuthenticationToken  used to save user's info
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, grantedAuthorities);

                // we need to setDetail of UsernamePasswordAuthenticationToken (put http request's detail data to
                // usernamePasswordAuthenticationToken, so current thread(SecurityContextHolder) will recognize the request
                // from the same user)
                // then, next time, as request has been filtered, no need to do filter again
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // Now we have processed our own filter, do not forget to let other filter (default by Spring) to do other filters
        // call filterChain and let filterChain to let next filter process httpServletRequest, httpServletResponse
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}


