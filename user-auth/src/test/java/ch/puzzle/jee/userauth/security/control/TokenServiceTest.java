package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.security.entity.Token;
import ch.puzzle.jee.userauth.security.entity.User;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotAuthorizedException;
import java.time.LocalTime;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class TokenServiceTest {
    private TokenService service;

    @Before
    public void init() {
        service = new TokenService();
        service.repository = mock(TokenRepository.class);
    }

    @Test
    public void shouldCreateTokenForUser() throws Exception {
        // given
        User user = new User();

        // when
        Token token = service.createToken(user);

        // then
        verify(service.repository).persist(any(Token.class));
        assertThat(token.getUser(), is(user));
        assertThat(token.getTokenString().length(), is(32));
    }

    @Test
    public void shouldRefreshTokenIfValidated() throws Exception {
        // given
        Token token = mock(Token.class);
        when(token.getUser()).thenReturn(new User());
        when(token.getValidUntil()).thenReturn(LocalTime.now().plusSeconds(10));
        when(service.repository.findByTokenString(anyString())).thenReturn(token);

        // when
        Token result = service.validateAndRefreshToken("token-string");

        // then
        assertThat(token, is(result));
        verify(token).refreshValidUntil();
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldThrowExceptionIfTokenWasNotFound() throws Exception {
        // when
        service.validateAndRefreshToken("token-string");
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldThrowExceptionIfUserIsNull() throws Exception {
        // given
        Token tokenWithoutUser = mock(Token.class);
        when(tokenWithoutUser.getValidUntil()).thenReturn(LocalTime.now().plusSeconds(10));
        when(service.repository.findByTokenString(anyString())).thenReturn(tokenWithoutUser);

        // when
        service.validateAndRefreshToken("token-string");
    }

    @Test
    public void shouldInvalidateToken() throws Exception {
        // given
        Token token = new Token("qwertz", null);
        when(service.repository.findByTokenString("qwertz")).thenReturn(token);

        // when
        service.invalidateToken("qwertz");

        // then
        verify(service.repository).remove(token);
    }
}