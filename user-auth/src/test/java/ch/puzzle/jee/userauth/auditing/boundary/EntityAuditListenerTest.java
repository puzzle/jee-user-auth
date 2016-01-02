package ch.puzzle.jee.userauth.auditing.boundary;

import ch.puzzle.jee.userauth.PersistenceTestRunner;
import ch.puzzle.jee.userauth.UserAuthContextHelper;
import ch.puzzle.jee.userauth.context.boundary.UserAuthContextHolder;
import ch.puzzle.jee.userauth.validation.boundary.DummyPerson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(PersistenceTestRunner.class)
public class EntityAuditListenerTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void init() {
        UserAuthContextHelper.setupContext();
    }

    @Test
    public void shouldInjectAuditedValuesOnPersist() throws Exception {
        // given
        DummyPerson entity = new DummyPerson("Test", "Entity", "test@something.com");

        // when
        entityManager.persist(entity);

        // then
        assertThat(entity.getChangedBy(), is(notNullValue()));
        assertThat(entity.getChangedOn(), is(notNullValue()));
    }

    @Test
    public void shouldRefreshAuditedValuesOnUpdate() throws Exception {
        // given
        DummyPerson entity = new DummyPerson("Test", "Entity", "test@something.com");
        entityManager.persist(entity);

        String changedBy = entity.getChangedBy();
        LocalDateTime changedOn = entity.getChangedOn();
        Thread.sleep(10);

        // when
        entity.setName("Updated name");
        entityManager.flush();

        // then
        assertThat(entity.getChangedOn(), is(not(changedOn)));
        assertThat(entity.getChangedBy(), is(UserAuthContextHolder.get().getUsername()));
    }
}
