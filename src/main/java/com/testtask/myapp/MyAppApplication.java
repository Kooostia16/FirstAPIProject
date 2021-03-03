package com.testtask.myapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
//import java.util.concurrent.ThreadPoolExecutor;

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
            ArrayList<CompanyEntity> ar = new ArrayList<>();
            d = new Date();
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(20);
            int i = 0;
            repo.deleteAll();
            for (String s: l) {
                ResponseEntity<String> response;
                ResponseEntity<String> response2;
                CompanyEntity ce = null;
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    String dResourceUrl = "https://sandbox.iexapis.com/stable/stock/" + s + "/quote?token=Tpk_ee567917a6b640bb8602834c9d30e571";
                    String nameResourceUrl = "https://sandbox.iexapis.com/stable/stock/" + s + "/quote/companyName?token=Tpk_ee567917a6b640bb8602834c9d30e571";
                    String volumeResourceUrl = "https://sandbox.iexapis.com/stable/stock/" + s + "/quote/volume?token=Tpk_ee567917a6b640bb8602834c9d30e571";
                    String priceResourceUrl = "https://sandbox.iexapis.com/stable/stock/" + s + "/quote/latestPrice?token=Tpk_ee567917a6b640bb8602834c9d30e571";
                    String previousVolumeResourceUrl = "https://sandbox.iexapis.com/stable/stock/" + s + "/quote/previousVolume?token=Tpk_ee567917a6b640bb8602834c9d30e571";

                    Date dt = new Date();

                    ObjectMapper om = new ObjectMapper();

                    JsonNode jn = om.readTree(restTemplate.getForEntity(dResourceUrl,String.class).getBody());

                    String name = jn.path("companyName").toString();
                    String p1 = jn.path("latestPrice").toString();
                    String p2 = restTemplate.getForEntity(priceResourceUrl,String.class).toString().split(",")[1];
                    String vol = jn.path("volume").toString();
                    String vol2 = jn.path("previousVolume").toString();

                    String cVol = null;
                    float percentChange = 0.0f;

                    System.out.println((new Date().getTime()) - dt.getTime());
                    System.out.println(p1);
                    System.out.println(p2);
                    System.out.println("-------");

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

                    ce = new CompanyEntity(name, String.valueOf(percentChange), cVol);
                    repo.save(ce);
                } catch (HttpClientErrorException e) {
                    //System.out.println("Error");
                    e.printStackTrace();
                    continue;
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            repo.findCompaniesByPricesLimitTo(5);
            Thread.sleep(5000);
            repo.findCompaniesByVolumeLimitTo(5);
            //Thread.sleep(4000 - (new Date().getTime() - d.getTime()));
        }
    }

}
