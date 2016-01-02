package ch.puzzle.jee.userauth;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedMap;

import static org.mockito.Mockito.*;

public class CORSAwareFilterTest {

    private CORSAwareFilter filter;

    @Before
    public void init() {
        filter = new CORSAwareFilter();
    }

    @Test
    public void shouldAddCorsAwareHeaderAttributes() throws Exception {
        // given
        MultivaluedMap<String, Object> headers = mock(MultivaluedMap.class);
        ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
        when(responseContext.getHeaders()).thenReturn(headers);

        // when
        filter.filter(null, responseContext);

        // then
        verify(headers).add(eq("Access-Control-Allow-Origin"), anyString());
        verify(headers).add(eq("Access-Control-Allow-Methods"), anyString());
        verify(headers).add(eq("Access-Control-Allow-Headers"), anyString());
    }
}