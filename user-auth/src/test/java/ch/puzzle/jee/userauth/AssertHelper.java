package ch.puzzle.jee.userauth;

import ch.puzzle.jee.userauth.testdata.UriInfoBuilder;

import javax.json.JsonObject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;

public final class AssertHelper {

    public static void assertBusinessError(Response response, String expectedKey) {
        assertThat(response.getStatus(), is(400));
        assertTrue(response.getEntity() instanceof JsonObject);
        assertThat(response.getEntity().toString(), containsString(expectedKey));
    }

    public static void assertEntityNotFound(Response response) {
        assertThat(response.getStatus(), is(404));
    }

    public static void assertOk(Response response) {
        assertThat(response.getStatus(), is(200));
    }

    /**
     * Assert that every method declared on the instance that has the @Path annotation will return a
     * HTTP response with a status code of 404 when queried with an invalid ID (e.g. -1).
     *
     * @param instance The instance of an entity resource to test.
     */
    public static void assertResourceMethodsReturnNotFoundOnInvalidId(EntityResource<?> instance, List<String> exceptions) {
        Objects.requireNonNull(instance, "instance cannot be null");
        Objects.requireNonNull(exceptions, "exceptions cannot be null");

        Class<?> clazz = instance.getClass();

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {

            // is there an annotation @Path?
            if (isResourceEndpointWithId(method) && !exceptions.contains(method.getName())) {
                try {
                    // get default values for parameter and invoke the method
                    Object[] invocationParameter = getInvocationParameter(method);
                    Object returnValue = method.invoke(instance, invocationParameter);

                    assertTrue("Response needs to be of type Response: " + method.getName(), returnValue instanceof Response);
                    assertThat("Response needs to be HTTP 404: " + method.getName(), ((Response) returnValue).getStatus(), is(404));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    fail("Method " + method.getName() + " declared on " + clazz.getName() + " could not be invoked " +
                            "to test for correct default return value: " + e.getMessage());
                }
            }
        }
    }

    public static void assertResourceMethodsReturnNotFoundOnInvalidId(EntityResource<?> instance) {
        assertResourceMethodsReturnNotFoundOnInvalidId(instance, Collections.emptyList());
    }

    private static boolean isResourceEndpointWithId(Method method) {
        return method.getAnnotation(Path.class) != null && hasPathParamAnnotation(method);
    }

    private static Object[] getInvocationParameter(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] params = new Object[method.getParameterCount()];

        for (int i = 0; i < parameterTypes.length; i++) {
            // a parameter is either an ID where we assign -1, an UriInfo that we set or an entity that we leave null
            if (hasPathParamAnnotation(parameterAnnotations[i])) {
                assertTrue("A parameter with the @PathParam must be of type long: " + method.getName(), parameterTypes[i] == long.class);

                params[i] = -1L;
            } else if (parameterTypes[i] == UriInfo.class) {
                params[i] = new UriInfoBuilder().defaults().build();
            }
        }

        return params;
    }

    private static boolean hasPathParamAnnotation(Method method) {
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            if (hasPathParamAnnotation(annotations)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasPathParamAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof PathParam) {
                return true;
            }
        }
        return false;
    }
}
