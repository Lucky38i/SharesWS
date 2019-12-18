package ntu.n0696066.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonIgnore
    private Long id;

    private String username;
    @JsonIgnore
    private String password;

    @OneToMany()
    @JoinColumn(name = "user_id")
    private final Set<Shares> ownedShares = new HashSet<>();


    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String role) {
        this.password = role;
    }

    public Set<Shares> getOwnedShares() {
        return ownedShares;
    }
}
