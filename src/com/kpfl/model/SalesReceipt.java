package com.kpfl.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SalesReceipt {
    private final String receiptNumber;
    private final String transactionType;
    private final LocalDateTime transactionDate;
    private final List<String> items;
    private final PaymentCalculator.PaymentSummary payment;

    public SalesReceipt(String transactionType, List<String> items,
                        PaymentCalculator.PaymentSummary payment) {
        if (transactionType == null || transactionType.trim().isEmpty()
                || items == null || payment == null) {
            throw new IllegalArgumentException("Receipt details are required.");
        }
        this.receiptNumber = "RCP-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();
        this.transactionType = transactionType;
        this.transactionDate = LocalDateTime.now();
        this.items = new ArrayList<String>(items);
        this.payment = payment;
    }

    public String getReceiptNumber() { return receiptNumber; }
    public String getTransactionType() { return transactionType; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public List<String> getItems() {
        return Collections.unmodifiableList(items);
    }
    public PaymentCalculator.PaymentSummary getPayment() { return payment; }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("\n========== FOOTBALL LEAGUE RECEIPT ==========\n");
        out.append("Receipt: ").append(receiptNumber).append("\n");
        out.append("Type: ").append(transactionType).append("\n");
        out.append("Date: ").append(transactionDate).append("\n");
        out.append("---------------------------------------------\n");
        for (String item : items) out.append(item).append("\n");
        out.append("---------------------------------------------\n");
        out.append("Subtotal: JMD ").append(payment.getSubtotal()).append("\n");
        out.append("GCT (15%): JMD ").append(payment.getTax()).append("\n");
        out.append("TOTAL: JMD ").append(payment.getTotal()).append("\n");
        out.append("Paid: JMD ").append(payment.getAmountTendered()).append("\n");
        out.append("Change: JMD ").append(payment.getChange()).append("\n");
        out.append("=============================================\n");
        return out.toString();
    }
}
