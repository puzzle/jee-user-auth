package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.PersistenceTestRunner;
import ch.puzzle.jee.userauth.security.entity.Token;
import ch.puzzle.jee.userauth.security.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(PersistenceTestRunner.class)
public class TokenRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    private TokenRepository repository;

    private User user;

    @Before
    public void init() {
        repository = new TokenRepository();
        repository.setEntityManager(entityManager);

        user = new User("Fritz", "Muster");
        entityManager.persist(user);
    }

    @Test
    public void shouldFindTokenByString() throws Exception {
        // given
        Token qwertz = new Token("qwertz", user);
        entityManager.persist(qwertz);

        // when
        Token result = repository.findByTokenString("qwertz");

        // then
        assertThat(result, is(qwertz));
    }
}