package ch.puzzle.jee.userauth.security.boundary;

import static ch.puzzle.jee.userauth.security.entity.Action.READ;
import static ch.puzzle.jee.userauth.security.entity.PermissionName.TOKEN;

public class DummySecuredResource {

    @RequiresPermissionAction(READ)
    public void actionWithoutPermissionName() {
        // nothing to do here
    }

    public void noPermissionRequired() {
        // nothing to do here
    }

    @RequiresPermission(TOKEN)
    public void illegalPermissionAction() {
        // nothing to do here
    }
}
