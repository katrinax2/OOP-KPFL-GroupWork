package com.kpfl.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket {
    private final String ticketId;
    private final Spectator spectator;
    private final Fixture fixture;
    private final Stadium stadium;
    private final BigDecimal ticketPrice;
    private final LocalDateTime purchaseDate;

    public Ticket(Spectator spectator, Fixture fixture, Stadium stadium, BigDecimal ticketPrice) {
        if (spectator == null || fixture == null || stadium == null) {
            throw new IllegalArgumentException("Spectator, fixture and stadium are required.");
        }
        if (ticketPrice == null || ticketPrice.signum() < 0) {
            throw new IllegalArgumentException("Ticket price cannot be null or negative.");
        }
        if (fixture.getStadium() != stadium) {
            throw new IllegalArgumentException("The stadium does not match this fixture.");
        }
        this.ticketId = "TKT-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();
        this.spectator = spectator;
        this.fixture = fixture;
        this.stadium = stadium;
        this.ticketPrice = PaymentCalculator.money(ticketPrice);
        this.purchaseDate = LocalDateTime.now();
    }

    public String getTicketId() { return ticketId; }
    public Spectator getSpectator() { return spectator; }
    public Fixture getFixture() { return fixture; }
    public Stadium getStadium() { return stadium; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }

    @Override
    public String toString() {
        return ticketId + " | " + spectator.getFullName() + " | "
                + fixture.getFixtureId() + " | JMD " + ticketPrice;
    }
}

