# Java Console Projects

Beginner-friendly, standalone console applications that demonstrate core Java concepts.

## Movie Ticket Booking System

A console application that demonstrates 2D arrays, loops, methods, and input/condition handling.

### Features

- Displays a 5-row by 8-seat theatre map (`O` = available, `X` = booked).
- Books a selected seat and prevents double-booking.
- Cancels a booking and makes the seat available again.
- Automatically charges INR 150 on weekdays and INR 200 on Saturdays/Sundays, based on the computer's current date.
- Accepts coupon `MOVIE10` for 10% off a ticket.
- Collects movie ratings from 1 to 5 and displays the session average.

### Run

```bash
javac MovieTicketBookingSystem.java
java MovieTicketBookingSystem
```

Bookings and ratings are kept in memory for the duration of the program; they reset when the application exits. This sample simulates booking and does not process payments or refunds.

## Personal Expense Tracker

A menu-driven Java program for recording and analyzing personal expenses.

### Features

- Adds expenses with a positive INR amount, description, category, and date.
- Changes the category of an existing expense and deletes an expense by its ID.
- Displays all expenses in date order.
- Calculates the total and highest expense.
- Calculates totals for each category.
- Uses `ArrayList`, `HashMap`, the `LocalDate` Date/Time API, Streams, and an enum for expense categories.

### Run

```bash
javac PersonalExpenseTracker.java
java PersonalExpenseTracker
```

Expenses are held in memory for the duration of the program and reset when it exits. Enter dates in `YYYY-MM-DD` format, or press Enter to use today's date.
