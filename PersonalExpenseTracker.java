import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A console-based personal expense tracker.
 * Expenses are kept in memory and are cleared when the program exits.
 */
public class PersonalExpenseTracker {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final ArrayList<Expense> expenses = new ArrayList<>();
    private static int nextExpenseId = 1;

    public static void main(String[] args) {
        System.out.println("=== Personal Expense Tracker ===");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ", 1, 8);

            switch (choice) {
                case 1:
                    addExpense();
                    break;
                case 2:
                    categorizeExpense();
                    break;
                case 3:
                    deleteExpense();
                    break;
                case 4:
                    displayAllExpenses();
                    break;
                case 5:
                    displayTotalExpense();
                    break;
                case 6:
                    displayHighestExpense();
                    break;
                case 7:
                    displayCategoryTotals();
                    break;
                case 8:
                    running = false;
                    System.out.println("Your expenses were kept for this session only. Goodbye!");
                    break;
                default:
                    // readInt keeps the choice within the menu range.
                    System.out.println("Invalid option.");
            }
            System.out.println();
        }

        INPUT.close();
    }

    private static void printMenu() {
        System.out.println("\n1. Add an expense");
        System.out.println("2. Change an expense category");
        System.out.println("3. Delete an expense");
        System.out.println("4. Display all expenses");
        System.out.println("5. Calculate total expenses");
        System.out.println("6. Find the highest expense");
        System.out.println("7. Calculate category-wise expenses");
        System.out.println("8. Exit");
    }

    private static void addExpense() {
        System.out.println("\n--- Add expense ---");
        BigDecimal amount = readAmount();
        String description = readNonEmptyLine("Description: ");
        ExpenseCategory category = readCategory();
        LocalDate date = readDate();

        Expense expense = new Expense(nextExpenseId++, amount, description, category, date);
        expenses.add(expense);
        System.out.println("Added expense #" + expense.getId() + " for " + formatMoney(amount) + ".");
    }

    private static void categorizeExpense() {
        if (!requireExpenses()) {
            return;
        }

        System.out.println("\nSelect the expense to recategorize:");
        displayAllExpenses();
        Expense expense = findExpenseById(readExpenseId());
        if (expense == null) {
            System.out.println("No expense was found with that ID.");
            return;
        }

        System.out.println("Current category: " + expense.getCategory());
        expense.setCategory(readCategory());
        System.out.println("Expense #" + expense.getId() + " is now categorized as "
                + expense.getCategory() + ".");
    }

    private static void deleteExpense() {
        if (!requireExpenses()) {
            return;
        }

        System.out.println("\nSelect the expense to delete:");
        displayAllExpenses();
        Expense expense = findExpenseById(readExpenseId());
        if (expense == null) {
            System.out.println("No expense was found with that ID.");
            return;
        }

        expenses.remove(expense);
        System.out.println("Deleted expense #" + expense.getId() + ".");
    }

    private static void displayAllExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses have been recorded yet.");
            return;
        }

        System.out.println("\nID   DATE         CATEGORY             AMOUNT (INR)  DESCRIPTION");
        System.out.println("--------------------------------------------------------------------------");
        expenses.stream()
                .sorted(Comparator.comparing(Expense::getDate).thenComparingInt(Expense::getId))
                .forEach(expense -> System.out.printf("%-4d %-12s %-20s %12s  %s%n",
                        expense.getId(),
                        expense.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        expense.getCategory(),
                        formatMoney(expense.getAmount()),
                        expense.getDescription()));
    }

    private static void displayTotalExpense() {
        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total expenses across " + expenses.size() + " entr"
                + (expenses.size() == 1 ? "y" : "ies") + ": " + formatMoney(total));
    }

    private static void displayHighestExpense() {
        Expense highest = expenses.stream()
                .max(Comparator.comparing(Expense::getAmount))
                .orElse(null);

        if (highest == null) {
            System.out.println("No expenses have been recorded yet.");
            return;
        }

        System.out.println("Highest expense: #" + highest.getId() + " - "
                + highest.getDescription() + " (" + highest.getCategory() + ", "
                + highest.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + ") - "
                + formatMoney(highest.getAmount()));
    }

    private static void displayCategoryTotals() {
        HashMap<ExpenseCategory, BigDecimal> totals = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        HashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));

        System.out.println("\nCategory-wise expenses:");
        for (ExpenseCategory category : ExpenseCategory.values()) {
            System.out.printf("%-20s %s%n", category, formatMoney(totals.getOrDefault(
                    category, BigDecimal.ZERO)));
        }
    }

    private static boolean requireExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses have been recorded yet.");
            return false;
        }
        return true;
    }

    private static Expense findExpenseById(int id) {
        return expenses.stream()
                .filter(expense -> expense.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private static BigDecimal readAmount() {
        while (true) {
            String text = readLine("Amount in INR (up to 2 decimal places): ").trim();
            try {
                BigDecimal amount = new BigDecimal(text).setScale(2, RoundingMode.UNNECESSARY);
                if (amount.signum() > 0) {
                    return amount;
                }
            } catch (NumberFormatException | ArithmeticException ignored) {
                // Show the same helpful message for malformed amounts and excess decimal places.
            }
            System.out.println("Enter a positive amount with no more than 2 decimal places.");
        }
    }

    private static String readNonEmptyLine(String prompt) {
        while (true) {
            String value = readLine(prompt).trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This value cannot be empty.");
        }
    }

    private static ExpenseCategory readCategory() {
        ExpenseCategory[] categories = ExpenseCategory.values();
        System.out.println("Categories:");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        int choice = readInt("Choose a category: ", 1, categories.length);
        return categories[choice - 1];
    }

    private static LocalDate readDate() {
        while (true) {
            String text = readLine("Date (YYYY-MM-DD, press Enter for today): ").trim();
            if (text.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException exception) {
                System.out.println("Invalid date. Use the YYYY-MM-DD format, for example 2026-10-03.");
            }
        }
    }

    private static int readExpenseId() {
        return readInt("Expense ID: ", 1, Integer.MAX_VALUE);
    }

    private static int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            String text = readLine(prompt).trim();
            try {
                int value = Integer.parseInt(text);
                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Keep prompting until the user enters an integer in range.
            }
            System.out.println("Please enter a whole number from " + minimum + " to " + maximum + ".");
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return INPUT.nextLine();
    }

    private static String formatMoney(BigDecimal amount) {
        return "INR " + amount.setScale(2, RoundingMode.UNNECESSARY).toPlainString();
    }

    private enum ExpenseCategory {
        FOOD("Food & Dining"),
        TRANSPORT("Transport"),
        HOUSING("Housing"),
        UTILITIES("Utilities"),
        HEALTHCARE("Healthcare"),
        EDUCATION("Education"),
        ENTERTAINMENT("Entertainment"),
        SHOPPING("Shopping"),
        OTHER("Other");

        private final String displayName;

        ExpenseCategory(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private static final class Expense {
        private final int id;
        private final BigDecimal amount;
        private final String description;
        private ExpenseCategory category;
        private final LocalDate date;

        private Expense(int id, BigDecimal amount, String description,
                        ExpenseCategory category, LocalDate date) {
            this.id = id;
            this.amount = amount;
            this.description = description;
            this.category = category;
            this.date = date;
        }

        private int getId() {
            return id;
        }

        private BigDecimal getAmount() {
            return amount;
        }

        private String getDescription() {
            return description;
        }

        private ExpenseCategory getCategory() {
            return category;
        }

        private void setCategory(ExpenseCategory category) {
            this.category = category;
        }

        private LocalDate getDate() {
            return date;
        }
    }
}
