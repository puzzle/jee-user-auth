package ch.puzzle.jee.userauth.security.boundary;

import ch.puzzle.jee.userauth.UserAuthContextHelper;
import ch.puzzle.jee.userauth.security.control.AuthenticationService;
import ch.puzzle.jee.userauth.security.control.PermissionService;
import ch.puzzle.jee.userauth.security.control.TokenService;
import ch.puzzle.jee.userauth.security.entity.Credentials;
import ch.puzzle.jee.userauth.security.entity.Token;
import ch.puzzle.jee.userauth.security.entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AuthenticationResourceTest {
    private AuthenticationResource resource;
    private User user;

    @Before
    public void init() {
        resource = new AuthenticationResource();
        resource.tokenService = mock(TokenService.class);
        resource.permissionService = mock(PermissionService.class);
        resource.authenticationService = mock(AuthenticationService.class);

        user = new User(UserAuthContextHelper.USERNAME, "pass");
        Set<String> permissions = new HashSet<>(asList("read", "write"));
        when(resource.authenticationService.authenticate(anyString(), anyString())).thenReturn(user);
        when(resource.authenticationService.getAuthenticatedUser(anyString())).thenReturn(user);
        when(resource.tokenService.createToken(user)).thenReturn(new Token("qwertz", user));
        when(resource.permissionService.getPermissionsForUser(UserAuthContextHelper.USERNAME)).thenReturn(permissions);

        UserAuthContextHelper.setupContext();
    }

    @After
    public void teardown() {
        UserAuthContextHelper.clearContext();
    }

    @Test
    public void shouldAuthenticateUser() throws Exception {
        // given
        Credentials credentials = new Credentials("user", "pass");

        // when
        Response response = resource.authenticateUser(credentials);

        // then
        assertThat(response.getStatus(), is(200));
        verify(resource.authenticationService).authenticate("user", "pass");
        verify(resource.tokenService).createToken(user);

        HashMap result = (HashMap) response.getEntity();
        assertThat(result.get("token"), is("qwertz"));
        assertThat(result.get("permissions"), is(not(nullValue())));
        assertThat(result.get("user"), is(not(nullValue())));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnAllPermissionsOfUser() throws Exception {
        // when
        Response response = resource.getListOfPermissions();
        HashMap result = (HashMap) response.getEntity();

        // then
        assertThat(response.getStatus(), is(200));
        Set<String> permissions = (Set<String>) result.get("permissions");
        assertThat(permissions, containsInAnyOrder("read", "write"));
        assertThat(result.get("user"), is(not(nullValue())));
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldThrowExceptionIfUserIsNotAuthenticated() throws Exception {
        // given
        when(resource.authenticationService.getAuthenticatedUser(anyString())).thenReturn(null);

        // when
        resource.getListOfPermissions();
    }

    @Test
    public void shouldInvalidateToken() throws Exception {
        // when
        Response response = resource.logout();

        // then
        assertThat(response.getStatus(), is(200));
        verify(resource.tokenService).invalidateToken(UserAuthContextHelper.TOKEN);
    }
}