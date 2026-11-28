package com.kpfl.controller;

import com.kpfl.exception.KPFLValidationException;
import com.kpfl.model.LeagueCommissioner;
import com.kpfl.model.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LeagueController {
    private final Scanner scanner;
    private final List<Person> peopleDirectory;
    private LeagueCommissioner commissioner;

    public LeagueController() {
        this.scanner = new Scanner(System.in);
        this.peopleDirectory = new ArrayList<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        try {
            this.commissioner = new LeagueCommissioner(
                    "COM-001", "Devon Clarke", 46, "Male", "Level 1 - Executive", 6);
            this.peopleDirectory.add(commissioner);
        } catch (KPFLValidationException e) {
            System.out.println("[Startup Error] " + e.getMessage());
        }
    }

    public void start() {
        boolean running = true;
        System.out.println("====================================================");
        System.out.println("  KINGSTON PREMIER FOOTBALL LEAGUE (KPFL) SYSTEM");
        System.out.println("====================================================");

        while (running) {
            printMainMenu();
            int choice = readInt("Select an option: ");

            switch (choice) {
                case 1:
                    handleCommissionerMenu();
                    break;
                case 2:
                    displayPeopleDirectory();
                    break;
                case 3:
                    System.out.println("\n[Teams & Personnel Module] Awaiting Member 1 integration (FootballTeam, Player, Coach).");
                    break;
                case 4:
                    System.out.println("\n[Stadiums, Fixtures & Standings Module] Awaiting Member 3 integration (Stadium, Fixture, MatchOfficial).");
                    break;
                case 5:
                    System.out.println("\n[Match-Day Ticketing & Stadium Shop Module] Awaiting Member 4 integration (Ticket, StadiumShop).");
                    break;
                case 6:
                    System.out.println("\n[Reports & Persistence Module] Awaiting Member 3/4 integration (Reports & File Save/Load).");
                    break;
                case 0:
                    System.out.println("\nExiting KPFL Management System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[Invalid Choice] Please select a valid menu option (0-6).");
            }
        }
        scanner.close();
    }

    private void printMainMenu() {
        System.out.println("\n-------------------- MAIN MENU ---------------------");
        System.out.println("1. League Commissioner Operations");
        System.out.println("2. View All Registered People (Polymorphic Directory)");
        System.out.println("3. Teams, Players & Coaches Management");
        System.out.println("4. Stadiums, Fixtures & League Standings");
        System.out.println("5. Match-Day Ticketing & Stadium Shop");
        System.out.println("6. Management Reports & Save/Load Data");
        System.out.println("0. Exit System");
        System.out.println("----------------------------------------------------");
    }

    private void handleCommissionerMenu() {
        boolean inSubMenu = true;
        while (inSubMenu) {
            System.out.println("\n----------- LEAGUE COMMISSIONER OPERATIONS -----------");
            System.out.println("Active Commissioner: " + commissioner.getName() + " (" + commissioner.getClearanceLevel() + ")");
            System.out.println("1. View Commissioner Profile");
            System.out.println("2. Log Match Preparation Inspection");
            System.out.println("3. View Inspection & Admin Logs");
            System.out.println("4. Update Clearance Level");
            System.out.println("0. Back to Main Menu");
            System.out.println("------------------------------------------------------");

            int choice = readInt("Select an option: ");
            try {
                switch (choice) {
                    case 1:
                        System.out.println("\n" + commissioner);
                        break;
                    case 2:
                        String fixtureId = readNonEmptyString("Enter Fixture ID (e.g., FIX-101): ");
                        String stadiumName = readNonEmptyString("Enter Stadium Name (e.g., National Stadium): ");
                        boolean passed = readBoolean("Did the match preparation pass inspection? (yes/no): ");
                        System.out.print("Enter inspection notes (optional): ");
                        String notes = scanner.nextLine();

                        commissioner.inspectMatchPreparation(fixtureId, stadiumName, passed, notes);
                        System.out.println("[Success] Match preparation inspection logged.");
                        break;
                    case 3:
                        List<String> logs = commissioner.getInspectionLogs();
                        System.out.println("\n--- Commissioner Inspection & Admin Logs ---");
                        if (logs.isEmpty()) {
                            System.out.println("No logs recorded yet.");
                        } else {
                            for (int i = 0; i < logs.size(); i++) {
                                System.out.println((i + 1) + ". " + logs.get(i));
                            }
                        }
                        break;
                    case 4:
                        String newClearance = readNonEmptyString("Enter new clearance level: ");
                        commissioner.setClearanceLevel(newClearance);
                        System.out.println("[Success] Clearance level updated.");
                        break;
                    case 0:
                        inSubMenu = false;
                        break;
                    default:
                        System.out.println("\n[Invalid Choice] Please select 0-4.");
                }
            } catch (KPFLValidationException e) {
                System.out.println("\n[Validation Error] " + e.get