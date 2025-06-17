# University Event Management System

## Overview
This is a standalone Java Swing application for managing university events. It allows event organizers to create, update, and delete events, and enables participants to view event details and register for events with fee breakdown and discount calculations.

## Features
- Create, update, and delete events
- Event details: name, organiser, description, date, time (08:00-24:00, 30-min intervals), venue, type, capacity, registration fee
- Event list with modern card-style UI
- Click event to view details, register, and see fee breakdown
- Registration supports:
  - Base registration fee
  - Optional services (catering, transportation)
  - Discounts (early bird, group)
  - Detailed bill: base fee, services, discounts, total, net payable
- All user interactions via Java Swing GUI

## Requirements
- Java 8 or higher
- No external libraries required (no database, no third-party dependencies)

## How to Run
1. Ensure you have Java installed (`java -version`).
2. Compile the application:
   ```sh
   javac EventManagementApp.java
   ```
3. Run the application:
   ```sh
   java EventManagementApp
   ```

## Usage Notes
- All event and registration data is stored in memory only. Data will be lost when the application is closed.
- Time selection for events is limited to 08:00 to 24:00, with minutes only 00 or 30.
- Registration fee breakdown includes optional services and discounts:
  - Early bird: 10% off if registering 7+ days before event
  - Group: 15% off if registering 5 or more participants (pax)
- The UI is designed for clarity and ease of use, following university event management requirements.

## Extensibility
- The codebase is structured for easy extension (e.g., adding persistent storage, more event types, or additional registration features).

---

© University Event Management System 