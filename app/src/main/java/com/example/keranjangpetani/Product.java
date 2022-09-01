package com.example.keranjangpetani;

import android.net.Uri;

public class Product {

    String uuid;
    String title;
    String price;
    String description;
    Uri image;

    public Product() {}

    public Product(String title, String price, String description, Uri image, String userID) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.image = image;
        this.uuid = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
