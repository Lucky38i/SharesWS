package ntu.n0696066.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SharePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long sharePrice_id;
    private String currency;
    private float value;
    private LocalDateTime lastUpdate;

    public long getSharePrice_id() {
        return sharePrice_id;
    }

    public void setID(long id){
        this.sharePrice_id = id;
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

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}