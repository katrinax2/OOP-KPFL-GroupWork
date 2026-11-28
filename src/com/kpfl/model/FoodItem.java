package com.kpfl.model;


import java.math.BigDecimal;
import java.time.LocalDate;

public class FoodItem extends Product {
    private LocalDate expiryDate;

    public FoodItem(String productId, String productName, BigDecimal unitPrice,
                    int quantityInStock, LocalDate expiryDate) {
        super(productId, productName, unitPrice, quantityInStock);
        setExpiryDate(expiryDate);
    }

    public LocalDate getExpiryDate() { return expiryDate; }

    public void setExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null) throw new IllegalArgumentException("Expiry date is required.");
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String getCategory() { return "Food and Refreshments"; }

    @Override
    public String toString() {
        return super.toString() + " | Expiry: " + expiryDate
                + " | Expired: " + isExpired();
    }
}
