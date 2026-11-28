public class MatchOfficial extends Person {
    private static final long serialVersionUID = 1L;

    public enum Role {
        REFEREE("Referee"),
        ASSISTANT_REFEREE("Assistant Referee"),
        FOURTH_OFFICIAL("Fourth Official");

        private final String label;
        Role(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    public enum Rank {
        TRAINEE("Trainee"), REGIONAL("Regional"), NATIONAL("National"), INTERNATIONAL("International");

        private final String label;
        Rank(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    private Role role;
    private Rank rank;
    private boolean available = true;

    public MatchOfficial(String id, String name, int age, String gender, Role role, Rank rank) {
        super(id, name, age, gender);
        setRole(role);
        setRank(rank);
    }

    public Role getRole() { return role; }
    public Rank getRank() { return rank; }
    public boolean isAvailable() { return available; }

    public void setRole(Role role) {
        if (role == null) throw new IllegalArgumentException("Official role is required.");
        this.role = role;
    }

    public void setRank(Rank rank) {
        if (rank == null) throw new IllegalArgumentException("Official rank is required.");
        this.rank = rank;
    }

    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String displayDetails() {
        return String.format("%s | %s | Age %d | %s | %s, %s | %s",
                getId(), getName(), getAge(), getGender(), role, rank,
                available ? "Available" : "Unavailable");
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", getId(), getName(), role);
    }
}
