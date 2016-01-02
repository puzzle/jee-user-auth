package ch.puzzle.jee.userauth.security.boundary;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.lang.reflect.Method;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AuthenticationFeatureTest {
    private AuthenticationFeature feature;

    @Before
    public void init() {
        feature = new AuthenticationFeature();
    }

    @Test
    public void shouldRegisterFilterWhenIsPermitAllReturnsFalse() throws Exception {
        // given
        AuthenticationFeature feature = new AuthenticationFeature() {
            @Override
            boolean isPermitAll(ResourceInfo resourceInfo) {
                return false;
            }
        };

        // when
        FeatureContext context = mock(FeatureContext.class);
        feature.configure(mock(ResourceInfo.class), context);

        // then
        verify(context).register(any(AuthenticationFilter.class));
    }

    @Test
    public void shouldNotRegisterFilterWhenIsPermitAllReturnsTrue() throws Exception {
        // given
        AuthenticationFeature feature = new AuthenticationFeature() {
            @Override
            boolean isPermitAll(ResourceInfo resourceInfo) {
                return true;
            }
        };

        // when
        FeatureContext context = mock(FeatureContext.class);
        feature.configure(mock(ResourceInfo.class), context);

        // then
        verify(context, never()).register(any(AuthenticationFilter.class));
    }

    @Test
    public void shouldNotPermitAllWithoutAnnotation() throws Exception {
        // given
        DummyResourceInfo resourceInfo = new DummyResourceInfo(new WithoutAnnotations());

        // when & then
        assertThat(feature.isPermitAll(resourceInfo), is(false));
    }

    @Test
    public void shouldNotPermitAllWhenResourceClassIsNull() throws Exception {
        // given
        DummyResourceInfoWithoutResourceClass resourceInfo = new DummyResourceInfoWithoutResourceClass();

        // when & then
        assertThat(feature.isPermitAll(resourceInfo), is(false));
    }

    @Test
    public void shouldNotPermitAllWhenResourceMethodIsNull() throws Exception {
        // given
        DummyResourceInfoWithoutResourceMethod resourceInfo = new DummyResourceInfoWithoutResourceMethod();

        // when & then
        assertThat(feature.isPermitAll(resourceInfo), is(false));
    }

    @Test
    public void shouldPermitAllWithAnnotationOnClassLevel() throws Exception {
        // given
        DummyResourceInfo resourceInfo = new DummyResourceInfo(new WithClassAnnotation());

        // when & then
        assertThat(feature.isPermitAll(resourceInfo), is(true));
    }

    @Test
    public void shouldPermitAllWithAnnotationOnMethodLevel() throws Exception {
        // given
        DummyResourceInfo resourceInfo = new DummyResourceInfo(new WithMethodAnnotation());

        // when & then
        assertThat(feature.isPermitAll(resourceInfo), is(true));
    }

    @Test
    public void shouldPermitAllWithAnnotationOnClassAndMethodLevel() throws Exception {
        // given
        DummyResourceInfo resourceInfo = new DummyResourceInfo(new WithClassAndMethodAnnotation());

        // when & then
        assertThat(feature.isPermitAll(resourceInfo), is(true));
    }

    private interface TestClass {
    }

    private static class DummyResourceInfo implements ResourceInfo {
        private TestClass dummyClass;

        public DummyResourceInfo(TestClass dummyClass) {
            this.dummyClass = dummyClass;
        }

        @Override
        public Method getResourceMethod() {
            try {
                return dummyClass.getClass().getMethod("doBusinessLogic");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getResourceClass() {
            return dummyClass.getClass();
        }
    }

    private static class DummyResourceInfoWithoutResourceMethod implements ResourceInfo {
        @Override
        public Method getResourceMethod() {
            return null;
        }

        @Override
        public Class<?> getResourceClass() {
            return getClass();
        }
    }

    private static class DummyResourceInfoWithoutResourceClass implements ResourceInfo {
        @Override
        public Method getResourceMethod() {
            try {
                return getClass().getMethod("getResourceClass");
            } catch (NoSuchMethodException e) {
            }
            return null;
        }

        @Override
        public Class<?> getResourceClass() {
            return null;
        }
    }

    private static class WithoutAnnotations implements TestClass {
        public void doBusinessLogic() {
        }
    }

    @PermitAll
    private static class WithClassAnnotation implements TestClass {
        public void doBusinessLogic() {
        }
    }

    private static class WithMethodAnnotation implements TestClass {
        @PermitAll
        public void doBusinessLogic() {
        }
    }

    @PermitAll
    private static class WithClassAndMethodAnnotation implements TestClass {
        @PermitAll
        public void doBusinessLogic() {
        }
    }
}