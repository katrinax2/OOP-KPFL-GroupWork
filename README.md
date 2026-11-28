# Kingston Premier Football League (KPFL) Management System

An object-oriented Java application built to manage league administration, team registrations, match fixtures, stadium operations, ticketing, and stadium shop sales for the Kingston Premier Football League.

---

## Project Structure

```text
KPFL-Management-System/
├── docs/
│   ├── design/          # Requirements & Design document, UML class diagrams
│   ├── planning/        # Weekly schedule, task breakdown, contribution log
│   └── guides/          # User guide and module integration notes
├── data/
│   ├── sample/          # Pre-populated sample records for testing and demo
│   └── runtime/         # Saved state generated during program execution
└── src/
    └── com/kpfl/
        ├── Main.java            # Application entry point
        ├── controller/          # Main menu and cross-module routing (LeagueController)
        ├── model/               # Core entities (Person, LeagueCommissioner, Player, Team, etc.)
        ├── service/             # Business logic, scheduling, standings, file persistence
        ├── exception/           # Custom validation and business-rule exceptions
        └── util/                # Shared input validation and helper utilities

