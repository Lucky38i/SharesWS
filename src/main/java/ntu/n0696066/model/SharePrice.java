package ntu.n0696066.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SharePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    private String currency;
    private float value;
    private long currentShares;
    private LocalDate lastUpdate;

    @OneToMany()
    @JoinColumn(name = "sharePrice_id")
    private final List<Shares> userShares = new ArrayList<>();

    public SharePrice(){};

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
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

    public long getCurrentShares() {
        return currentShares;
    }

    public void setCurrentShares(long currentShares) {
        this.currentShares = currentShares;
    }

    @JsonIgnore
    public List<Shares> getUserShares() {
        return userShares;
    }
}