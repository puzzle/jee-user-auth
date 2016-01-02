package ch.puzzle.jee.userauth;

import ch.puzzle.jee.userauth.context.boundary.UserAuthContextHolder;
import ch.puzzle.jee.userauth.context.entity.UserAuthContext;
import ch.puzzle.jee.userauth.security.entity.User;

public class UserAuthContextHelper {
    public final static String TOKEN = "xaEFZasXUaZxUuKsSPkktrhwcUHMVHVU";
    public final static String USERNAME = "peter";

    private UserAuthContextHelper() {
    }

    public static void setupContext() {
        User user = createUser(USERNAME);
        setupContext(user, USERNAME, TOKEN);
    }

    public static void setupContext(String username, String token) {
        User user = createUser(username);
        setupContext(user, username, token);
    }

    public static void setupContext(User user, String username, String token) {
        UserAuthContext context = new UserAuthContext(user, username, token);
        UserAuthContextHolder.set(context);
    }

    public static void clearContext() {
        UserAuthContextHolder.clearContext();
    }

    private static User createUser(String username) {
        return new User(username, "password");
    }
}
