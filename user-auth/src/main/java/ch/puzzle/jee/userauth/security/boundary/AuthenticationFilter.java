package ch.puzzle.jee.userauth.security.boundary;

import ch.puzzle.jee.userauth.context.boundary.UserAuthContextHolder;
import ch.puzzle.jee.userauth.context.entity.UserAuthContext;
import ch.puzzle.jee.userauth.security.control.TokenService;
import ch.puzzle.jee.userauth.security.entity.Token;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static javax.ws.rs.HttpMethod.OPTIONS;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String TOKEN_HEADER_PREFIX = "Bearer ";

    private TokenService tokenService;

    public AuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // ignore OPTIONS requests, there won't be any tokens set
        if (OPTIONS.equals(requestContext.getMethod())) {
            requestContext.abortWith(Response.ok().build());
            return;
        }

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        validateAuthorizationHeader(authorizationHeader);

        String tokenString = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length()).trim();
        Token token = tokenService.validateAndRefreshToken(tokenString);

        String login = token.getUser().getLogin();
        UserAuthContext context = new UserAuthContext(token.getUser(), login, token.getTokenString());
        UserAuthContextHolder.set(context);
    }

    private void validateAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }
    }
}