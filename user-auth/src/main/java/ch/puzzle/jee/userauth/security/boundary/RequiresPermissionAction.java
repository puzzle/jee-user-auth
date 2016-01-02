package ch.puzzle.jee.userauth.security.boundary;

import ch.puzzle.jee.userauth.security.entity.Action;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD})
public @interface RequiresPermissionAction {
    Action value();
}
