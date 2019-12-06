package ntu.n0696066.model;

import javax.persistence.*;

@Entity
public class Shares {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    // TODO make this print the user ID. when retrieving JSON
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shareprice_id", unique = true, referencedColumnName = "id")
    private SharePrice sharePrice;

    private String companyName;
    private String companySymbol;
    private long sharesAmount;

    public Shares(){};

    public long getShares_id() {
        return id;
    }

    public void setShares_id(long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanySymbol() {
        return companySymbol;
    }

    public void setCompanySymbol(String companySymbol) {
        this.companySymbol = companySymbol;
    }

    public long getSharesAmount() {
        return sharesAmount;
    }

    public void setSharesAmount(long sharesAmount) {
        this.sharesAmount = sharesAmount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SharePrice getSharePrice() {
        return sharePrice;
    }

    public void setSharePrice(SharePrice sharePrice) {
        this.sharePrice = sharePrice;
    }
}
