package com.kpfl.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PaymentCalculator {
    public static final BigDecimal GCT_RATE = new BigDecimal("0.15");

    private PaymentCalculator() { }

    public static PaymentSummary calculate(BigDecimal subtotal, BigDecimal amountTendered) {
        if (subtotal == null || amountTendered == null) {
            throw new IllegalArgumentException("Payment values cannot be null.");
        }
        if (subtotal.signum() < 0 || amountTendered.signum() < 0) {
            throw new IllegalArgumentException("Payment values cannot be negative.");
        }

        BigDecimal roundedSubtotal = money(subtotal);
        BigDecimal tax = money(roundedSubtotal.multiply(GCT_RATE));
        BigDecimal total = money(roundedSubtotal.add(tax));
        BigDecimal tendered = money(amountTendered);

        if (tendered.compareTo(total) < 0) {
            throw new IllegalArgumentException(
                "Insufficient payment. Total due is JMD " + total
            );
        }

        BigDecimal change = money(tendered.subtract(total));
        return new PaymentSummary(roundedSubtotal, tax, total, tendered, change);
    }

    public static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public static final class PaymentSummary {
        private final BigDecimal subtotal, tax, total, amountTendered, change;

        private PaymentSummary(BigDecimal subtotal, BigDecimal tax, BigDecimal total,
                               BigDecimal amountTendered, BigDecimal change) {
            this.subtotal = subtotal;
            this.tax = tax;
            this.total = total;
            this.amountTendered = amountTendered;
            this.change = change;
        }

        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getTax() { return tax; }
        public BigDecimal getTotal() { return total; }
        public BigDecimal getAmountTendered() { return amountTendered; }
        public BigDecimal getChange() { return change; }
    }
}


