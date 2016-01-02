package ch.puzzle.jee.userauth;

import ch.puzzle.jee.userauth.testdata.UriInfoBuilder;
import ch.puzzle.jee.userauth.validation.boundary.DummyPerson;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class EntityResourceTest {

    private BaseRepository<DummyPerson> repository;
    private ColourResource resource;
    private DummyPerson dummyPerson;

    @Before
    public void init() {
        resource = new ColourResource();
        repository = mock(BaseRepository.class);
        dummyPerson = new DummyPerson("Dummy", "Person", "test@something.com");
    }

    @Test
    public void shouldFindAll() throws Exception {
        // when
        Response response = resource.findAll();

        // then
        verify(repository).findAll();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void shouldFindById() throws Exception {
        // given
        when(repository.find(42l)).thenReturn(dummyPerson);

        // when
        Response response = resource.findById(42l);

        // then
        verify(repository).find(42l);
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void shouldReturn404IfNotFoundById() throws Exception {
        // when
        Response response = resource.findById(42l);

        // then
        assertThat(response.getStatus(), is(404));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionIfVersionIsNullOnMerge() throws Exception {
        // when
        dummyPerson.setId(42l);
        dummyPerson.setVersion(null);
        resource.merge(42l, dummyPerson);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionIfIdIsNullOnMerge() throws Exception {
        // when
        dummyPerson.setId(null);
        dummyPerson.setVersion(1l);
        resource.merge(42l, dummyPerson);
    }


    @Test
    public void shouldPersistEntity() throws Exception {
        // when
        Response response = resource.persist(new UriInfoBuilder().path("/path").build(), dummyPerson);

        // then
        verify(repository).persist(dummyPerson);
        assertThat(response.getStatus(), is(201));
        assertThat(response.getHeaderString("Location"), is("/path"));

        Object entity = response.getEntity();
        assertThat(entity, is(notNullValue()));
        assertThat(entity, is(instanceOf(DummyPerson.class)));
    }

    @Test
    public void shouldMergeEntity() throws Exception {
        // when
        dummyPerson.setId(42l);
        dummyPerson.setVersion(1l);
        Response response = resource.merge(42l, dummyPerson);

        // then
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void shouldRemoveEntity() throws Exception {
        // given
        when(repository.remove(42l)).thenReturn(true);

        // when
        Response response = resource.remove(42l);

        // then
        verify(repository).remove(42l);
        assertThat(response.getStatus(), is(204));
    }

    @Test
    public void shouldReturn404WhenEntityToRemoveWasNotFound() throws Exception {
        // given
        when(repository.remove(42l)).thenReturn(false);

        // when
        Response response = resource.remove(42l);

        // then
        assertThat(response.getStatus(), is(404));
    }

    @Test
    public void shouldResponseToOptionsRequest() throws Exception {
        // when
        Response response = resource.options();

        // then
        assertThat(response.getStatus(), is(200));
    }

    private class ColourResource extends EntityResource<DummyPerson> {
        @Override
        protected BaseRepository<DummyPerson> getRepository() {
            return EntityResourceTest.this.repository;
        }
    }
}