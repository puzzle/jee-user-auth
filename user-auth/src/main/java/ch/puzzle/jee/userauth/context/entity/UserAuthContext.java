package ch.puzzle.jee.userauth.context.entity;

import ch.puzzle.jee.userauth.security.entity.User;

import java.util.Objects;

/**
 * Immutable representation of the request-bound application specific context.
 */
public final class UserAuthContext {

    private final User user;
    private final String username;
    private final String token;

    public UserAuthContext(User user, String username, String token) {
        this.user = Objects.requireNonNull(user, "User must not be null for context creation");
        this.username = Objects.requireNonNull(username, "Username must not be null for context creation");
        this.token = Objects.requireNonNull(token, "Token must not be null for context creation");
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAuthContext that = (UserAuthContext) o;
        return Objects.equals(username, that.username) && Objects.equals(token, that.token) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, username, token);
    }

    @Override
    public String toString() {
        return "UserAuthContext{" +
                "user=" + user +
                ", username='" + username + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
