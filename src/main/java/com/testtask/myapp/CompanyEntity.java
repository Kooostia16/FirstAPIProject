package com.testtask.myapp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    String companyName;
    String price;
    String volume;

    public CompanyEntity(String companyName, String price, String volume) {
        this.companyName = companyName;
        this.price = price;
        this.volume = volume;
    }

    protected CompanyEntity() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
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
