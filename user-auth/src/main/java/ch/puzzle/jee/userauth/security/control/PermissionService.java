package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.security.entity.Action;
import ch.puzzle.jee.userauth.security.entity.PermissionName;
import ch.puzzle.jee.userauth.security.entity.User;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

@Singleton
public class PermissionService {

    @Inject
    PermissionRepository permissionRepository;

    private MultivaluedMap<String, Permission> cache = new MultivaluedHashMap<>();

    private String buildPermissionString(PermissionName permissionName, Action action) {
        return new StringBuilder().append(permissionName.name().toLowerCase()).append('_').append(action.name().toLowerCase()).toString();
    }

    // TODO: time-based cache refresh
    @PostConstruct // cache will be initialized once after creation of singleton bean
    public void initCache() {
        List<User> allUsers = permissionRepository.findAllUsersWithPermissions();
        allUsers.forEach(user -> {
            user.getRoles().forEach(role -> {
                role.getPermissions().forEach(permission -> {
                    Permission permissionTuple = new Permission(permission.getName(), permission.getActions());
                    cache.add(user.getLogin(), permissionTuple);
                });
            });
        });
    }

    public boolean hasPermission(String login, PermissionName requiredPermission, Action requiredAction) {
        List<Permission> permissions = cache.get(login);
        if (permissions == null) {
            return false;
        }
        for (Permission p : permissions) {
            if (requiredPermission.equals(p.getName())) {
                return requiredAction == null || p.getActions().contains(requiredAction);
            }
        }
        return false;
    }

    /**
     * Gets all permissions of a user from cache and concatenates the PermissionNames with the associated actions.
     *
     * @param login The login-name of the technical system user.
     * @return a set of all permissions associated to the user with the provided login.
     */
    public Set<String> getPermissionsForUser(String login) {
        List<Permission> permissions = cache.get(login);
        if (permissions == null) {
            return Collections.emptySet();
        }
        Set<String> permissionStrings = new HashSet<>();
        for (Permission permission : permissions) {
            for (Action action : permission.getActions()) {
                permissionStrings.add(buildPermissionString(permission.getName(), action));
            }
        }
        return permissionStrings;
    }

    private static class Permission {
        private PermissionName name;
        private EnumSet<Action> actions;

        public Permission(PermissionName name, EnumSet<Action> actions) {
            this.name = name;
            this.actions = actions;
        }

        public PermissionName getName() {
            return name;
        }

        public EnumSet<Action> getActions() {
            return actions;
        }
    }
}
