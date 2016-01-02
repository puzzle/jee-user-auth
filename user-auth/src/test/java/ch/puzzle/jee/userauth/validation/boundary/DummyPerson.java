package ch.puzzle.jee.userauth.validation.boundary;

import ch.puzzle.jee.userauth.auditing.entity.AuditedEntity;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entity just used for unit tests.
 */
@Entity
public class DummyPerson extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dummy_seq")
    @SequenceGenerator(name = "dummy_seq", sequenceName = "dummy_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @Email
    @NotNull
    private String email;

    public DummyPerson() {
        // used by JPA
    }

    public DummyPerson(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
