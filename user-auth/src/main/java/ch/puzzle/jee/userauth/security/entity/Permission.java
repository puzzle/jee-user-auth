package ch.puzzle.jee.userauth.security.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.EnumSet;

@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_seq")
    @SequenceGenerator(name = "permission_seq", sequenceName = "permission_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PermissionName name;

    @Convert(converter = PermissionActionsConverter.class)
    private EnumSet<Action> actions;

    private Permission() {
        // used by JPA
    }

    public Permission(PermissionName name, EnumSet<Action> actions) {
        this.name = name;
        this.actions = actions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PermissionName getName() {
        return name;
    }

    public void setName(PermissionName name) {
        this.name = name;
    }

    public EnumSet<Action> getActions() {
        return actions;
    }

    public void setActions(EnumSet<Action> actions) {
        this.actions = actions;
    }
}
