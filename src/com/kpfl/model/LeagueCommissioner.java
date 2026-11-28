package com.kpfl.model;

import com.kpfl.exception.KPFLValidationException;
import java.util.ArrayList;
import java.util.List;

public class LeagueCommissioner extends Person {
    private static final long serialVersionUID = 1L;

    private String clearanceLevel;
    private int yearsInOffice;
    private List<String> inspectionLogs;

    public LeagueCommissioner(String id, String name, int age, String gender, String clearanceLevel, int yearsInOffice)
            throws KPFLValidationException {
        super(id, name, age, gender);
        if (age < 25) {
            throw new KPFLValidationException("League Commissioner must be at least 25 years old.");
        }
        setClearanceLevel(clearanceLevel);
        setYearsInOffice(yearsInOffice);
        this.inspectionLogs = new ArrayList<>();
    }

    // Core Commissioner Operations (Connects to  FootballTeam & Fixture classes)
     public void approveTeamRegistration(FootballTeam team) throws KPFLValidationException {
          if (team == null) {
              throw new KPFLValidationException("Team cannot be null.");
         }
         team.setLeagueStatus("APPROVED");
          logAction("Approved registration for team: " + team.getName());
      }
     
      public void suspendTeam(FootballTeam team, String reason) throws KPFLValidationException {
          if (team == null) {
              throw new KPFLValidationException("Team cannot be null.");
          }
          team.setLeagueStatus("SUSPENDED");
          logAction("Suspended team " + team.getName() + " | Reason: " + reason);
      }
     
      public void authorizeOfficialAssignment(Fixture fixture, MatchOfficial official) throws KPFLValidationException {
          if (fixture == null || official == null) {
              throw new KPFLValidationException("Fixture and MatchOfficial cannot be null.");
          }
         fixture.assignOfficial(official);
         logAction("Assigned Official " + official.getName() + " to Fixture " + fixture.getFixtureId());
      }
     

    public void inspectMatchPreparation(String fixtureId, String stadiumName, boolean passed, String notes)
            throws KPFLValidationException {
        if (fixtureId == null || fixtureId.trim().isEmpty()) {
            throw new KPFLValidationException("Fixture ID cannot be empty for inspection.");
        }
        if (stadiumName == null || stadiumName.trim().isEmpty()) {
            throw new KPFLValidationException("Stadium name cannot be empty for inspection.");
        }
        String status = passed ? "PASSED" : "FAILED";
        String cleanNotes = (notes == null || notes.trim().isEmpty()) ? "No additional notes" : notes.trim();
        String log = String.format("Fixture %s @ %s -> %s (%s)",
                fixtureId.trim(), stadiumName.trim(), status, cleanNotes);
        inspectionLogs.add(log);
    }

    private void logAction(String action) {
        inspectionLogs.add("[ADMIN ACTION] " + action);
    }

    public List<String> getInspectionLogs() {
        return new ArrayList<>(inspectionLogs);
    }

    public String getClearanceLevel() {
        return clearanceLevel;
    }

    public void setClearanceLevel(String clearanceLevel) throws KPFLValidationException {
        if (clearanceLevel == null || clearanceLevel.trim().isEmpty()) {
            throw new KPFLValidationException("Clearance level cannot be empty.");
        }
        this.clearanceLevel = clearanceLevel.trim();
    }

    public int getYearsInOffice() {
        return yearsInOffice;
    }

    public void setYearsInOffice(int yearsInOffice) throws KPFLValidationException {
        if (yearsInOffice < 0) {
            throw new KPFLValidationException("Years in office cannot be negative.");
        }
        this.yearsInOffice = yearsInOffice;
    }

    @Override
    public String getRole() {
        return "League Commissioner";
    }

    @Override
    public String getSummaryDetails() {
        return String.format("Clearance: %s | Tenure: %d yrs | Inspections Logged: %d",
                clearanceLevel, yearsInOffice, inspectionLogs.size());
    }
}
