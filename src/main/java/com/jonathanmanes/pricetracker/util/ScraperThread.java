package com.jonathanmanes.pricetracker.util;

import java.util.*;
import java.text.*;
import java.lang.*;

import java.io.IOException;

import com.jonathanmanes.pricetracker.model.Product;
import com.jonathanmanes.pricetracker.repository.ProductRepository;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

class ScraperThread extends Thread {
    private final Product product;

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth.token}")
    private String AUTH_TOKEN ;

    public final ProductRepository productRepository;

    public ScraperThread(Product product, ProductRepository productRepository) {
        this.product = product;
        this.productRepository = productRepository;
    }

    public void run() {
        Document doc;
        try {
            doc = Jsoup.connect(this.product.url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();

            Elements priceSpan = doc.select(".a-price-whole");
            Elements productTitleElem = doc.select("#productTitle");

            String productTitle = productTitleElem.first().text();
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            int currentPrice = 0;
            try {
                currentPrice = format.parse(priceSpan.first().text()).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (product.prices.size() == 365) {
                product.prices.remove(0);
            }

            int flag = 0;

            for(Integer value: product.prices) {
                if (value <= currentPrice) {
                    flag = 1;
                    break;
                }
            }

            product.prices.add(currentPrice);
            this.productRepository.save(product);

            Message.creator(
                    new com.twilio.type.PhoneNumber("+32497526677"),
                    new com.twilio.type.PhoneNumber("+32460238693"),
                    ("Price for your tracked product " + product.name + " has dropped to - " + currentPrice)
            ).create();
            if (flag == 0) {

                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

                for (String user: product.usersTracking) {
                    Message.creator(
                            new com.twilio.type.PhoneNumber(user),
                            new com.twilio.type.PhoneNumber("+32460238693"),
                            ("Price for your tracked product " + product.name + " has dropped to - " + currentPrice)
                    ).create();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

@Component
class ProductScraperJob {

    @Autowired
    public ProductRepository productRepository;

    @Scheduled(cron = "0 0 * * * ?")
    public void scrapeProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            ScraperThread thread = new ScraperThread(product, productRepository);
            thread.start();
        }
    }
}