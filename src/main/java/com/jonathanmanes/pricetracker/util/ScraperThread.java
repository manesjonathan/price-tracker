package com.jonathanmanes.pricetracker.util;

import com.jonathanmanes.pricetracker.model.Product;
import com.jonathanmanes.pricetracker.repository.ProductRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScraperThread extends Thread {

    private final Product product;

    private final String TWILIO_PHONE_NUMBER;

    private final String ACCOUNT_SID;

    private final String AUTH_TOKEN;

    private final ProductRepository productRepository;

    public ScraperThread(Product product, ProductRepository productRepository, String AUTH_TOKEN, String ACCOUNT_SID, String TWILIO_PHONE_NUMBER) {
        this.AUTH_TOKEN = AUTH_TOKEN;
        this.ACCOUNT_SID = ACCOUNT_SID;
        this.TWILIO_PHONE_NUMBER = TWILIO_PHONE_NUMBER;
        this.product = product;
        this.productRepository = productRepository;
    }

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect(this.product.url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();

            Elements priceSpan = doc.select(".a-price-whole");
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            int currentPrice = 0;
            try {
                currentPrice = format.parse(Objects.requireNonNull(priceSpan.first()).text()).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (product.prices.size() == 365) {
                product.prices.remove(0);
            }

            int flag = 0;

            for (Integer value : product.prices) {
                if (value <= currentPrice) {
                    flag = 1;
                    break;
                }
            }

            product.prices.add(currentPrice);
            this.productRepository.save(product);


            //if (flag == 0) {

            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            for (String user : product.usersTracking) {

                Message.creator(
                        new com.twilio.type.PhoneNumber(user),
                        new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),
                        ("Price for your tracked product " + product.name + " has dropped to - " + currentPrice)
                ).create();
            }
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

@Component
@RequiredArgsConstructor
class ProductScraperJob {

    @Resource(name = "productRepository")
    private final ProductRepository productRepository;

    @Value("${custom.twilio.phone.number}")
    private String TWILIO_PHONE_NUMBER;

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth.token}")
    private String AUTH_TOKEN;

    @Scheduled(cron = "0 * * * * ?")
    public void scrapeProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            ScraperThread thread = new ScraperThread(product, productRepository, AUTH_TOKEN, ACCOUNT_SID, TWILIO_PHONE_NUMBER);
            thread.start();
        }
    }
}