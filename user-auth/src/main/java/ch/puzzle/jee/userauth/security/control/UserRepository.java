package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.BaseRepository;
import ch.puzzle.jee.userauth.security.entity.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository extends BaseRepository<User> {

    public User findByLogin(String login) {
        return singleResult(createNamedQuery("User.findByLogin").setParameter("login", login));
    }
}
