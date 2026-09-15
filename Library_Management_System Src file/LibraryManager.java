import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Contains the main business logic of the Library Management System.
 */
public class LibraryManager {
    private final List<Book> books;
    private final List<User> users;
    private final List<BorrowTransaction> transactions;

    public LibraryManager(
            List<Book> books,
            List<User> users,
            List<BorrowTransaction> transactions) {

        this.books = new ArrayList<Book>(books);
        this.users = new ArrayList<User>(users);
        this.transactions = new ArrayList<BorrowTransaction>(transactions);
    }

    public User authenticate(String username, String password, String expectedRole) {
        if (username == null || password == null) {
            return null;
        }

        for (User user : users) {
            boolean usernameMatches = user.getUsername().equalsIgnoreCase(username.trim());
            boolean passwordMatches = user.checkPassword(password);
            boolean roleMatches = expectedRole == null
                    || user.getRole().equalsIgnoreCase(expectedRole);

            if (usernameMatches && passwordMatches && roleMatches) {
                return user;
            }
        }
        return null;
    }

    public List<Book> getAllBooks() {
        List<Book> copy = new ArrayList<Book>(books);
        Collections.sort(copy, Comparator.comparing(Book::getBookId));
        return copy;
    }

    public List<User> getAllUsers() {
        List<User> copy = new ArrayList<User>(users);
        Collections.sort(copy, Comparator.comparing(User::getUserId));
        return copy;
    }

    public List<BorrowTransaction> getAllTransactions() {
        List<BorrowTransaction> copy = new ArrayList<BorrowTransaction>(transactions);
        Collections.sort(copy, Comparator.comparing(BorrowTransaction::getTransactionId));
        return copy;
    }

    public List<BorrowTransaction> getTransactionsForUser(String userId) {
        List<BorrowTransaction> result = new ArrayList<BorrowTransaction>();
        for (BorrowTransaction transaction : transactions) {
            if (transaction.getUserId().equalsIgnoreCase(userId)) {
                result.add(transaction);
            }
        }
        Collections.sort(result, Comparator.comparing(BorrowTransaction::getBorrowDate).reversed());
        return result;
    }

    public List<BorrowTransaction> getActiveTransactionsForUser(String userId) {
        List<BorrowTransaction> result = new ArrayList<BorrowTransaction>();
        for (BorrowTransaction transaction : transactions) {
            if (transaction.getUserId().equalsIgnoreCase(userId)
                    && !transaction.isReturned()) {
                result.add(transaction);
            }
        }
        Collections.sort(result, Comparator.comparing(BorrowTransaction::getBorrowDate));
        return result;
    }

    public List<BorrowTransaction> getActiveTransactions() {
        List<BorrowTransaction> result = new ArrayList<BorrowTransaction>();
        for (BorrowTransaction transaction : transactions) {
            if (!transaction.isReturned()) {
                result.add(transaction);
            }
        }
        Collections.sort(result, Comparator.comparing(BorrowTransaction::getDueDate));
        return result;
    }

    public Book findBookById(String bookId) {
        if (bookId == null) {
            return null;
        }
        for (Book book : books) {
            if (book.getBookId().equalsIgnoreCase(bookId.trim())) {
                return book;
            }
        }
        return null;
    }

    public User findUserById(String userId) {
        if (userId == null) {
            return null;
        }
        for (User user : users) {
            if (user.getUserId().equalsIgnoreCase(userId.trim())) {
                return user;
            }
        }
        return null;
    }

    public BorrowTransaction findTransactionById(String transactionId) {
        if (transactionId == null) {
            return null;
        }
        for (BorrowTransaction transaction : transactions) {
            if (transaction.getTransactionId().equalsIgnoreCase(transactionId.trim())) {
                return transaction;
            }
        }
        return null;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> result = new ArrayList<Book>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String normalized = keyword.trim().toLowerCase();
        for (Book book : books) {
            if (book.getBookId().toLowerCase().contains(normalized)
                    || book.getTitle().toLowerCase().contains(normalized)
                    || book.getAuthor().toLowerCase().contains(normalized)) {
                result.add(book);
            }
        }
        Collections.sort(result, Comparator.comparing(Book::getTitle));
        return result;
    }

    public String addBook(String bookId, String title, String author, int totalCopies) {
        if (findBookById(bookId) != null) {
            return "A book with this ID already exists.";
        }

        books.add(new Book(bookId, title, author, totalCopies));
        saveAll();
        return "Book added successfully.";
    }

    public String updateBook(
            String bookId,
            String newTitle,
            String newAuthor,
            int newTotalCopies) {

        Book book = findBookById(bookId);
        if (book == null) {
            return "Book not found.";
        }

        int highestReservedCopy = getHighestActiveCopyNumber(bookId);
        if (newTotalCopies < highestReservedCopy) {
            return "Cannot reduce copies below " + highestReservedCopy
                    + " because higher-numbered copies are currently reserved.";
        }

        book.setTitle(newTitle);
        book.setAuthor(newAuthor);
        book.setTotalCopies(newTotalCopies);
        saveAll();
        return "Book updated successfully.";
    }

    public String deleteBook(String bookId) {
        Book book = findBookById(bookId);
        if (book == null) {
            return "Book not found.";
        }

        for (BorrowTransaction transaction : transactions) {
            if (transaction.getBookId().equalsIgnoreCase(bookId)) {
                return "This book cannot be deleted because transaction history exists for it.";
            }
        }

        books.remove(book);
        saveAll();
        return "Book deleted successfully.";
    }

    public String addUser(
            String role,
            String userId,
            String name,
            String username,
            String password) {

        if (findUserById(userId) != null) {
            return "A user with this ID already exists.";
        }
        if (findUserByUsername(username) != null) {
            return "This username is already in use.";
        }

        User newUser;
        if ("ADMIN".equalsIgnoreCase(role)) {
            newUser = new Admin(userId, name, username, password);
        } else {
            newUser = new User(userId, name, username, password);
        }

        users.add(newUser);
        saveAll();
        return "Account added successfully.";
    }

    public String updateUser(
            String userId,
            String newName,
            String newUsername,
            String newPassword) {

        User user = findUserById(userId);
        if (user == null) {
            return "User not found.";
        }

        User sameUsername = findUserByUsername(newUsername);
        if (sameUsername != null && !sameUsername.getUserId().equalsIgnoreCase(userId)) {
            return "This username is already in use.";
        }

        user.setName(newName);
        user.setUsername(newUsername);
        user.setPassword(newPassword);
        saveAll();
        return "User updated successfully.";
    }

    public String deleteUser(String userId, String currentAdminId) {
        User user = findUserById(userId);
        if (user == null) {
            return "User not found.";
        }
        if (user.getUserId().equalsIgnoreCase(currentAdminId)) {
            return "You cannot delete the account that is currently logged in.";
        }

        for (BorrowTransaction transaction : transactions) {
            if (transaction.getUserId().equalsIgnoreCase(userId)
                    && !transaction.isReturned()) {
                return "This account has an active reservation and cannot be deleted.";
            }
        }

        users.remove(user);
        saveAll();
        return "User deleted successfully.";
    }

    public int getAvailableCopyCount(
            String bookId,
            LocalDate requestedStart,
            LocalDate requestedEnd) {

        Book book = findBookById(bookId);
        if (book == null || !DateUtils.isValidBookingPeriod(requestedStart, requestedEnd)) {
            return 0;
        }

        int available = 0;
        for (int copyNumber = 1; copyNumber <= book.getTotalCopies(); copyNumber++) {
            if (isCopyAvailable(bookId, copyNumber, requestedStart, requestedEnd)) {
                available++;
            }
        }
        return available;
    }

    public int findAvailableCopyNumber(
            String bookId,
            LocalDate requestedStart,
            LocalDate requestedEnd) {

        Book book = findBookById(bookId);
        if (book == null) {
            return -1;
        }

        for (int copyNumber = 1; copyNumber <= book.getTotalCopies(); copyNumber++) {
            if (isCopyAvailable(bookId, copyNumber, requestedStart, requestedEnd)) {
                return copyNumber;
            }
        }
        return -1;
    }

    private boolean isCopyAvailable(
            String bookId,
            int copyNumber,
            LocalDate requestedStart,
            LocalDate requestedEnd) {

        for (BorrowTransaction transaction : transactions) {
            if (transaction.getBookId().equalsIgnoreCase(bookId)
                    && transaction.getCopyNumber() == copyNumber
                    && transaction.overlaps(requestedStart, requestedEnd)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the earliest future start date on which the same requested duration
     * can be booked without a collision. The search is limited to ten years.
     */
    public LocalDate findEarliestAvailableStartDate(
            String bookId,
            LocalDate requestedStart,
            LocalDate requestedEnd) {

        Book book = findBookById(bookId);
        if (book == null || !DateUtils.isValidBookingPeriod(requestedStart, requestedEnd)) {
            return null;
        }

        long duration = DateUtils.durationDays(requestedStart, requestedEnd);
        LocalDate candidateStart = requestedStart;
        LocalDate searchLimit = requestedStart.plusYears(10);

        while (!candidateStart.isAfter(searchLimit)) {
            LocalDate candidateEnd = candidateStart.plusDays(duration);
            if (findAvailableCopyNumber(bookId, candidateStart, candidateEnd) != -1) {
                return candidateStart;
            }
            candidateStart = candidateStart.plusDays(1);
        }
        return null;
    }

    public String borrowBook(
            String userId,
            String bookId,
            LocalDate requestedStart,
            LocalDate requestedEnd) {

        User user = findUserById(userId);
        if (user == null) {
            return "User not found.";
        }

        Book book = findBookById(bookId);
        if (book == null) {
            return "Book not found.";
        }

        if (!DateUtils.isValidBookingPeriod(requestedStart, requestedEnd)) {
            return "Due date must be after the borrow date.";
        }

        if (requestedStart.isBefore(LocalDate.now())) {
            return "Borrow date cannot be in the past.";
        }

        if (hasOverlappingBookingForSameUser(userId, bookId, requestedStart, requestedEnd)) {
            return "You already have an overlapping reservation for this book.";
        }

        int copyNumber = findAvailableCopyNumber(bookId, requestedStart, requestedEnd);
        if (copyNumber == -1) {
            LocalDate earliest = findEarliestAvailableStartDate(
                    bookId,
                    requestedStart.plusDays(1),
                    requestedEnd.plusDays(1));

            if (earliest == null) {
                return "We have this book, but it is currently unavailable. "
                        + "No suitable date was found within the search period.";
            }

            return "We have this book, but it is currently unavailable for the requested period. "
                    + "Please check back on " + DateUtils.formatDate(earliest)
                    + ", when a copy can be booked for the same duration.";
        }

        String transactionId = generateTransactionId();
        BorrowTransaction transaction = new BorrowTransaction(
                transactionId,
                userId,
                bookId,
                copyNumber,
                requestedStart,
                requestedEnd,
                null,
                false);

        transactions.add(transaction);
        saveAll();

        return "Book borrowed/reserved successfully. Transaction ID: "
                + transactionId + ", assigned copy: " + copyNumber + ".";
    }

    public String returnBook(String transactionId, String actingUserId, boolean adminAction) {
        BorrowTransaction transaction = findTransactionById(transactionId);
        if (transaction == null) {
            return "Transaction not found.";
        }
        if (transaction.isReturned()) {
            return "This book has already been returned.";
        }
        if (!adminAction
                && !transaction.getUserId().equalsIgnoreCase(actingUserId)) {
            return "You may return only your own borrowed books.";
        }
        if (LocalDate.now().isBefore(transaction.getBorrowDate())) {
            return "This is a future reservation and cannot be returned before its borrow date.";
        }

        transaction.markReturned(LocalDate.now());
        saveAll();

        if (LocalDate.now().isAfter(transaction.getDueDate())) {
            long lateDays = DateUtils.durationDays(transaction.getDueDate(), LocalDate.now());
            return "Book returned successfully. It was " + lateDays + " day(s) late.";
        }
        return "Book returned successfully.";
    }

    public String checkAvailabilityMessage(
            String bookId,
            LocalDate requestedStart,
            LocalDate requestedEnd) {

        Book book = findBookById(bookId);
        if (book == null) {
            return "Book not found.";
        }
        if (!DateUtils.isValidBookingPeriod(requestedStart, requestedEnd)) {
            return "Due date must be after the start date.";
        }

        int available = getAvailableCopyCount(bookId, requestedStart, requestedEnd);
        if (available > 0) {
            return available + " of " + book.getTotalCopies()
                    + " copies are available for the requested period.";
        }

        LocalDate earliest = findEarliestAvailableStartDate(
                bookId,
                requestedStart.plusDays(1),
                requestedEnd.plusDays(1));

        if (earliest == null) {
            return "No copy is available, and no suitable date was found within ten years.";
        }

        return "All " + book.getTotalCopies() + " copies are unavailable. "
                + "The earliest suggested start date for the same duration is "
                + DateUtils.formatDate(earliest) + ".";
    }

    private boolean hasOverlappingBookingForSameUser(
            String userId,
            String bookId,
            LocalDate requestedStart,
            LocalDate requestedEnd) {

        for (BorrowTransaction transaction : transactions) {
            if (transaction.getUserId().equalsIgnoreCase(userId)
                    && transaction.getBookId().equalsIgnoreCase(bookId)
                    && transaction.overlaps(requestedStart, requestedEnd)) {
                return true;
            }
        }
        return false;
    }

    private User findUserByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                return user;
            }
        }
        return null;
    }

    private int getHighestActiveCopyNumber(String bookId) {
        int highest = 0;
        for (BorrowTransaction transaction : transactions) {
            if (transaction.getBookId().equalsIgnoreCase(bookId)
                    && !transaction.isReturned()) {
                highest = Math.max(highest, transaction.getCopyNumber());
            }
        }
        return highest;
    }

    private String generateTransactionId() {
        int maximum = 0;
        for (BorrowTransaction transaction : transactions) {
            String id = transaction.getTransactionId();
            if (id.matches("T\\d+")) {
                try {
                    maximum = Math.max(maximum, Integer.parseInt(id.substring(1)));
                } catch (NumberFormatException ignored) {
                    // Ignore non-standard IDs and continue.
                }
            }
        }
        return String.format("T%04d", maximum + 1);
    }

    private void saveAll() {
        FileManager.saveAll(books, users, transactions);
    }
}
