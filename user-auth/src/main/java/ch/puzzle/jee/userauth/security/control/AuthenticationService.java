package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.security.entity.User;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;

public class AuthenticationService {

    @Inject
    UserRepository userRepository;

    public User authenticate(String username, String password) {
        if (username == null || username.isEmpty()) {
            notAuthorized();
        }
        if (password == null || password.isEmpty()) {
            notAuthorized();
        }

        User user = userRepository.findByLogin(username);
        if (user == null || !password.equals(user.getPassword())) {
            notAuthorized();
        }
        return user;
    }

    public User getAuthenticatedUser(String username) {
        return userRepository.findByLogin(username);
    }

    private void notAuthorized() {
        throw new NotAuthorizedException("Could not authenticate user. Wrong credentials provided.");
    }
}
