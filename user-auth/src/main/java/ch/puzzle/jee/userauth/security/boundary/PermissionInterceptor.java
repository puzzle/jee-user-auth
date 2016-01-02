package ch.puzzle.jee.userauth.security.boundary;

import ch.puzzle.jee.userauth.context.boundary.UserAuthContextHolder;
import ch.puzzle.jee.userauth.context.entity.UserAuthContext;
import ch.puzzle.jee.userauth.security.control.PermissionService;
import ch.puzzle.jee.userauth.security.entity.Action;
import ch.puzzle.jee.userauth.security.entity.PermissionName;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import java.util.Objects;

import static ch.puzzle.jee.userauth.security.entity.Action.SPECIFY;

public class PermissionInterceptor {

    @Inject
    PermissionService permissionService;

    private static PermissionName getRequiredPermission(InvocationContext ctx) {
        RequiresPermission classAnnotation = getClassPermissionAnnotation(ctx);
        RequiresPermission methodAnnotation = getMethodPermissionAnnotation(ctx);

        // the annotation on the method always has precedence over annotations declared on the class
        if (methodAnnotation != null) {
            return methodAnnotation.value();
        } else {
            // if there is a permission annotation on the class, use that, otherwise we assume no permission needed
            return classAnnotation != null ? classAnnotation.value() : null;
        }
    }

    private static Action getRequiredAction(InvocationContext ctx) {
        RequiresPermission classPermissionAnnotation = getClassPermissionAnnotation(ctx);
        RequiresPermission methodPermissionAnnotation = getMethodPermissionAnnotation(ctx);
        RequiresPermissionAction methodPermissionActionAnnotation = getMethodPermissionActionAnnotation(ctx);

        // the @RequiresPermissionAction always has precedence over @RequiresPermission
        if (methodPermissionActionAnnotation != null) {
            return methodPermissionActionAnnotation.value();
        } else {
            // next we check if there is a @RequiresPermission on the method
            if (methodPermissionAnnotation != null) {
                return methodPermissionAnnotation.action();
            } else {
                // maybe there is a @RequiresPermission on the class
                return classPermissionAnnotation != null ? classPermissionAnnotation.action() : null;
            }
        }
    }

    private static RequiresPermission getClassPermissionAnnotation(InvocationContext ctx) {
        return ctx.getTarget().getClass().getAnnotation(RequiresPermission.class);
    }

    private static RequiresPermission getMethodPermissionAnnotation(InvocationContext ctx) {
        RequiresPermission permission = ctx.getMethod().getAnnotation(RequiresPermission.class);
        if (permission != null && SPECIFY.equals(permission.action())) {
            throw new IllegalStateException("The annotation @RequiresPermission is not allowed to have the " +
                    "default action SPECIFY when set on a method. This is only valid for a Class!");
        }
        return permission;
    }

    private static RequiresPermissionAction getMethodPermissionActionAnnotation(InvocationContext ctx) {
        return ctx.getMethod().getAnnotation(RequiresPermissionAction.class);
    }

    @AroundInvoke
    public Object ensurePermissions(InvocationContext ctx) throws Exception {
        UserAuthContext context = UserAuthContextHolder.get();
        if (context == null) {
            throw new NotAuthorizedException("Not authorized");
        }
        Objects.requireNonNull(context.getUsername(), "username name must not be null");
        Objects.requireNonNull(ctx.getMethod(), "method must not be null");

        PermissionName requiredPermission = getRequiredPermission(ctx);
        Action requiredAction = getRequiredAction(ctx);

        // if the required permission is null the user doesn't need to be authorized
        if (requiredPermission == null && requiredAction == null) {
            return ctx.proceed();
        } else if (requiredPermission == null) {
            // there is an action set without a permission name. this might happen if a subclass of
            // entity resource forgot to declare @RequiresPermission on the class
            throw new IllegalStateException("The method " + ctx.getMethod().getName() + " has a " +
                    "@RequiresPermissionAction set but there is no @RequiresPermission either on the method or the class.");
        } else if (SPECIFY.equals(requiredAction)) {
            // someone set the @RequiresPermission with the default action SPECIFY on the class but forgot to
            // add a @RequiresPermissionAction on the method
            throw new IllegalStateException("The method " + ctx.getMethod().getName() + " or its class only has the " +
                    "default action SPECIFY set which must be overridden at method level.");
        }

        boolean authorized = permissionService.hasPermission(context.getUsername(), requiredPermission, requiredAction);
        if (authorized) {
            return ctx.proceed();
        } else {
            throw new ForbiddenException("User " + context.getUsername() + " does not have the required permission " +
                    requiredPermission + " " + requiredAction);
        }
    }
}
