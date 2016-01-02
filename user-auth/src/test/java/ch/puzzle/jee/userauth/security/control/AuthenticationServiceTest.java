package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.security.entity.User;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotAuthorizedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    private AuthenticationService service;
    private User user;

    @Before
    public void init() {
        service = new AuthenticationService();
        service.userRepository = mock(UserRepository.class);

        user = new User("bill", "secret");
        when(service.userRepository.findByLogin("bill")).thenReturn(user);
    }

    @Test
    public void shouldAuthenticateUserWithCorrectCredentials() throws Exception {
        // when & then
        assertThat(service.authenticate("bill", "secret"), is(user));
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldNotAuthenticateWithNullUsername() throws Exception {
        // when
        service.authenticate(null, "secret");
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldNotAuthenticateWithEmptyUsername() throws Exception {
        // when
        service.authenticate("", "secret");
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldNotAuthenticateWithWrongUsername() throws Exception {
        // when
        service.authenticate("billy", "secret");
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldNotAuthenticateWithNullPassword() throws Exception {
        // when
        service.authenticate("bill", null);
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldNotAuthenticateWithEmptyPassword() throws Exception {
        // when
        service.authenticate("bill", "");
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldNotAuthenticateWithWrongPassword() throws Exception {
        // when
        service.authenticate("bill", "s3cret");
    }

    @Test
    public void shouldDelegateToRepository() throws Exception {
        // when
        service.getAuthenticatedUser("bill");

        // then
        verify(service.userRepository).findByLogin("bill");
    }
}