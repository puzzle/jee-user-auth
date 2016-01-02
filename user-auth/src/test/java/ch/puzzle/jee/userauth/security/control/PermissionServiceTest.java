package ch.puzzle.jee.userauth.security.control;

import ch.puzzle.jee.userauth.security.entity.Permission;
import ch.puzzle.jee.userauth.security.entity.Role;
import ch.puzzle.jee.userauth.security.entity.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static ch.puzzle.jee.userauth.security.entity.Action.*;
import static ch.puzzle.jee.userauth.security.entity.PermissionName.TOKEN;
import static ch.puzzle.jee.userauth.security.entity.PermissionName.USER;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PermissionServiceTest {

    private PermissionService service;

    @Before
    public void init() {
        service = new PermissionService();
        service.permissionRepository = mock(PermissionRepository.class);
    }

    @Test
    public void shouldVerifyForExistingPermissionAndExistingAction() throws Exception {
        // given
        initCacheWithUserAndPermission();

        // when & then
        assertThat(service.hasPermission("user", TOKEN, READ), is(true));
    }

    @Test
    public void shouldVerifyForExistingPermissionAndNotExistingAction() throws Exception {
        // given
        initCacheWithUserAndPermission();

        // when & then
        assertThat(service.hasPermission("user", TOKEN, UPDATE), is(false));
    }

    @Test
    public void shouldVerifyForNonExistingPermissionAndExistingAction() throws Exception {
        // given
        initCacheWithUserAndPermission();

        // when & then
        assertThat(service.hasPermission("user", USER, READ), is(false));
    }

    @Test
    public void shouldVerifyForNonExistingPermissionAndNonExistingAction() throws Exception {
        // given
        initCacheWithUserAndPermission();

        // when & then
        assertThat(service.hasPermission("user", USER, UPDATE), is(false));
    }

    @Test
    public void shouldVerifyForExistingPermissionAndNullAction() throws Exception {
        // given
        initCacheWithUserAndPermission();

        // when & then
        assertThat(service.hasPermission("user", TOKEN, null), is(true));
    }

    @Test
    public void shouldVerifyForNonExistingPermissionAndNullAction() throws Exception {
        // given
        initCacheWithUserAndPermission();

        // when & then
        assertThat(service.hasPermission("user", USER, null), is(false));
    }

    @Test
    public void shouldVerifyForUserNotFoundInCache() throws Exception {
        // when & then
        assertThat(service.hasPermission("user", TOKEN, READ), is(false));
    }

    @Test
    public void shouldVerifyForEmptyCache() throws Exception {
        // given
        when(service.permissionRepository.findAllUsersWithPermissions()).thenReturn(Collections.emptyList());
        service.initCache();

        // when & then
        assertThat(service.hasPermission("user", TOKEN, READ), is(false));
    }

    @Test
    public void shouldFindAllPermissionsAndActionsForUser() throws Exception {
        // given
        initCacheWithUserAndPermission();

        // when
        Set<String> permissions = service.getPermissionsForUser("user");

        // then
        assertThat(permissions, containsInAnyOrder("token_create", "token_read"));
    }

    @Test
    public void shouldReturnEmptySetForUserWithoutPermissions() throws Exception {
        // when
        Set<String> permissions = service.getPermissionsForUser("non-existing-user");

        // then
        assertThat(permissions, is(empty()));
    }

    private void initCacheWithUserAndPermission() {
        Permission permission = new Permission(TOKEN, EnumSet.of(READ, CREATE));
        Role role = new Role("UserRole").addPermission(permission);
        User user = new User("user", "pass").addRole(role);

        when(service.permissionRepository.findAllUsersWithPermissions()).thenReturn(singletonList(user));
        service.initCache();
    }
}