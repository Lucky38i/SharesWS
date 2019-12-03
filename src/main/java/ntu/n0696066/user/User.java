package ntu.n0696066.user;

import ntu.n0696066.shares.Shares;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userName;
    private String password;
    @OneToMany(mappedBy = "user")
    private final List<Shares> ownedShares = new ArrayList<Shares>();

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
