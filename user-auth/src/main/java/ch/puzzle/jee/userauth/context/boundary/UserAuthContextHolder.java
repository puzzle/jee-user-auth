package ch.puzzle.jee.userauth.context.boundary;

import ch.puzzle.jee.userauth.context.entity.UserAuthContext;

/**
 * Provides accessors for the thread-bound UserAuthContext.
 */
public class UserAuthContextHolder {
    private static ThreadLocal<UserAuthContext> holder = new InheritableThreadLocal<>();

    private UserAuthContextHolder() {
    }

    public static UserAuthContext get() {
        return holder.get();
    }

    public static void set(UserAuthContext context) {
        holder.set(context);
    }

    public static void clearContext() {
        holder.remove();
    }
}
