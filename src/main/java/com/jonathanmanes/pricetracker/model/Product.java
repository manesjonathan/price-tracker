package com.jonathanmanes.pricetracker.model;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Product")
public class Product {
    @Id
    public String id;

    public String name;
    public String url;
    public List<Integer>prices;
    public List<String>usersTracking;

    public Product () {}

    public Product (String name, String url, Integer price, String userTracking) {
        super();
        this.name = name;
        this.url = url;
        this.prices = new ArrayList<>();
        this.usersTracking = new ArrayList<>();
        this.prices.add(price);
        this.usersTracking.add(userTracking);
    }
}