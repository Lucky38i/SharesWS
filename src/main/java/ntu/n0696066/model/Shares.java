package ntu.n0696066.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Shares {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    private String companyName;
    private String companySymbol;
    private long ownedShares;

    @ManyToOne()
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "sharePrice_id", insertable = false, updatable = false)
    private SharePrice sharePrice;

    public Shares(){};

    @JsonIgnore
    public long getShares_id() {
        return id;
    }

    @JsonIgnore
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

    public long getOwnedShares() {
        return ownedShares;
    }

    public void setOwnedShares(long sharesAmount) {
        this.ownedShares = sharesAmount;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonIgnore
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
