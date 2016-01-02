package ch.puzzle.jee.userauth.auditing.entity;

import ch.puzzle.jee.userauth.auditing.boundary.EntityAuditListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(EntityAuditListener.class)
public abstract class AuditedEntity {

    @Version
    private Long version;

    @JsonIgnore
    @Column(name = "changedby")
    private String changedBy;

    @JsonIgnore
    @Column(name = "changedon")
    private LocalDateTime changedOn;

    public abstract void setId(Long id);

    public abstract Long getId();

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getChangedOn() {
        return changedOn;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public void setChangedOn(LocalDateTime changedOn) {
        this.changedOn = changedOn;
    }
}
