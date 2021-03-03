package com.testtask.myapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testtask.myapp.repositories.MyRepository;
import com.testtask.myapp.urls.APIUrls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class MyAppApplication {

    static MyRepository repo;
    static long timePassed;
    static boolean showPriceQuery = true;

    @Autowired
    public void setRepo(MyRepository rep) {
        repo = rep;
    }

    public static List<String> getData() {
        List<String> list = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        String dataResourceUrl = "https://sandbox.iexapis.com/stable/ref-data/symbols?token=Tpk_ee567917a6b640bb8602834c9d30e571";
        ResponseEntity<String> response = restTemplate.getForEntity(dataResourceUrl, String.class);
        String d = response.getBody();
        String[] a = d.split("[,]");

        for (String i: a) {
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
        List<String> l = getData();
        RestTemplate restTemplate = new RestTemplate();

        while (true) {
            for (String s: l) {
                CompanyEntity ce = null;
                try {

                    ObjectMapper om = new ObjectMapper();

                    JsonNode jn = om.readTree(restTemplate.getForEntity(APIUrls.baseReourceUrl+s+APIUrls.dResourceUrl,String.class).getBody());

                    String name = jn.path("companyName").toString();
                    String p1 = jn.path("latestPrice").toString();
                    String p2 = restTemplate.getForEntity(APIUrls.baseReourceUrl+s+APIUrls.priceResourceUrl,String.class).toString().split(",")[1];
                    String vol = jn.path("volume").toString();
                    String vol2 = jn.path("previousVolume").toString();

                    String cVol = null;
                    float percentChange = 0.0f;

                    if (!p1.equals("null") && !p2.equals("null")) {
                        float p1f = Float.valueOf(p1);
                        float p2f = Float.valueOf(p2);
                        percentChange =  Math.abs(Math.max(p1f,p2f)/Math.min(p1f,p2f)-1.0f)*100.0f;
                    }

                    if (!vol.equals("null")) {
                        cVol = vol;
                    } else if (!vol2.equals("null")) {
                        cVol = vol2;
                    }

                    ce = new CompanyEntity(name, String.valueOf(percentChange), cVol,s);
                    repo.deleteCompanyWithSymbol(ce.getSymbol());
                    repo.save(ce);
                } catch (Exception e) {

                }

                System.out.println("\033[H\033[2J");
                System.out.println("\033[H\033[2J");

                if (showPriceQuery) {
                    List<CompanyEntity> cL = repo.findCompaniesByPricesLimitTo(5);
                    System.out.println("id | companyName | priceChangePercent | volume");
                    for (CompanyEntity c: cL) {
                        System.out.println(0 +
                                " | "+ c.getCompanyName() +
                                " | "+ c.getPrice() +" | "+
                                c.getVolume());
                    }
                } else {
                    List<CompanyEntity> cL = repo.findCompaniesByVolumeLimitTo(5);;
                    System.out.println("id | companyName | priceChangePercent | volume");
                    for (CompanyEntity c: cL) {
                        System.out.println(0 +
                                " | "+ c.getCompanyName() +
                                " | "+ c.getPrice() +" | "+
                                c.getVolume());
                    }
                }

                if (new Date().getTime() - timePassed > 5000) {
                    timePassed = new Date().getTime();
                    showPriceQuery = !showPriceQuery;
                }

            }
        }
    }
}
