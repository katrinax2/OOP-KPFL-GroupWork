package com.kpfl.model;

import java.math.BigDecimal;

public class Merchandise extends Product {
    private String size;

    public Merchandise(String productId, String productName, BigDecimal unitPrice,
                       int quantityInStock, String size) {
        super(productId, productName, unitPrice, quantityInStock);
        setSize(size);
    }

    public String getSize() { return size; }

    public void setSize(String size) {
        if (size == null || size.trim().isEmpty()) {
            throw new IllegalArgumentException("Merchandise size is required.");
        }
        this.size = size.trim();
    }

    @Override
    public String getCategory() { return "Merchandise"; }

    @Override
    public String toString() { return super.toString() + " | Size: " + size; }
}

