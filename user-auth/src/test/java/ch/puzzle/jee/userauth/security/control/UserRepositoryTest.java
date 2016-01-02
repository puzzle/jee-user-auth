package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.PersistenceTestRunner;
import ch.puzzle.jee.userauth.security.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(PersistenceTestRunner.class)
public class UserRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    private UserRepository repository;

    @Before
    public void init() {
        repository = new UserRepository();
        repository.setEntityManager(entityManager);
    }

    @Test
    public void shouldFindUserByLogin() throws Exception {
        // given
        User user = new User("gavin", "foo-bar");
        entityManager.persist(user);

        // when
        User result = repository.findByLogin("gavin");

        // then
        assertThat(result, is(user));
    }
}