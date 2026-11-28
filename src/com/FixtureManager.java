import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FixtureManager {

    private static final int MAX_ASSISTANTS = 2;

    private final List<Stadium> stadiums = new ArrayList<>();
    private final List<MatchOfficial> officials = new ArrayList<>();
    private final List<Fixture> fixtures = new ArrayList<>();

    public void registerStadium(Stadium stadium) {
        if (stadium == null) throw new IllegalArgumentException("Stadium is required.");
        if (findStadium(stadium.getStadiumId()).isPresent()) {
            throw new IllegalArgumentException("Stadium ID already exists: " + stadium.getStadiumId());
        }
        stadiums.add(stadium);
    }

    public Optional<Stadium> findStadium(String id) {
        return stadiums.stream().filter(s -> s.getStadiumId().equalsIgnoreCase(id)).findFirst();
    }

    public List<Stadium> getStadiums() { return Collections.unmodifiableList(stadiums); }

    public void registerOfficial(MatchOfficial official) {
        if (official == null) throw new IllegalArgumentException("Official is required.");
        if (findOfficial(official.getId()).isPresent()) {
            throw new IllegalArgumentException("Official ID already exists: " + official.getId());
        }
        officials.add(official);
    }

    public Optional<MatchOfficial> findOfficial(String id) {
        return officials.stream().filter(o -> o.getId().equalsIgnoreCase(id)).findFirst();
    }

    public List<MatchOfficial> getOfficials() { return Collections.unmodifiableList(officials); }

    public Fixture createFixture(String fixtureId, FootballTeam home, FootballTeam away,
                                 Stadium stadium, LocalDateTime kickoff) throws FixtureRuleException {

        if (fixtureId == null || fixtureId.isBlank()) {
            throw new IllegalArgumentException("Fixture ID is required.");
        }
        if (findFixture(fixtureId).isPresent()) {
            throw new IllegalArgumentException("Fixture ID already exists: " + fixtureId);
        }
        if (home == null || away == null || stadium == null || kickoff == null) {
            throw new IllegalArgumentException("Teams, stadium and kickoff time are all required.");
        }
        if (home.getTeamId().equals(away.getTeamId())) {
            throw new SchedulingConflictException("A team cannot play against itself.");
        }

        checkEligible(home);
        checkEligible(away);
        checkTeamsAndStadium(null, home, away, stadium, kickoff);

        Fixture fixture = new Fixture(fixtureId, home, away, stadium, kickoff);
        fixtures.add(fixture);
        return fixture;
    }

    public void rescheduleFixture(String fixtureId, Stadium newStadium, LocalDateTime newKickoff)
            throws FixtureRuleException {

        if (newStadium == null || newKickoff == null) {
            throw new IllegalArgumentException("Stadium and kickoff time are required.");
        }
        Fixture fixture = requireScheduled(fixtureId);

        checkEligible(fixture.getHomeTeam());
        checkEligible(fixture.getAwayTeam());
        checkTeamsAndStadium(fixture, fixture.getHomeTeam(), fixture.getAwayTeam(), newStadium, newKickoff);
        for (MatchOfficial official : fixture.getOfficials()) {
            checkOfficialFree(official, newKickoff, fixture);
        }

        fixture.setStadium(newStadium);
        fixture.setKickoff(newKickoff);
    }

    public void cancelFixture(String fixtureId) throws FixtureRuleException {
        requireScheduled(fixtureId).cancel();
    }

    public void completeFixture(String fixtureId, MatchResult result) throws FixtureRuleException {
        Fixture fixture = requireScheduled(fixtureId);
        if (result == null) {
            throw new InvalidFixtureStateException("Fixture " + fixtureId
                    + " cannot be completed until a valid result has been recorded.");
        }
        fixture.complete(result);
    }

    public Optional<Fixture> findFixture(String id) {
        return fixtures.stream().filter(f -> f.getFixtureId().equalsIgnoreCase(id)).findFirst();
    }

    public List<Fixture> getFixtures() { return Collections.unmodifiableList(fixtures); }

    public List<Fixture> getFixturesForTeam(FootballTeam team) {
        List<Fixture> result = new ArrayList<>();
        for (Fixture f : fixtures) {
            if (f.involvesTeam(team)) result.add(f);
        }
        result.sort(Comparator.comparing(Fixture::getKickoff));
        return result;
    }

    public List<Fixture> getFixturesForOfficial(MatchOfficial official) {
        List<Fixture> result = new ArrayList<>();
        for (Fixture f : fixtures) {
            if (f.hasOfficial(official)) result.add(f);
        }
        result.sort(Comparator.comparing(Fixture::getKickoff));
        return result;
    }

    public List<Fixture> getFixturesOnDate(LocalDate date) {
        List<Fixture> result = new ArrayList<>();
        for (Fixture f : fixtures) {
            if (f.getKickoff().toLocalDate().equals(date)) result.add(f);
        }
        result.sort(Comparator.comparing(Fixture::getKickoff));
        return result;
    }

    public void assignOfficial(String fixtureId, String officialId) throws FixtureRuleException {
        Fixture fixture = requireScheduled(fixtureId);
        MatchOfficial official = findOfficial(officialId)
                .orElseThrow(() -> new IllegalArgumentException("No official with ID " + officialId));

        if (fixture.hasOfficial(official)) {
            throw new SchedulingConflictException(official.getName() + " is already assigned to this fixture.");
        }
        if (!official.isAvailable()) {
            throw new SchedulingConflictException(official.getName() + " is marked unavailable.");
        }
        checkRoleSlotAvailable(fixture, official.getRole());
        checkOfficialFree(official, fixture.getKickoff(), fixture);

        fixture.addOfficial(official);
    }

    public void removeOfficialFromFixture(String fixtureId, String officialId) throws FixtureRuleException {
        Fixture fixture = requireScheduled(fixtureId);
        MatchOfficial official = findOfficial(officialId)
                .orElseThrow(() -> new IllegalArgumentException("No official with ID " + officialId));
        if (!fixture.removeOfficial(official)) {
            throw new IllegalArgumentException(official.getName() + " is not assigned to this fixture.");
        }
    }

    public void loadData(List<Stadium> loadedStadiums, List<MatchOfficial> loadedOfficials,
                         List<Fixture> loadedFixtures) {
        stadiums.clear();
        officials.clear();
        fixtures.clear();
        stadiums.addAll(loadedStadiums);
        officials.addAll(loadedOfficials);
        fixtures.addAll(loadedFixtures);
    }

    private void checkEligible(FootballTeam team) throws IneligibleTeamException {
        if (team.getLeagueStatus() != FootballTeam.LeagueStatus.REGISTERED) {
            throw new IneligibleTeamException(team.getTeamName() + " is "
                    + team.getLeagueStatus() + " and cannot be assigned to a fixture.");
        }
    }

    private void checkTeamsAndStadium(Fixture ignore, FootballTeam home, FootballTeam away,
                                      Stadium stadium, LocalDateTime kickoff)
            throws SchedulingConflictException {

        for (Fixture existing : fixtures) {
            if (existing == ignore || existing.getStatus() != Fixture.Status.SCHEDULED) continue;
            if (!existing.overlapsWith(kickoff)) continue;

            if (existing.involvesTeam(home)) {
                throw new SchedulingConflictException(home.getTeamName()
                        + " already has a fixture at that time (" + existing.getFixtureId() + ").");
            }
            if (existing.involvesTeam(away)) {
                throw new SchedulingConflictException(away.getTeamName()
                        + " already has a fixture at that time (" + existing.getFixtureId() + ").");
            }
            if (existing.getStadium().getStadiumId().equals(stadium.getStadiumId())) {
                throw new SchedulingConflictException(stadium.getName()
                        + " is already booked at that time (" + existing.getFixtureId() + ").");
            }
        }
    }

    private void checkOfficialFree(MatchOfficial official, LocalDateTime kickoff, Fixture ignore)
            throws SchedulingConflictException {

        for (Fixture existing : fixtures) {
            if (existing == ignore || existing.getStatus() != Fixture.Status.SCHEDULED) continue;
            if (existing.hasOfficial(official) && existing.overlapsWith(kickoff)) {
                throw new SchedulingConflictException(official.getName()
                        + " is already assigned to " + existing.getFixtureId() + " at that time.");
            }
        }
    }

    private void checkRoleSlotAvailable(Fixture fixture, MatchOfficial.Role role)
            throws SchedulingConflictException {

        long count = fixture.getOfficials().stream().filter(o -> o.getRole() == role).count();
        int max = (role == MatchOfficial.Role.ASSISTANT_REFEREE) ? MAX_ASSISTANTS : 1;
        if (count >= max) {
            throw new SchedulingConflictException("This fixture already has the maximum number of "
                    + role + "(s): " + max + ".");
        }
    }

    private Fixture requireScheduled(String fixtureId) throws InvalidFixtureStateException {
        Fixture fixture = findFixture(fixtureId)
                .orElseThrow(() -> new IllegalArgumentException("No fixture with ID " + fixtureId));
        if (fixture.getStatus() != Fixture.Status.SCHEDULED) {
            throw new InvalidFixtureStateException("Fixture " + fixtureId + " is "
                    + fixture.getStatus() + " and cannot be changed.");
        }
        return fixture;
    }

    public String generateFixtureReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================== FIXTURE REPORT ===================\n");
        if (fixtures.isEmpty()) {
            sb.append("No fixtures have been created.\n");
        } else {
            List<Fixture> sorted = new ArrayList<>(fixtures);
            sorted.sort(Comparator.comparing(Fixture::getKickoff));
            for (Fixture f : sorted) {
                sb.append(String.format("%-8s %s vs %s%n", f.getFixtureId(),
                        f.getHomeTeam().getTeamName(), f.getAwayTeam().getTeamName()));
                sb.append(String.format("         Venue   : %s (%s)%n", f.getStadium().getName(), f.getStadium().getLocation()));
                sb.append(String.format("         Kickoff : %s%n", f.getFormattedKickoff()));
                sb.append(String.format("         Status  : %s%n", f.getStatus()));
                if (f.getResult() != null) {
                    sb.append(String.format("         Result  : %d - %d%n",
                            f.getResult().getHomeScore(), f.getResult().getAwayScore()));
                }
                sb.append(String.format("         Officials: %s%n%n",
                        f.getOfficials().isEmpty() ? "none assigned" : officialNames(f)));
            }
        }
        long scheduled = fixtures.stream().filter(f -> f.getStatus() == Fixture.Status.SCHEDULED).count();
        long completed = fixtures.stream().filter(f -> f.getStatus() == Fixture.Status.COMPLETED).count();
        long cancelled = fixtures.stream().filter(f -> f.getStatus() == Fixture.Status.CANCELLED).count();
        sb.append(String.format("Total: %d | Scheduled: %d | Completed: %d | Cancelled: %d%n",
                fixtures.size(), scheduled, completed, cancelled));
        return sb.toString();
    }

    public String generateOfficialsReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("================== OFFICIALS REPORT ==================\n");
        if (officials.isEmpty()) {
            sb.append("No match officials registered.\n");
        }
        for (MatchOfficial o : officials) {
            List<Fixture> assigned = getFixturesForOfficial(o);
            sb.append(String.format("%s | %d fixture(s)%n", o.displayDetails(), assigned.size()));
            for (Fixture f : assigned) {
                sb.append(String.format("    %s: %s vs %s, %s%n", f.getFixtureId(),
                        f.getHomeTeam().getTeamName(), f.getAwayTeam().getTeamName(), f.getFormattedKickoff()));
            }
        }
        sb.append("\nScheduled fixtures with no referee: ");
        boolean any = false;
        for (Fixture f : fixtures) {
            if (f.getStatus() == Fixture.Status.SCHEDULED && !f.hasReferee()) {
                sb.append(any ? ", " : "").append(f.getFixtureId());
                any = true;
            }
        }
        sb.append(any ? "\n" : "none\n");
        return sb.toString();
    }

    public String generateStadiumReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================== STADIUM REPORT ===================\n");
        if (stadiums.isEmpty()) {
            sb.append("No stadiums registered.\n");
        }
        for (Stadium s : stadiums) {
            long upcoming = fixtures.stream()
                    .filter(f -> f.getStatus() == Fixture.Status.SCHEDULED
                            && f.getStadium().getStadiumId().equals(s.getStadiumId()))
                    .count();
            sb.append(String.format("%s | scheduled fixtures: %d%n", s, upcoming));
        }
        return sb.toString();
    }

    private String officialNames(Fixture f) {
        StringBuilder names = new StringBuilder();
        for (MatchOfficial o : f.getOfficials()) {
            if (names.length() > 0) names.append(", ");
            names.append(o.getName()).append(" (").append(o.getRole()).append(")");
        }
        return names.toString();
    }
}
