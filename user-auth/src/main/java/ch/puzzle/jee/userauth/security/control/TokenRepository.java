package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.BaseRepository;
import ch.puzzle.jee.userauth.security.entity.Token;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenRepository extends BaseRepository<Token> {

    public Token findByTokenString(String tokenString) {
        return singleResult(createNamedQuery("Token.findByTokenString").setParameter("token", tokenString));
    }
}
