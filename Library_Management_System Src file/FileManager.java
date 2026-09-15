import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all text-file persistence.
 * A pipe character is used as the field separator.
 */
public final class FileManager {
    public static final Path BOOK_FILE = Paths.get("books.txt");
    public static final Path USER_FILE = Paths.get("users.txt");
    public static final Path TRANSACTION_FILE = Paths.get("transactions.txt");

    private FileManager() {
    }

    public static boolean bookFileExists() {
        return Files.exists(BOOK_FILE);
    }

    public static boolean userFileExists() {
        return Files.exists(USER_FILE);
    }

    public static boolean transactionFileExists() {
        return Files.exists(TRANSACTION_FILE);
    }

    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<Book>();
        if (!Files.exists(BOOK_FILE)) {
            return books;
        }

        try (BufferedReader reader = Files.newBufferedReader(BOOK_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length != 4) {
                    System.err.println("Skipping invalid book record: " + line);
                    continue;
                }
                books.add(new Book(
                        parts[0],
                        parts[1],
                        parts[2],
                        Integer.parseInt(parts[3])));
            }
        } catch (IOException | RuntimeException exception) {
            System.err.println("Could not load books: " + exception.getMessage());
        }
        return books;
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<User>();
        if (!Files.exists(USER_FILE)) {
            return users;
        }

        try (BufferedReader reader = Files.newBufferedReader(USER_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length != 5) {
                    System.err.println("Skipping invalid user record: " + line);
                    continue;
                }

                String role = parts[0].trim().toUpperCase();
                if ("ADMIN".equals(role)) {
                    users.add(new Admin(parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim()));
                } else {
                    users.add(new User(parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim()));
                }
            }
        } catch (IOException | RuntimeException exception) {
            System.err.println("Could not load users: " + exception.getMessage());
        }
        return users;
    }

    public static List<BorrowTransaction> loadTransactions() {
        List<BorrowTransaction> transactions = new ArrayList<BorrowTransaction>();
        if (!Files.exists(TRANSACTION_FILE)) {
            return transactions;
        }

        try (BufferedReader reader = Files.newBufferedReader(TRANSACTION_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length != 8) {
                    System.err.println("Skipping invalid transaction record: " + line);
                    continue;
                }

                LocalDate returnDate = parts[6].trim().isEmpty()
                        ? null
                        : LocalDate.parse(parts[6], DateUtils.FORMATTER);

                transactions.add(new BorrowTransaction(
                        parts[0],
                        parts[1],
                        parts[2],
                        Integer.parseInt(parts[3]),
                        LocalDate.parse(parts[4], DateUtils.FORMATTER),
                        LocalDate.parse(parts[5], DateUtils.FORMATTER),
                        returnDate,
                        Boolean.parseBoolean(parts[7])));
            }
        } catch (IOException | RuntimeException exception) {
            System.err.println("Could not load transactions: " + exception.getMessage());
        }
        return transactions;
    }

    public static void saveBooks(List<Book> books) {
        List<String> lines = new ArrayList<String>();
        for (Book book : books) {
            lines.add(clean(book.getBookId()) + "|"
                    + clean(book.getTitle()) + "|"
                    + clean(book.getAuthor()) + "|"
                    + book.getTotalCopies());
        }
        writeLinesSafely(BOOK_FILE, lines);
    }

    public static void saveUsers(List<User> users) {
        List<String> lines = new ArrayList<String>();
        for (User user : users) {
            lines.add(clean(user.getRole()) + "|"
                    + clean(user.getUserId()) + "|"
                    + clean(user.getName()) + "|"
                    + clean(user.getUsername()) + "|"
                    + clean(user.getPasswordForStorage()));
        }
        writeLinesSafely(USER_FILE, lines);
    }

    public static void saveTransactions(List<BorrowTransaction> transactions) {
        List<String> lines = new ArrayList<String>();
        for (BorrowTransaction transaction : transactions) {
            lines.add(clean(transaction.getTransactionId()) + "|"
                    + clean(transaction.getUserId()) + "|"
                    + clean(transaction.getBookId()) + "|"
                    + transaction.getCopyNumber() + "|"
                    + DateUtils.formatDate(transaction.getBorrowDate()) + "|"
                    + DateUtils.formatDate(transaction.getDueDate()) + "|"
                    + (transaction.getReturnDate() == null
                            ? ""
                            : DateUtils.formatDate(transaction.getReturnDate())) + "|"
                    + transaction.isReturned());
        }
        writeLinesSafely(TRANSACTION_FILE, lines);
    }

    public static void saveAll(
            List<Book> books,
            List<User> users,
            List<BorrowTransaction> transactions) {

        saveBooks(books);
        saveUsers(users);
        saveTransactions(transactions);
    }

    public static void createEmptyTransactionFile() {
        if (!Files.exists(TRANSACTION_FILE)) {
            writeLinesSafely(TRANSACTION_FILE, new ArrayList<String>());
        }
    }

    private static String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('|', '/').replace('\n', ' ').replace('\r', ' ').trim();
    }

    private static void writeLinesSafely(Path target, List<String> lines) {
        Path temporary = Paths.get(target.toString() + ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not write " + target + ": " + exception.getMessage(), exception);
        }

        try {
            Files.move(temporary, target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicMoveFailure) {
            try {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException fallbackFailure) {
                throw new IllegalStateException(
                        "Could not replace " + target + ": " + fallbackFailure.getMessage(),
                        fallbackFailure);
            }
        }
    }
}
