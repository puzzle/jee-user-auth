package ch.puzzle.jee.userauth.auditing.boundary;

import ch.puzzle.jee.userauth.auditing.entity.AuditedEntity;
import ch.puzzle.jee.userauth.context.boundary.UserAuthContextHolder;
import ch.puzzle.jee.userauth.context.entity.UserAuthContext;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class EntityAuditListener {

    @PrePersist
    public void onPersist(AuditedEntity entity) {
        audit(entity);
    }

    @PreUpdate
    public void onUpdate(AuditedEntity entity) {
        audit(entity);
    }

    private void audit(AuditedEntity entity) {
        UserAuthContext context = UserAuthContextHolder.get();
        if (context != null) {
            entity.setChangedBy(context.getUsername());
        } else {
            entity.setChangedBy("unknown");
        }
        entity.setChangedOn(LocalDateTime.now());
    }
}