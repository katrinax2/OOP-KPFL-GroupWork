package com.kpfl.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TicketSalesManager {
    private final List<Ticket> soldTickets = new ArrayList<Ticket>();
    private final List<SalesReceipt> receipts = new ArrayList<SalesReceipt>();

    public synchronized int getTicketsSold(Fixture fixture) {
        requireFixture(fixture);
        int count = 0;
        for (Ticket ticket : soldTickets) {
            if (ticket.getFixture() == fixture) count++;
        }
        return count;
    }

    public synchronized int getAvailableSeats(Fixture fixture) {
        requireFixture(fixture);
        return Math.max(0, fixture.getStadium().getCapacity() - getTicketsSold(fixture));
    }

    public synchronized SalesReceipt sellTickets(Spectator spectator, Fixture fixture,
            int quantity, BigDecimal ticketPrice, BigDecimal amountTendered) {
        if (spectator == null) throw new IllegalArgumentException("Spectator is required.");
        requireFixture(fixture);
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        if (ticketPrice == null || ticketPrice.signum() < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative.");
        }
        if (quantity > getAvailableSeats(fixture)) {
            throw new IllegalStateException("Not enough seats. Available seats: "
                    + getAvailableSeats(fixture));
        }

        BigDecimal subtotal = PaymentCalculator.money(ticketPrice)
                .multiply(BigDecimal.valueOf(quantity));
        PaymentCalculator.PaymentSummary payment =
                PaymentCalculator.calculate(subtotal, amountTendered);

        List<Ticket> pendingTickets = new ArrayList<Ticket>();
        List<String> receiptLines = new ArrayList<String>();
        for (int i = 0; i < quantity; i++) {
            Ticket ticket = new Ticket(spectator, fixture, fixture.getStadium(), ticketPrice);
            pendingTickets.add(ticket);
            receiptLines.add(ticket.toString());
        }

        // Commit records only after capacity and payment checks succeed.
        soldTickets.addAll(pendingTickets);
        SalesReceipt receipt = new SalesReceipt("TICKET SALE", receiptLines, payment);
        receipts.add(receipt);
        return receipt;
    }

    public synchronized List<Ticket> getSoldTickets() {
        return Collections.unmodifiableList(new ArrayList<Ticket>(soldTickets));
    }

    public synchronized List<SalesReceipt> getReceipts() {
        return Collections.unmodifiableList(new ArrayList<SalesReceipt>(receipts));
    }

    private void requireFixture(Fixture fixture) {
        if (fixture == null || fixture.getStadium() == null) {
            throw new IllegalArgumentException("A valid fixture and its stadium are required.");
        }
        if (fixture.getStadium().getCapacity() <= 0) {
            throw new IllegalStateException("Stadium capacity must be positive.");
        }
    }
}


