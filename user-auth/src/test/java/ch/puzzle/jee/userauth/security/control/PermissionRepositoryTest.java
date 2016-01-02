package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.PersistenceTestRunner;
import ch.puzzle.jee.userauth.security.entity.Action;
import ch.puzzle.jee.userauth.security.entity.Permission;
import ch.puzzle.jee.userauth.security.entity.Role;
import ch.puzzle.jee.userauth.security.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.EnumSet;
import java.util.List;

import static ch.puzzle.jee.userauth.security.entity.Action.READ;
import static ch.puzzle.jee.userauth.security.entity.PermissionName.TOKEN;
import static ch.puzzle.jee.userauth.security.entity.PermissionName.USER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(PersistenceTestRunner.class)
public class PermissionRepositoryTest {

    private PermissionRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void init() {
        repository = new PermissionRepository();
        repository.setEntityManager(entityManager);
    }

    @Test
    public void shouldFindPermissions() throws Exception {
        // given
        Permission p1 = new Permission(USER, Action.all());
        Permission p2 = new Permission(TOKEN, EnumSet.of(READ));
        entityManager.persist(p1);
        entityManager.persist(p2);

        Role role = new Role("UserRole");
        role.addPermission(p1).addPermission(p2);
        entityManager.persist(role);

        User user = new User("user", "pass");
        user.addRole(role);
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();

        // when
        List<User> result = repository.findAllUsersWithPermissions();

        // close the entity manager to prohibit lazy loading
        entityManager.close();

        // then
        assertThat(result.size(), is(1));
        User userResult = result.get(0);
        assertThat(userResult.getRoles().size(), is(1));

        Role roleResult = userResult.getRoles().iterator().next();
        assertThat(roleResult.getPermissions().size(), is(2));
    }
}