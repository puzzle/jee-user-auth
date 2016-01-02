package ch.puzzle.jee.userauth.testdata;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UriInfoBuilder extends BaseBuilder<UriInfo, UriInfoBuilder> {

    private String path;

    @Override
    public UriInfo build() {
        UriInfo uriInfo = mock(UriInfo.class);
        UriBuilder uriBuilder = mock(UriBuilder.class);
        when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
        when(uriInfo.getBaseUriBuilder()).thenReturn(uriBuilder);

        try {
            when(uriBuilder.build()).thenReturn(new URI(path));
        } catch (URISyntaxException e) {

        }
        return uriInfo;
    }

    @Override
    public UriInfoBuilder defaults() {
        path = "/";
        return this;
    }

    public UriInfoBuilder path(String path) {
        this.path = path;
        return this;
    }

    @Override
    public UriInfoBuilder id() {
        return this;
    }
}
