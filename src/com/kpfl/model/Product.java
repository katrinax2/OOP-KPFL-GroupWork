package com.kpfl.model;

import java.math.BigDecimal;

public abstract class Product {
    private final String productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantityInStock;

    protected Product(String productId, String productName,
                      BigDecimal unitPrice, int quantityInStock) {
        this.productId = requireText(productId, "Product ID");
        this.productName = requireText(productName, "Product name");
        setUnitPrice(unitPrice);
        if (quantityInStock < 0) throw new IllegalArgumentException("Stock cannot be negative.");
        this.quantityInStock = quantityInStock;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }

    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantityInStock() { return quantityInStock; }
    public abstract String getCategory();

    public boolean hasStock(int quantity) {
        return quantity > 0 && quantityInStock >= quantity;
    }

    public void restock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Restock quantity must be positive.");
        quantityInStock = Math.addExact(quantityInStock, quantity);
    }

    public void setProductName(String productName) {
        this.productName = requireText(productName, "Product name");
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be null or negative.");
        }
        this.unitPrice = PaymentCalculator.money(unitPrice);
    }

    // Package-private: inventory deduction is controlled by StadiumShop.
    void deductStock(int quantity) {
        if (!hasStock(quantity)) throw new IllegalStateException("Insufficient stock.");
        quantityInStock -= quantity;
    }

    @Override
    public String toString() {
        return productId + " | " + productName + " | " + getCategory()
                + " | JMD " + unitPrice + " | Stock: " + quantityInStock;
    }
}
