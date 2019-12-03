package ntu.n0696066.shares;

import ntu.n0696066.user.User;

import javax.persistence.*;

@Entity
public class Shares {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String companyName;
    private String companySymbol;
    private long sharesAmount;
    private User user;
    private SharePrice sharePrice;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToOne
    public SharePrice getSharePrice() {
        return sharePrice;
    }

    public void setSharePrice(SharePrice sharePrice) {
        this.sharePrice = sharePrice;
    }
}
