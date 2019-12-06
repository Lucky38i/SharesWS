package ntu.n0696066.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class SharePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @OneToOne(mappedBy = "sharePrice")
    private Shares share;

    private String currency;
    private float value;
    private LocalDate lastUpdate;

    public SharePrice(){};

    public long getId() {
        return id;
    }

    public void setID(long id){
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Shares getShare() {
        return share;
    }

    public void setShare(Shares share) {
        this.share = share;
    }
}