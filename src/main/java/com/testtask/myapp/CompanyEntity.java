package com.testtask.myapp;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    private String companyName;
    private String price;
    private String volume;
    private String symbol;

    public CompanyEntity(String companyName, String price, String volume, String symbol) {
        this.companyName = companyName;
        this.price = price;
        this.volume = volume;
        this.symbol = symbol;
    }

    protected CompanyEntity() {
    }

    @Override
    public String toString() {
        return "CompanyEntity{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", price='" + price + '\'' +
                ", volume='" + volume + '\'' +
                '}';
    }
}
