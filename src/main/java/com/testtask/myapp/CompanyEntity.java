package com.testtask.myapp;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    private String companyName;
    private String price;
    private String priceChange;
    private long volume;
    private String symbol;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public CompanyEntity(String companyName, String price, long volume, String symbol, Date date) {
        this.companyName = companyName;
        this.price = price;
        this.volume = volume;
        this.symbol = symbol;
        this.date = date;
    }

    protected CompanyEntity() {
    }

    @Override
    public String toString() {
        return id + " | " + companyName + " | " + price + " | " + volume;
    }
}
