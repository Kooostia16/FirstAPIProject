package com.testtask.myapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testtask.myapp.repositories.MyRepository;
import lombok.SneakyThrows;
import org.h2.util.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.testtask.myapp.stringUtils.StrUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class MyAppApplication {

    static MyRepository repo;
    static long timePassed;
    static boolean showPriceQuery = true;

    static RestTemplate restTemplate;

    static String baseUrl;
    static String dUrl;
    static String priceUrl;
    static String sourceUrl;

    static int maxStrL = 25;

    @Value("${baseUrl}")
    public void setBaseUrl(String baseUrl) {
        MyAppApplication.baseUrl = baseUrl;
    }

    @Value("${dUrl}")
    public void setdUrl(String dUrl) {
        MyAppApplication.dUrl = dUrl;
    }

    @Value("${priceUrl}")
    public void setPriceUrl(String priceUrl) {
        MyAppApplication.priceUrl = priceUrl;
    }

    @Value("${sourceUrl}")
    public void setSourceUrl(String sourceUrl) {
        MyAppApplication.sourceUrl = sourceUrl;
    }

    @Bean
    public RestTemplate setRestTemplate() {
        this.restTemplate = new RestTemplate();
        return this.restTemplate;
    }

    @Autowired
    public void setRepo(MyRepository rep) {
        repo = rep;
    }

    public static List<String> getData() throws JsonProcessingException {
        List<String> list = new ArrayList<>();
        String jsonStr = restTemplate.getForObject(sourceUrl,String.class);

        ObjectMapper om = new ObjectMapper();

        JsonNode rootAr = om.readTree(jsonStr);

        for (int i = 0; i < rootAr.size(); i++) {
            try {
                String arrayElement = rootAr.get(i).toString();
                JsonNode root = om.readTree(arrayElement);
                list.add(root.path("symbol").textValue());
            } catch(Exception e) {
                //Just skip it
            }
        }

        return list;
    }

    public static void main(String[] args) throws JsonProcessingException {

        SpringApplication.run(MyAppApplication.class, args);
        List<String> l = getData();

        while (true) {
            for (String s: l) {
                CompanyEntity ce;
                try {
                    ObjectMapper om = new ObjectMapper();
                    JsonNode jn = om.readTree(restTemplate.getForEntity(baseUrl+s+dUrl,String.class).getBody());


                    String name = jn.path("companyName").toString();
                    String p1 = jn.path("latestPrice").toString();
                    String p2 = restTemplate.getForEntity(baseUrl+s+priceUrl,String.class).toString().split(",")[1];
                    String vol = jn.path("volume").toString();
                    String vol2 = jn.path("previousVolume").toString();

                    long cVol = 0;
                    float percentChange = 0.0f;

                    if (!p1.equals("null") && !p2.equals("null")) {
                        float p1f = Float.parseFloat(p1);
                        float p2f = Float.parseFloat(p2);
                        percentChange =  Math.abs(Math.max(p1f,p2f)/Math.min(p1f,p2f)-1.0f)*100.0f;
                    }

                    if (!vol.equals("null")) {
                        cVol = Long.parseLong(vol);
                    } else if (!vol2.equals("null")) {
                        cVol = Long.parseLong(vol2);
                    }

                    ce = new CompanyEntity(name, String.valueOf(percentChange), cVol,s, new Date());
                    repo.save(ce);
                } catch (Exception e) {
                    //Nothing
                }

                System.out.println("\033[H\033[2J");

                if (showPriceQuery) {
                    List<Object[]> cL = repo.findCompaniesByPricesLimitTo(5);
                    showInfo(cL);
                    System.out.println("\n Companies ordered by max change of price in percent");
                } else {
                    List<Object[]> cL = repo.findCompaniesByVolumeLimitTo(5);;
                    showInfo(cL);
                    System.out.println("\n Companies ordered by max volume");
                }

                if (new Date().getTime() - timePassed > 5000) {
                    timePassed = new Date().getTime();
                    showPriceQuery = !showPriceQuery;
                }

            }
        }
    }

    private static void showInfo(List<Object[]> cL) {
        System.out.println("companyName | priceChangePercent | volume");
        for (Object[] i: cL) {
            String name, price, volume;
            name = i[1].toString();
            price = i[2].toString();
            volume = (i[3]==null?0:i[3]).toString();
            maxStrL = Math.max(Math.max(Math.max(name.length(), price.length()), volume.length()),maxStrL);
            System.out.println(StrUtils.fillMaxStringLengthWith(name," ", maxStrL) +
                    " | " + StrUtils.fillMaxStringLengthWith(price, " ", maxStrL) +
                    " | " + StrUtils.fillMaxStringLengthWith(volume, " ", maxStrL));

        }
    }
}
