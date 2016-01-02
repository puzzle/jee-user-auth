package ch.puzzle.jee.userauth;

import ch.puzzle.jee.userauth.validation.boundary.DummyPerson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(PersistenceTestRunner.class)
public class BaseRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    private ColourRepository repository;
    private DummyPerson blue, red;

    @Before
    public void setup() throws Exception {

        UserAuthContextHelper.setupContext();

        repository = new ColourRepository();
        repository.setEntityManager(entityManager);

        blue = new DummyPerson("Person", "1", "dummy@test.com");
        entityManager.persist(blue);
        red = new DummyPerson("Person", "2", "dummy@test.com");
        entityManager.persist(red);
    }

    @Test
    public void shouldFindEntity() throws Exception {
        // when
        DummyPerson result = repository.find(red.getId());

        // then
        assertThat(result, equalTo(red));
    }

    @Test
    public void shouldPersistEntity() throws Exception {
        // given
        DummyPerson black = new DummyPerson("Person", "3", "dummy@test.com");

        // when
        repository.persist(black);
        entityManager.flush();

        // then
        assertThat(black.getId(), is(notNullValue()));
        assertThat(entityManager.find(DummyPerson.class, black.getId()), equalTo(black));
    }

    @Test
    public void shouldMergeEntity() throws Exception {
        // given
        entityManager.flush();
        entityManager.detach(blue);

        // when
        blue.setName("Some name");
        repository.merge(blue);
        entityManager.flush();

        // then
        String query = "select p from DummyPerson p where p.name = 'Some name'";
        DummyPerson merged = entityManager.createQuery(query, DummyPerson.class).getSingleResult();
        assertThat(merged.getId(), equalTo(blue.getId()));
    }

    @Test
    public void shouldIncrementVersionValueOnMerge() throws Exception {
        // given
        entityManager.flush();
        entityManager.detach(blue);

        // when
        blue.setName("Other name");
        DummyPerson merged = repository.merge(blue);
        entityManager.flush();

        // then
        assertThat(merged.getVersion(), is(greaterThan(blue.getVersion())));
    }

    @Test
    public void shouldRemoveEntityByObject() throws Exception {
        // when
        repository.remove(blue);
        entityManager.flush();

        // then
        assertThat(entityManager.find(DummyPerson.class, blue.getId()), is(nullValue()));
    }

    @Test
    public void shouldRemoveEntityByIdAndReturnTrue() throws Exception {
        // when
        boolean found = repository.remove(blue.getId());
        entityManager.flush();

        // then
        assertThat(found, is(true));
        assertThat(entityManager.find(DummyPerson.class, blue.getId()), is(nullValue()));
    }

    @Test
    public void shouldReturnFalseIfEntityToRemoveWasNotFound() throws Exception {
        // when
        boolean found = repository.remove(Long.MAX_VALUE);
        entityManager.flush();

        // then
        assertThat(found, is(false));
    }

    @Test
    public void shouldReturnNullIfResultListIsEmpty() throws Exception {
        // given
        entityManager.remove(blue);
        entityManager.remove(red);

        // when
        DummyPerson colour = repository.singleResult(entityManager.createQuery("select p from DummyPerson p", DummyPerson.class));

        // then
        assertThat(colour, is(nullValue()));
    }

    @Test
    public void shouldFindAll() throws Exception {
        // when
        List<DummyPerson> result = repository.findAll();

        // then
        assertThat(result, containsInAnyOrder(blue, red));
    }

    private static class ColourRepository extends BaseRepository<DummyPerson> {
    }
}