package ntu.n0696066.user;

import javax.persistence.*;

@Entity
@NamedQuery(query= "select u from User u", name = "query_find_all_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String role;

    protected User() {}

    public User(String name) {
        super();
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
