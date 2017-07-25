package com.steeplesoft.deskdroid.service;

import java.io.IOException;
import java.security.Key;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

/**
 * Created by jason on 4/27/2017.
 */
@Provider
@Secure
@Priority(Priorities.AUTHENTICATION)
public class SecureFilter implements ContainerRequestFilter {
    private DeskDroidService deskDroidService;

    public SecureFilter(DeskDroidService deskDroidService) {
        this.deskDroidService = deskDroidService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            String token = authorizationHeader.substring("Bearer".length()).trim();
            final Key key = KeyGenerator.getKey(deskDroidService.getApplicationContext());
            final JwtParser jwtParser = Jwts.parser().setSigningKey(key);
            jwtParser.parseClaimsJws(token);
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
