package ntu.n0696066.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long user_id;
    private String username;
    private String password;
    @OneToMany(mappedBy = "user")
    private final List<Shares> ownedShares = new ArrayList<Shares>();

    public User() {};

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(long id) {
        this.user_id = id;
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

    public List<Shares> getOwnedShares() {
        return ownedShares;
    }
}
