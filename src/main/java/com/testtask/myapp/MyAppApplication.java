package com.testtask.myapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Access;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class MyAppApplication {

    static MyRepository repo;

    @Autowired
    public void setRepo(MyRepository rep) {
        repo = rep;
    }

    public static List<String> getData() {
        List<String> list = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = "https://sandbox.iexapis.com/stable/ref-data/symbols?token=Tpk_ee567917a6b640bb8602834c9d30e571";
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        String d = response.getBody();
        String[] a = d.split("[,]");

        for (String i: a) {
            //System.out.println(i.length());
            if (i.length() <= 1) continue;
            if (i.charAt(0) == '{' || i.charAt(0) == '[') {
                String s = i.split(":")[1];
                StringBuilder sb = new StringBuilder(s);
                list.add(sb.deleteCharAt(s.length()-1).deleteCharAt(0).toString());
            }
        }

        return list;
    }

    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(MyAppApplication.class, args);
        Date d;
        List<String> l = getData();

        while (true) {
            d = new Date();
            for (String s: l) {
                ResponseEntity<String> response;
                ResponseEntity<String> response2;
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    String fooResourceUrl = "https://sandbox.iexapis.com/stable/stock/" + s + "/quote?token=Tpk_ee567917a6b640bb8602834c9d30e571";
                    response = restTemplate.getForEntity(fooResourceUrl, String.class);
                    response2 = restTemplate.getForEntity(fooResourceUrl, String.class);
                } catch (HttpClientErrorException e) {
                    continue;
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = null;
                JsonNode root2 = null;
                try {
                    root = mapper.readTree(response.getBody());
                    root2 = mapper.readTree(response2.getBody());
                    String name = root.path("companyName").toString();

                    System.out.println(root.path("latestPrice").toString());
                    System.out.println(root2.path("latestPrice").toString());

                    //repo.save(new CompanyEntity(name, String.valueOf(Math.abs(price/price2-1)),volume));
                } catch (JsonProcessingException e) {

                }
            }
            return;
            //Thread.sleep(4000 - (new Date().getTime() - d.getTime()));
        }
    }

}
