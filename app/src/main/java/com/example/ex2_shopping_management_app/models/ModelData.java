package com.example.ex2_shopping_management_app.models;

public class ModelData {
    private String productName;
    private int quantity;
    private int image;

    public ModelData() {}

    public ModelData(String productName, int quantity, int image) {
        this.productName = productName;
        this.quantity = quantity;
        this.image = image;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}

