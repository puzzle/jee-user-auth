package ch.puzzle.jee.userauth.security.boundary;

import ch.puzzle.jee.userauth.context.boundary.UserAuthContextHolder;
import ch.puzzle.jee.userauth.context.entity.UserAuthContext;
import ch.puzzle.jee.userauth.security.control.AuthenticationService;
import ch.puzzle.jee.userauth.security.control.PermissionService;
import ch.puzzle.jee.userauth.security.control.TokenService;
import ch.puzzle.jee.userauth.security.entity.Credentials;
import ch.puzzle.jee.userauth.security.entity.PermissionName;
import ch.puzzle.jee.userauth.security.entity.Token;
import ch.puzzle.jee.userauth.security.entity.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static ch.puzzle.jee.userauth.security.entity.Action.DELETE;
import static ch.puzzle.jee.userauth.security.entity.Action.READ;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Stateless
@Produces(APPLICATION_JSON)
@Path("/authentication")
public class AuthenticationResource {

    @Inject
    AuthenticationService authenticationService;

    @Inject
    PermissionService permissionService;

    @Inject
    TokenService tokenService;

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @PermitAll
    public Response authenticateUser(Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        User user = authenticationService.authenticate(username, password);
        Token token = tokenService.createToken(user);

        // Collect the response elements
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("token", token.getTokenString());
        responseMap.put("permissions", permissionService.getPermissionsForUser(username));
        responseMap.put("user", user);

        return Response.ok(responseMap).build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Interceptors(PermissionInterceptor.class)
    @RequiresPermission(value = PermissionName.TOKEN, action = READ)
    public Response getListOfPermissions() {
        String username = UserAuthContextHolder.get().getUsername();
        User user = authenticationService.getAuthenticatedUser(username);

        if (user == null) {
            throw new NotAuthorizedException("User not found.");
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("permissions", permissionService.getPermissionsForUser(user.getLogin()));
        responseMap.put("user", user);
        return Response.ok(responseMap).build();
    }

    @DELETE
    @Interceptors(PermissionInterceptor.class)
    @RequiresPermission(value = PermissionName.TOKEN, action = DELETE)
    public Response logout() {
        UserAuthContext context = UserAuthContextHolder.get();
        tokenService.invalidateToken(context.getToken());
        return Response.ok().build();
    }
}
