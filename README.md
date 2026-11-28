# Kingston Premier Football League (KPFL) Management System

## Project Description

Welcome to the **Kingston Premier Football League (KPFL) Management System**, an Object-Oriented Programming (OOP) group project built in Java. The main objective of this application is to simulate and manage the core operations of a professional football league through an interactive console menu system.

In this system, football teams compete across scheduled fixtures hosted at designated stadiums. The league involves multiple  roles—**League Commissioners, Players, Coaches, Match Officials, and Spectators**—each handled through specialized behaviors and validation rules. Teams can be registered, approved, or suspended by the League Commissioner; fixtures are scheduled without stadium or official conflicts; match results automatically update league standings; and spectators can purchase match tickets (validated against stadium capacity) as well as stadium shop items billed with **15% General Consumption Tax (GCT)**.

Unlike single-file procedural programs, this project separates responsibilities across multiple interacting classes and packages (`controller`, `model`, `service`, `exception`, and `util`). Proper encapsulation, access modifiers (`private`, `protected`, `public`), inheritance, abstraction, polymorphism, and custom exception handling (`KPFLValidationException`) are enforced throughout the codebase to preserve data integrity and modularity.

---

## Class Hierarchy & Project Structure

The project is organized into clean packages under `src/com/kpfl/`:

### 1. Entry Point & Controller
* `Main.java` (`com.kpfl`) – Bootstraps the application, initializes `LeagueController`, and launches the main console menu loop.
* `LeagueController.java` (`com.kpfl.controller`) – Handles user input, manages the polymorphic `List<Person>` directory, catches validation exceptions, and routes requests across all league modules.

### 2. Core Models (`com.kpfl.model`)
* **Person Hierarchy (Abstraction & Polymorphism):**
  * `Person.java` *(abstract)* – Base class defining common attributes (`id`, `name`, `age`, `gender`) and abstract methods (`getRole()`, `getSummaryDetails()`).
  * `LeagueCommissioner.java` – Approves/suspends teams, authorizes match officials, and logs stadium preparation inspections.
  * `Player.java` – Represents registered squad players with jersey numbers, positions, and match statistics.
  * `Coach.java` – Represents team coaching staff and licensing details.
  * `MatchOfficial.java` – Represents referees/officials assigned to fixtures.
  * `Spectator.java` – Represents fans purchasing match tickets and stadium shop items.
* **League, Match & Commerce Entities:**
  * `FootballLeague.java` – Central league container holding teams, fixtures, stadiums, and standings.
  * `FootballTeam.java` – Represents a registered club, its roster, approval status, and league points.
  * `Stadium.java` – Stores venue details, location, and seating capacity.
  * `Fixture.java` – Connects home and away teams, a stadium, date/time, and assigned match officials.
  * `MatchResult.java` – Records final scores and drives points calculation (`Win = 3`, `Draw = 1`, `Loss = 0`).
  * `Ticket.java` – Represents a match ticket linked to a `Spectator`, `Fixture`, and `Stadium`.
  * `Product.java` *(abstract)*, `Merchandise.java`, `FoodItem.java` – Product inheritance hierarchy for stadium shop inventory.
  * `SalesReceipt.java` – Stores itemized transaction records, subtotal, 15% GCT, total due, and change.

### 3. Services, Exceptions & Utilities
* `TeamService.java` (`com.kpfl.service`) – Manages team registration and roster operations.
* `FixtureService.java` (`com.kpfl.service`) – Handles conflict-free fixture scheduling, result entry, and league table sorting.
* `TicketSalesManager.java` (`com.kpfl.service`) – Validates seat availability and issues tickets and receipts.
* `PaymentCalculator.java` (`com.kpfl.service`) – Computes subtotals, 15% GCT, totals, and customer change using `BigDecimal`.
* `StadiumShop.java` (`com.kpfl.service`) – Manages stadium merchandise and food sales.
* `ReportService.java` (`com.kpfl.service`) – Generates standings, fixture, and financial summary reports.
* `FileStorageService.java` (`com.kpfl.service`) – Saves and reloads league state to and from `data/runtime/`.
* `KPFLValidationException.java` (`com.kpfl.exception`) – Custom checked exception for domain and business-rule violations.
* `InputValidator.java` (`com.kpfl.util`) – Reusable console input validation helpers.

---

## Directory Layout

```text
KPFL_Management_System/
├── README.md
├── .gitignore
├── data/
│   ├── sample/          # Pre-populated sample records for demonstration
│   └── runtime/         # Saved runtime data generated during program execution
└── src/
    └── com/
        └── kpfl/
            ├── Main.java
            ├── controller/
            ├── exception/
            ├── model/
            ├── service/
            └── util/



## Group Members
Katrina Watson - 2404855