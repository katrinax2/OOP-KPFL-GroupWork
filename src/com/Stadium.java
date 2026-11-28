import java.io.Serializable;

public class Stadium implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum FieldCondition {
        EXCELLENT("Excellent"), GOOD("Good"), FAIR("Fair"), POOR("Poor");

        private final String label;
        FieldCondition(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    private final String stadiumId;
    private String name;
    private String location;
    private int capacity;
    private FieldCondition fieldCondition;
    private double ticketPrice;

    public Stadium(String stadiumId, String name, String location, int capacity,
                   FieldCondition fieldCondition, double ticketPrice) {
        if (stadiumId == null || stadiumId.isBlank()) {
            throw new IllegalArgumentException("Stadium ID is required.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Stadium name is required.");
        }
        this.stadiumId = stadiumId;
        this.name = name;
        this.location = location;
        setCapacity(capacity);
        setFieldCondition(fieldCondition);
        setTicketPrice(ticketPrice);
    }

    public String getStadiumId() { return stadiumId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public FieldCondition getFieldCondition() { return fieldCondition; }
    public double getTicketPrice() { return ticketPrice; }

    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Seating capacity must be greater than zero.");
        }
        this.capacity = capacity;
    }

    public void setFieldCondition(FieldCondition fieldCondition) {
        if (fieldCondition == null) {
            throw new IllegalArgumentException("Field condition is required.");
        }
        this.fieldCondition = fieldCondition;
    }

    public void setTicketPrice(double ticketPrice) {
        if (ticketPrice < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative.");
        }
        this.ticketPrice = ticketPrice;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s) | capacity %d | field: %s | ticket: $%.2f",
                stadiumId, name, location, capacity, fieldCondition, ticketPrice);
    }
}
