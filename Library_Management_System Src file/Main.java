import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the Library Management System.
 */
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            DataInitializer.initializeIfNeeded();

            LibraryManager manager = new LibraryManager(
                    FileManager.loadBooks(),
                    FileManager.loadUsers(),
                    FileManager.loadTransactions());

            runMainMenu(manager);
        } catch (RuntimeException exception) {
            System.err.println("The application could not start: " + exception.getMessage());
        } finally {
            SCANNER.close();
        }
    }

    private static void runMainMenu(LibraryManager manager) {
        boolean running = true;

        while (running) {
            printHeader("LIBRARY MANAGEMENT SYSTEM");
            System.out.println("1. Admin Login");
            System.out.println("2. User Login");
            System.out.println("0. Exit");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    loginAsAdmin(manager);
                    break;
                case 2:
                    loginAsUser(manager);
                    break;
                case 0:
                    running = false;
                    System.out.println("Thank you for using the library system.");
                    break;
                default:
                    System.out.println("Invalid menu option.");
            }
        }
    }

    private static void loginAsAdmin(LibraryManager manager) {
        printHeader("ADMIN LOGIN");
        String username = readNonEmpty("Username: ");
        String password = readNonEmpty("Password: ");

        User account = manager.authenticate(username, password, "ADMIN");
        if (account == null) {
            System.out.println("Invalid admin username or password.");
            return;
        }

        System.out.println("Welcome, " + account.getName() + ".");
        runAdminMenu(manager, (Admin) account);
    }

    private static void loginAsUser(LibraryManager manager) {
        printHeader("USER LOGIN");
        String username = readNonEmpty("Username: ");
        String password = readNonEmpty("Password: ");

        User account = manager.authenticate(username, password, "USER");
        if (account == null) {
            System.out.println("Invalid user username or password.");
            return;
        }

        System.out.println("Welcome, " + account.getName() + ".");
        runUserMenu(manager, account);
    }

    private static void runAdminMenu(LibraryManager manager, Admin admin) {
        boolean loggedIn = true;

        while (loggedIn) {
            printHeader("ADMIN MENU - " + admin.getName());
            System.out.println("1. View All Books");
            System.out.println("2. Search Books");
            System.out.println("3. Add Book");
            System.out.println("4. Update Book");
            System.out.println("5. Delete Book");
            System.out.println("6. View All Accounts");
            System.out.println("7. Add Account");
            System.out.println("8. Update Account");
            System.out.println("9. Delete Account");
            System.out.println("10. View All Transactions");
            System.out.println("11. View Active Reservations");
            System.out.println("12. Return Book for a User");
            System.out.println("13. Check Book Availability");
            System.out.println("0. Logout");

            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1:
                        printBooks(manager, manager.getAllBooks());
                        break;
                    case 2:
                        searchBooks(manager);
                        break;
                    case 3:
                        addBook(manager);
                        break;
                    case 4:
                        updateBook(manager);
                        break;
                    case 5:
                        deleteBook(manager);
                        break;
                    case 6:
                        printUsers(manager.getAllUsers());
                        break;
                    case 7:
                        addAccount(manager);
                        break;
                    case 8:
                        updateAccount(manager);
                        break;
                    case 9:
                        deleteAccount(manager, admin);
                        break;
                    case 10:
                        printTransactions(manager.getAllTransactions());
                        break;
                    case 11:
                        printTransactions(manager.getActiveTransactions());
                        break;
                    case 12:
                        adminReturnBook(manager, admin);
                        break;
                    case 13:
                        checkAvailability(manager);
                        break;
                    case 0:
                        loggedIn = false;
                        System.out.println("Logged out successfully.");
                        break;
                    default:
                        System.out.println("Invalid menu option.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
    }

    private static void runUserMenu(LibraryManager manager, User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            printHeader("USER MENU - " + user.getName());
            System.out.println("1. View All Books");
            System.out.println("2. Search Books");
            System.out.println("3. Borrow/Reserve Book");
            System.out.println("4. Return Book");
            System.out.println("5. View My Active Reservations");
            System.out.println("6. View My Transaction History");
            System.out.println("7. Check Book Availability");
            System.out.println("0. Logout");

            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1:
                        printBooks(manager, manager.getAllBooks());
                        break;
                    case 2:
                        searchBooks(manager);
                        break;
                    case 3:
                        borrowBook(manager, user);
                        break;
                    case 4:
                        userReturnBook(manager, user);
                        break;
                    case 5:
                        printTransactions(manager.getActiveTransactionsForUser(user.getUserId()));
                        break;
                    case 6:
                        printTransactions(manager.getTransactionsForUser(user.getUserId()));
                        break;
                    case 7:
                        checkAvailability(manager);
                        break;
                    case 0:
                        loggedIn = false;
                        System.out.println("Logged out successfully.");
                        break;
                    default:
                        System.out.println("Invalid menu option.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
    }

    private static void searchBooks(LibraryManager manager) {
        String keyword = readNonEmpty("Enter book ID, title, or author: ");
        printBooks(manager, manager.searchBooks(keyword));
    }

    private static void addBook(LibraryManager manager) {
        printHeader("ADD BOOK");
        String id = readNonEmpty("Book ID: ");
        String title = readNonEmpty("Title: ");
        String author = readNonEmpty("Author: ");
        int copies = readPositiveInt("Total copies: ");
        System.out.println(manager.addBook(id, title, author, copies));
    }

    private static void updateBook(LibraryManager manager) {
        printHeader("UPDATE BOOK");
        String id = readNonEmpty("Book ID to update: ");
        Book existing = manager.findBookById(id);
        if (existing == null) {
            System.out.println("Book not found.");
            return;
        }

        System.out.println("Current: " + existing);
        String title = readNonEmpty("New title: ");
        String author = readNonEmpty("New author: ");
        int copies = readPositiveInt("New total copies: ");
        System.out.println(manager.updateBook(id, title, author, copies));
    }

    private static void deleteBook(LibraryManager manager) {
        printHeader("DELETE BOOK");
        String id = readNonEmpty("Book ID: ");
        if (confirm("Delete this book? (y/n): ")) {
            System.out.println(manager.deleteBook(id));
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void addAccount(LibraryManager manager) {
        printHeader("ADD ACCOUNT");
        String role;
        while (true) {
            role = readNonEmpty("Role (ADMIN/USER): ").toUpperCase();
            if ("ADMIN".equals(role) || "USER".equals(role)) {
                break;
            }
            System.out.println("Role must be ADMIN or USER.");
        }

        String id = readNonEmpty("Account ID: ");
        String name = readNonEmpty("Full name: ");
        String username = readNonEmpty("Username: ");
        String password = readNonEmpty("Password: ");
        System.out.println(manager.addUser(role, id, name, username, password));
    }

    private static void updateAccount(LibraryManager manager) {
        printHeader("UPDATE ACCOUNT");
        String id = readNonEmpty("Account ID: ");
        User existing = manager.findUserById(id);
        if (existing == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("Current: " + existing);
        String name = readNonEmpty("New full name: ");
        String username = readNonEmpty("New username: ");
        String password = readNonEmpty("New password: ");
        System.out.println(manager.updateUser(id, name, username, password));
    }

    private static void deleteAccount(LibraryManager manager, Admin admin) {
        printHeader("DELETE ACCOUNT");
        String id = readNonEmpty("Account ID: ");
        if (confirm("Delete this account? (y/n): ")) {
            System.out.println(manager.deleteUser(id, admin.getUserId()));
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void borrowBook(LibraryManager manager, User user) {
        printHeader("BORROW / RESERVE BOOK");
        String bookId = readNonEmpty("Book ID: ");
        LocalDate startDate = readDate("Borrow date (yyyy-MM-dd): ");
        LocalDate dueDate = readDate("Due/availability date (yyyy-MM-dd): ");

        System.out.println(manager.borrowBook(
                user.getUserId(),
                bookId,
                startDate,
                dueDate));
    }

    private static void userReturnBook(LibraryManager manager, User user) {
        printHeader("RETURN BOOK");
        List<BorrowTransaction> active = manager.getActiveTransactionsForUser(user.getUserId());
        printTransactions(active);
        if (active.isEmpty()) {
            return;
        }

        String transactionId = readNonEmpty("Transaction ID to return: ");
        System.out.println(manager.returnBook(transactionId, user.getUserId(), false));
    }

    private static void adminReturnBook(LibraryManager manager, Admin admin) {
        printHeader("ADMIN RETURN BOOK");
        List<BorrowTransaction> active = manager.getActiveTransactions();
        printTransactions(active);
        if (active.isEmpty()) {
            return;
        }

        String transactionId = readNonEmpty("Transaction ID to return: ");
        System.out.println(manager.returnBook(transactionId, admin.getUserId(), true));
    }

    private static void checkAvailability(LibraryManager manager) {
        printHeader("CHECK AVAILABILITY");
        String bookId = readNonEmpty("Book ID: ");
        LocalDate startDate = readDate("Start date (yyyy-MM-dd): ");
        LocalDate dueDate = readDate("End/availability date (yyyy-MM-dd): ");
        System.out.println(manager.checkAvailabilityMessage(bookId, startDate, dueDate));
    }

    private static void printBooks(LibraryManager manager, List<Book> books) {
        printHeader("BOOK LIST");
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        System.out.println("ID     | Title                        | Author                 | Available Today | Total");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Book book : books) {
            int availableToday = manager.getAvailableCopyCount(
                    book.getBookId(), today, tomorrow);
            System.out.printf(
                    "%-6s | %-28s | %-22s | %-15d | %d%n",
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    availableToday,
                    book.getTotalCopies());
        }
    }

    private static void printUsers(List<User> users) {
        printHeader("ACCOUNT LIST");
        if (users.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.println("ID     | Name                   | Username       | Role");
        System.out.println("---------------------------------------------------------------");
        for (User user : users) {
            System.out.println(user);
        }
    }

    private static void printTransactions(List<BorrowTransaction> transactions) {
        printHeader("TRANSACTION LIST");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        for (BorrowTransaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            String value = readNonEmpty(prompt);
            try {
                return DateUtils.parseDate(value);
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Please enter a number greater than zero.");
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private static boolean confirm(String prompt) {
        System.out.print(prompt);
        String answer = SCANNER.nextLine().trim();
        return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("============================================================");
        System.out.println(title);
        System.out.println("============================================================");
    }
}
