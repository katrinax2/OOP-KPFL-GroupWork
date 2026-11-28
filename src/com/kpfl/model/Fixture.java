package com.kpfl.model;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fixture implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DURATION_MINUTES = 120;

    public enum Status { SCHEDULED, COMPLETED, CANCELLED }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final String fixtureId;
    private final FootballTeam homeTeam;
    private final FootballTeam awayTeam;
    private Stadium stadium;
    private LocalDateTime kickoff;
    private Status status = Status.SCHEDULED;
    private MatchResult result;
    private final List<MatchOfficial> officials = new ArrayList<>();

    public Fixture(String fixtureId, FootballTeam homeTeam, FootballTeam awayTeam,
                   Stadium stadium, LocalDateTime kickoff) {
        this.fixtureId = fixtureId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.stadium = stadium;
        this.kickoff = kickoff;
    }

    public String getFixtureId() { return fixtureId; }
    public FootballTeam getHomeTeam() { return homeTeam; }
    public FootballTeam getAwayTeam() { return awayTeam; }
    public Stadium getStadium() { return stadium; }
    public LocalDateTime getKickoff() { return kickoff; }
    public LocalDateTime getEnd() { return kickoff.plusMinutes(DURATION_MINUTES); }
    public Status getStatus() { return status; }
    public MatchResult getResult() { return result; }
    public List<MatchOfficial> getOfficials() { return Collections.unmodifiableList(officials); }

    void setStadium(Stadium stadium) { this.stadium = stadium; }
    void setKickoff(LocalDateTime kickoff) { this.kickoff = kickoff; }
    void addOfficial(MatchOfficial official) { officials.add(official); }
    boolean removeOfficial(MatchOfficial official) { return officials.remove(official); }
    void cancel() { this.status = Status.CANCELLED; }

    void complete(MatchResult result) {
        this.result = result;
        this.status = Status.COMPLETED;
    }

    public boolean involvesTeam(FootballTeam team) {
        return homeTeam.getTeamId().equals(team.getTeamId())
                || awayTeam.getTeamId().equals(team.getTeamId());
    }

    public boolean hasOfficial(MatchOfficial official) {
        return officials.stream().anyMatch(o -> o.getId().equals(official.getId()));
    }

    public boolean hasReferee() {
        return officials.stream().anyMatch(o -> o.getRole() == MatchOfficial.Role.REFEREE);
    }

    public boolean overlapsWith(LocalDateTime otherStart) {
        LocalDateTime otherEnd = otherStart.plusMinutes(DURATION_MINUTES);
        return kickoff.isBefore(otherEnd) && otherStart.isBefore(getEnd());
    }

    public String getFormattedKickoff() { return kickoff.format(FMT); }

    @Override
    public String toString() {
        return String.format("%s | %s vs %s | %s | %s | %s",
                fixtureId, homeTeam.getTeamName(), awayTeam.getTeamName(),
                stadium.getName(), getFormattedKickoff(), status);
    }
}
