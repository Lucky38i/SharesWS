package ntu.n0696066.shares;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SharePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String currency;
    private float value;
    private LocalDateTime lastUpdate;

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

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}