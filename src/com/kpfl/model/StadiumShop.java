package com.kpfl.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StadiumShop {
    private final Map<String, Product> inventory = new LinkedHashMap<String, Product>();
    private final List<SalesReceipt> receipts = new ArrayList<SalesReceipt>();

    public synchronized void addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product is required.");
        if (inventory.containsKey(product.getProductId())) {
            throw new IllegalArgumentException("Product ID already exists: " + product.getProductId());
        }
        inventory.put(product.getProductId(), product);
    }

    public synchronized Product getProduct(String productId) {
        Product product = inventory.get(productId);
        if (product == null) throw new IllegalArgumentException("Product not found: " + productId);
        return product;
    }

    public synchronized void updatePrice(String productId, BigDecimal newPrice) {
        getProduct(productId).setUnitPrice(newPrice);
    }

    public synchronized void restockProduct(String productId, int quantity) {
        getProduct(productId).restock(quantity);
    }

    public synchronized boolean removeProduct(String productId) {
        return inventory.remove(productId) != null;
    }

    public synchronized List<Product> getAllProducts() {
        return Collections.unmodifiableList(
                new ArrayList<Product>(inventory.values()));
    }

    public synchronized List<SalesReceipt> getReceipts() {
        return Collections.unmodifiableList(new ArrayList<SalesReceipt>(receipts));
    }

    public synchronized SalesReceipt processSale(Map<String, Integer> cart,
                                                  BigDecimal amountTendered) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Shopping cart cannot be empty.");
        }

        // Validate all lines and calculate subtotal before changing any stock.
        Map<Product, Integer> validatedItems = new LinkedHashMap<Product, Integer>();
        List<String> receiptLines = new ArrayList<String>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> line : cart.entrySet()) {
            String id = line.getKey();
            Integer quantity = line.getValue();
            if (id == null || quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Each cart line needs a product ID and positive quantity.");
            }

            Product product = getProduct(id);
            if (!product.hasStock(quantity)) {
                throw new IllegalStateException("Insufficient stock for " + product.getProductName()
                        + ". Available: " + product.getQuantityInStock());
            }
            if (product instanceof FoodItem && ((FoodItem) product).isExpired()) {
                throw new IllegalStateException(product.getProductName() + " has expired and cannot be sold.");
            }

            BigDecimal lineTotal = product.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
            subtotal = subtotal.add(lineTotal);
            validatedItems.put(product, quantity);
            receiptLines.add(product.getProductName() + " x " + quantity
                    + " @ JMD " + product.getUnitPrice() + " = JMD "
                    + PaymentCalculator.money(lineTotal));
        }

        PaymentCalculator.PaymentSummary payment =
                PaymentCalculator.calculate(subtotal, amountTendered);

        // Deduct stock only after every item and the payment have passed validation.
        for (Map.Entry<Product, Integer> item : validatedItems.entrySet()) {
            item.getKey().deductStock(item.getValue());
        }

        SalesReceipt receipt = new SalesReceipt("STADIUM SHOP SALE", receiptLines, 	);
        receipts.add(receipt);
        return receipt;
    }

    public synchronized void displayInventory() {
        System.out.println("\n--- STADIUM SHOP INVENTORY ---");
        if (inventory.isEmpty()) {
            System.out.println("No products registered.");
            return;
        }
        for (Product product : inventory.values()) System.out.println(product);
    }
}
