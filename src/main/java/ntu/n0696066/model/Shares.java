package ntu.n0696066.model;

import javax.persistence.*;

@Entity
@NamedQuery(name ="findShareBySymbolAndUser", query = "FROM Shares WHERE companySymbol = ?1 AND user = ?2")
public class Shares {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long shares_id;
    private String companyName;
    private String companySymbol;
    private long sharesAmount;
    @ManyToOne
    private User user;
    @OneToOne
    private SharePrice sharePrice;

    public long getShares_id() {
        return shares_id;
    }

    public void setShares_id(long id) {
        this.shares_id = id;
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
