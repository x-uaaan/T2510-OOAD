run program using this command 
java -cp ".;javax.mail.jar;activation.jar" EventManagementApp

# University Event Management System

## Overview
This is a standalone Java Swing application for managing university events with CSV-based data persistence. It allows event organizers to create, update, and delete events, and enables participants to register for events with comprehensive fee breakdown and discount calculations. All data is automatically saved to CSV files for persistence between sessions.

## Features
- **Event Management**: Create, update, and delete events with full CRUD operations
- **CSV Data Persistence**: All data (events, users, registrations, payments) is automatically saved to CSV files
- **User Authentication**: Login/register system with email verification
- **Event Details**: name, organiser, description, date, time (08:00-24:00, 30-min intervals), venue, type, capacity, registration fee
- **Advanced Venue Selection**: Support for multiple venue types (MPH, DTC, Stadium, Faculty rooms, Lecture Halls)
- **Event List**: Modern card-style UI with real-time updates
- **Registration System**: 
  - Base registration fee with optional services (catering, transportation)
  - Multiple discount types (early bird, group registration, promo codes)
  - Detailed bill breakdown: base fee, services, discounts, total, net payable
  - Capacity tracking and registration management
- **Payment Processing**: Complete payment workflow with transaction tracking
- **Email Integration**: OTP verification and event notifications

## Data Storage Structure
The application uses a normalized CSV database structure:
- `csv/events.csv` - Event information with auto-generated event IDs
- `csv/users.csv` - User accounts and profiles
- `csv/registrations.csv` - Event registrations linking users to events
- `csv/payments.csv` - Payment records and transaction tracking

## Requirements
- Java 8 or higher
- Email libraries: `javax.mail.jar` and `activation.jar` (included)

## How to Run
1. Ensure you have Java installed (`java -version`).
2. Compile the application:
   ```sh
   javac EventManagementApp.java
   ```
3. Run the application with email support:
   ```sh
   java -cp ".;javax.mail.jar;activation.jar" EventManagementApp
   ```
   Or use the provided batch file:
   ```sh
   RUN_WITH_REAL_EMAIL.bat
   ```

## Data Persistence
- **Automatic Saving**: All data is automatically saved to CSV files when changes are made
- **Auto-loading**: Application loads existing data from CSV files on startup
- **Data Integrity**: Uses unique IDs for all entities (E0001 for events, U0001 for users, etc.)
- **Backup Safe**: CSV files are human-readable and can be backed up easily

## Registration & Payment Features
- **Multi-level Discounts**: Early bird (10%), group registration (15%), promo codes (variable)
- **Service Add-ons**: Catering (+RM20), Transportation (+RM10)
- **Capacity Management**: Real-time tracking of registered participants vs event capacity
- **Payment Methods**: Support for multiple payment methods with transaction tracking
- **Registration Status**: Pending, Confirmed, Cancelled status tracking

## Usage Notes
- First-time users need to register and verify their email address
- Event organizers can create events with complex venue configurations
- Time selection is limited to 08:00 to 24:00, with 30-minute intervals
- Registration includes comprehensive fee breakdown with all applicable discounts
- All data persists between application sessions via CSV storage
- CSV files are created automatically in the `csv/` directory on first use

## Extensibility
The codebase follows object-oriented principles and is designed for easy extension:
- **Modular CSV Managers**: Easy to add new data types
- **Strategy Pattern**: Discount calculation system can be extended
- **Factory Pattern**: Venue creation and ID generation
- **CRUD Operations**: Complete data management for all entities
- **Event Types**: Easily extensible event classification system

---

© University Event Management System - Now with Complete Data Persistence 