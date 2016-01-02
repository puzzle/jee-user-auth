package ch.puzzle.jee.userauth.security.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "tokenString"))
public class Token {

    public static final int DEFAULT_TTL_SECONDS = 3600;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_seq")
    @SequenceGenerator(name = "token_seq", sequenceName = "token_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private String tokenString;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    private LocalTime validUntil;

    @Transient
    private long ttl = DEFAULT_TTL_SECONDS;

    private Token() {
        refreshValidUntil();
    }

    public Token(String tokenString, User user) {
        this.tokenString = tokenString;
        this.user = user;
        refreshValidUntil();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalTime getValidUntil() {
        return validUntil;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public void refreshValidUntil() {
        this.validUntil = LocalTime.now().plusSeconds(ttl);
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", tokenString='" + tokenString + '\'' +
                ", validUntil=" + validUntil +
                '}';
    }
}
