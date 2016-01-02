package ch.puzzle.jee.userauth.security.boundary;

import ch.puzzle.jee.userauth.UserAuthContextHelper;
import ch.puzzle.jee.userauth.auditing.entity.AuditedEntity;
import ch.puzzle.jee.userauth.context.boundary.UserAuthContextHolder;
import ch.puzzle.jee.userauth.security.control.PermissionService;
import ch.puzzle.jee.userauth.security.entity.Action;
import ch.puzzle.jee.userauth.security.entity.Permission;
import ch.puzzle.jee.userauth.security.entity.PermissionName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.interceptor.InvocationContext;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.*;

import static ch.puzzle.jee.userauth.security.entity.Action.READ;
import static ch.puzzle.jee.userauth.security.entity.PermissionName.TOKEN;
import static ch.puzzle.jee.userauth.security.entity.PermissionName.USER;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PermissionInterceptorTest {

    private static final Map<String, Class[]> SECURED_METHODS = new HashMap<>();
    private static final Map<String, Class[]> ENTITY_METHODS = new HashMap<>();
    private static final List<Permission> PERMISSION_SET_TOKEN = Arrays.asList(new Permission(TOKEN, Action.all()));
    private static final List<Permission> PERMISSION_SET_USER = Arrays.asList(new Permission(USER, EnumSet.of(READ)));

    static {
        SECURED_METHODS.put("persist", new Class[]{UriInfo.class, AuditedEntity.class});
        SECURED_METHODS.put("findAll", new Class[]{});
        SECURED_METHODS.put("findById", new Class[]{Long.class});
        SECURED_METHODS.put("merge", new Class[]{Long.class, AuditedEntity.class});
        SECURED_METHODS.put("remove", new Class[]{Long.class});
        SECURED_METHODS.put("otherPermissionName", new Class[]{});
        SECURED_METHODS.put("noPermissionAction", new Class[]{});
        SECURED_METHODS.put("illegalPermissionAction", new Class[]{});

        ENTITY_METHODS.put("persist", new Class[]{UriInfo.class, AuditedEntity.class});
        ENTITY_METHODS.put("findAll", new Class[]{});
        ENTITY_METHODS.put("findById", new Class[]{Long.class});
        ENTITY_METHODS.put("merge", new Class[]{Long.class, AuditedEntity.class});
        ENTITY_METHODS.put("remove", new Class[]{Long.class});
    }

    @Mock
    InvocationContext context;

    @Mock
    PermissionService permissionService;

    @InjectMocks
    PermissionInterceptor interceptor;

    PermissionAnswer answer = new PermissionAnswer();

    // get Method from DummySecuredEntityResource
    private static Method getEntityResourceMethod(String name, Class[] classes) {
        try {
            return DummySecuredEntityResource.class.getMethod(name, classes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    // get Method from DummySecuredResource
    private static Method getResourceMethod(String name, Class[] classes) {
        try {
            return DummySecuredResource.class.getMethod(name, classes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Before
    public void init() {
        initMocks(this);

        when(context.getTarget()).thenReturn(new DummySecuredEntityResource());
        when(permissionService.hasPermission(anyString(), any(PermissionName.class), any(Action.class))).thenAnswer(answer);

        answer.setSimulatedPermissions(Collections.emptyList());
        UserAuthContextHelper.setupContext();
    }

    @After
    public void teardown() {
        UserAuthContextHelper.clearContext();
    }

    private void expectForbiddenException(boolean failOnGeneralException) {
        try {
            // when
            interceptor.ensurePermissions(context);
            fail("ensurePermissions did not throw the expected exception");
        } catch (ForbiddenException fe) {
            // then
        } catch (Exception e) {
            if (failOnGeneralException) {
                fail(e.getMessage());
            }
        }
    }

    private void expectIllegalStateException(String messageContains) {
        try {
            // when
            interceptor.ensurePermissions(context);
            fail("ensurePermissions did not throw the expected exception");
        } catch (IllegalStateException fe) {
            // then
            assertThat(fe.getMessage(), containsString(messageContains));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void expectPermissionsOk() {
        try {
            // when
            interceptor.ensurePermissions(context);

            // then
            verify(context, atLeastOnce()).proceed();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = NotAuthorizedException.class)
    public void shouldNotAllowNullContext() throws Exception {
        // given
        UserAuthContextHolder.set(null);

        // when
        interceptor.ensurePermissions(context);
    }

    @Test
    public void shouldGetAccessDeniedOnAllSecuredMethods() {
        for (String methodName : SECURED_METHODS.keySet()) {
            // given
            when(context.getMethod()).thenReturn(getEntityResourceMethod(methodName, SECURED_METHODS.get(methodName)));

            // when/then
            expectForbiddenException(false);
        }
    }

    @Test
    public void shouldPassOnEntityMethods() {
        for (String methodName : ENTITY_METHODS.keySet()) {
            // given
            when(context.getMethod()).thenReturn(getEntityResourceMethod(methodName, ENTITY_METHODS.get(methodName)));
            answer.setSimulatedPermissions(PERMISSION_SET_TOKEN);

            // when/then
            expectPermissionsOk();
        }
    }

    @Test
    public void shouldFailOnAssociationPermissions() {
        // given
        when(context.getMethod()).thenReturn(getEntityResourceMethod("otherPermissionName", SECURED_METHODS.get("otherPermissionName")));
        answer.setSimulatedPermissions(PERMISSION_SET_TOKEN);

        // when/then
        expectForbiddenException(true);
    }

    @Test
    public void shouldPassOnAthletePermissions() {
        // given
        when(context.getMethod()).thenReturn(getEntityResourceMethod("otherPermissionName", SECURED_METHODS.get("otherPermissionName")));
        answer.setSimulatedPermissions(PERMISSION_SET_USER);

        // when/then
        expectPermissionsOk();
    }

    @Test
    public void shouldFailOnMissingPermissionAction() {
        // given
        when(context.getMethod()).thenReturn(getEntityResourceMethod("noPermissionAction", SECURED_METHODS.get("noPermissionAction")));

        // when/then
        expectIllegalStateException("only has the default action SPECIFY");
    }

    @Test
    public void shouldFailOnIllegalPermissionAction() {
        // given
        when(context.getMethod()).thenReturn(getEntityResourceMethod("illegalPermissionAction", SECURED_METHODS.get("illegalPermissionAction")));

        // when/then
        expectIllegalStateException("only has the default action SPECIFY");
    }

    @Test
    public void shouldFailOnActionWithoutPermissionName() {
        // given
        when(context.getMethod()).thenReturn(getResourceMethod("actionWithoutPermissionName", new Class[]{}));
        when(context.getTarget()).thenReturn(new DummySecuredResource());

        // when/then
        expectIllegalStateException("there is no @RequiresPermission");
    }

    @Test
    public void shouldNotNeedAuthorization() {
        // given
        when(context.getMethod()).thenReturn(getResourceMethod("noPermissionRequired", new Class[]{}));
        when(context.getTarget()).thenReturn(new DummySecuredResource());

        // when/then
        expectPermissionsOk();
    }

    @Test
    public void shouldFailOnDefaultPermissionOnMethod() {
        // given
        when(context.getMethod()).thenReturn(getResourceMethod("illegalPermissionAction", new Class[]{}));
        when(context.getTarget()).thenReturn(new DummySecuredResource());

        // when/then
        expectIllegalStateException("is not allowed to have the default action SPECIFY");
    }

    private static class PermissionAnswer implements Answer {

        private List<Permission> simulatedPermissions;

        private PermissionAnswer() {
            simulatedPermissions = Collections.emptyList();
        }

        private void setSimulatedPermissions(List<Permission> permissions) {
            this.simulatedPermissions = permissions;
        }

        @Override
        public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            Object[] args = invocationOnMock.getArguments();
            PermissionName name = (PermissionName) args[1];
            Action action = (Action) args[2];
            for (Permission p : simulatedPermissions) {
                if (name.equals(p.getName())) {
                    if (p.getActions().contains(action)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
