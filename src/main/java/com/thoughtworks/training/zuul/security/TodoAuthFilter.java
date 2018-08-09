package com.thoughtworks.training.zuul.security;

import com.google.common.net.HttpHeaders;
import com.netflix.zuul.context.RequestContext;
import com.thoughtworks.training.zuul.model.User;
import com.thoughtworks.training.zuul.feign.UserFeign;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.thymeleaf.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.dsig.Manifest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

@Slf4j
@Component
public class TodoAuthFilter extends OncePerRequestFilter {
    @Autowired
    private UserFeign userFeign;

    @Value("${private.password}")
    private String privatePassword;
    private User user;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(token)) {
            Claims body = Jwts.parser()
                    .setSigningKey(privatePassword.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();


                int id = (int) body.get("id");
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(id,
                                null,
                                Collections.emptyList())
                );

            RequestContext.getCurrentContext().addZuulRequestHeader(
                    HttpHeaders.AUTHORIZATION,String.format("%s:%s",user.getId(),user.getName())
            );
        }

        filterChain.doFilter(request, response);
    }

//    private boolean validateUser(Claims body) {
//
//        User user = new User();
//        user.setName((String) body.get("name"));
//        user.setPassword((String) body.get("password"));
//        return userFeign.validateUser(user);
//    }

    private User validateUser(String token) throws UnsupportedEncodingException {
        Claims body = Jwts.parser()
                .setSigningKey(privatePassword.getBytes("UTF-8"))
                .parseClaimsJws(token)
                .getBody();


        int id = (int) body.get("id");
        String name = (String) body.get("name");
        User user = new User();
        user.setId(id);
        user.setName(name);

        return userFeign.validateUser(user);
    }

}
