package ch.puzzle.jee.userauth.security.boundary;

import ch.puzzle.jee.userauth.BaseRepository;
import ch.puzzle.jee.userauth.EntityResource;
import ch.puzzle.jee.userauth.security.entity.Action;
import ch.puzzle.jee.userauth.validation.boundary.DummyPerson;
import org.mockito.Mock;

import static ch.puzzle.jee.userauth.security.entity.Action.SPECIFY;
import static ch.puzzle.jee.userauth.security.entity.PermissionName.TOKEN;
import static ch.puzzle.jee.userauth.security.entity.PermissionName.USER;

@RequiresPermission(TOKEN)
public class DummySecuredEntityResource extends EntityResource<DummyPerson> {

    @Mock
    BaseRepository<DummyPerson> repository;

    @Override
    protected BaseRepository<DummyPerson> getRepository() {
        return repository;
    }

    @RequiresPermission(value = USER, action = Action.READ)
    public void otherPermissionName() {
        // nothing to do here
    }

    public void noPermissionAction() {
        // nothing to do here
    }

    @RequiresPermissionAction(SPECIFY)
    public void illegalPermissionAction() {
        // nothing to do here
    }
}
