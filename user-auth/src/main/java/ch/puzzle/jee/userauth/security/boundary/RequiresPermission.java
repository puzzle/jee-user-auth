package ch.puzzle.jee.userauth.security.boundary;

import ch.puzzle.jee.userauth.security.entity.Action;
import ch.puzzle.jee.userauth.security.entity.PermissionName;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface RequiresPermission {
    PermissionName value();

    Action action() default Action.SPECIFY;
}
