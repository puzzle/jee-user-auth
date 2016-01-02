package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.security.entity.Token;
import ch.puzzle.jee.userauth.security.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.Random;

public class TokenService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenService.class);
    private static final String TOKEN_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";

    // use Random as member and to ensure randomness of nextInt()
    // SecureRandom is thread safe so it's fine to keep as member
    private final Random random;

    @Inject
    TokenRepository repository;

    public TokenService() {
        random = new SecureRandom();
    }

    public Token createToken(User user) {
        Token token = new Token(generateRandomString(32), user);
        repository.persist(token);
        LOG.debug("Created new {} for {}", token, user);
        return token;
    }

    /**
     * Throws a NotAuthorizedException if the token with the provided string was not found in the database or is
     * expired.
     *
     * @param tokenString token to validate.
     */
    public Token validateAndRefreshToken(String tokenString) {
        Token token = repository.findByTokenString(tokenString);
        if (token == null || token.getValidUntil() == null || token.getValidUntil().isBefore(LocalTime.now())) {
            throw new NotAuthorizedException("Token has expired");
        }
        if (token.getUser() == null) {
            throw new NotAuthorizedException("User on token not set");
        }
        token.refreshValidUntil();
        return token;
    }

    public void invalidateToken(String token) {
        Token entity = repository.findByTokenString(token);
        repository.remove(entity);
        LOG.debug("Invalidated {}", entity);
    }

    protected String generateRandomString(int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(TOKEN_CHARACTERS.charAt(random.nextInt(TOKEN_CHARACTERS.length())));
        }
        return s.toString();
    }
}
