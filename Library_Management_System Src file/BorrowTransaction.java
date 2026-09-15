import java.time.LocalDate;

/**
 * Records one reservation/borrowing transaction for a specific physical copy.
 */
public class BorrowTransaction {
    private final String transactionId;
    private final String userId;
    private final String bookId;
    private final int copyNumber;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;

    public BorrowTransaction(
            String transactionId,
            String userId,
            String bookId,
            int copyNumber,
            LocalDate borrowDate,
            LocalDate dueDate,
            LocalDate returnDate,
            boolean returned) {

        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty.");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be empty.");
        }
        if (copyNumber < 1) {
            throw new IllegalArgumentException("Copy number must be at least 1.");
        }
        if (!DateUtils.isValidBookingPeriod(borrowDate, dueDate)) {
            throw new IllegalArgumentException("Due date must be after borrow date.");
        }

        this.transactionId = transactionId.trim().toUpperCase();
        this.userId = userId.trim().toUpperCase();
        this.bookId = bookId.trim().toUpperCase();
        this.copyNumber = copyNumber;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.returned = returned;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getBookId() {
        return bookId;
    }

    public int getCopyNumber() {
        return copyNumber;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    /**
     * If a book was returned early or late, the actual return date becomes
     * the effective end of that transaction. Otherwise, the planned due date
     * is used for collision checking.
     */
    public LocalDate getEffectiveEndDate() {
        return returned && returnDate != null ? returnDate : dueDate;
    }

    public boolean overlaps(LocalDate requestedStart, LocalDate requestedEnd) {
        return DateUtils.rangesOverlap(
                borrowDate,
                getEffectiveEndDate(),
                requestedStart,
                requestedEnd);
    }

    public void markReturned(LocalDate actualReturnDate) {
        if (returned) {
            throw new IllegalStateException("This transaction has already been returned.");
        }
        if (actualReturnDate == null) {
            throw new IllegalArgumentException("Return date cannot be null.");
        }
        if (actualReturnDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Return date cannot be before borrow date.");
        }
        this.returnDate = actualReturnDate;
        this.returned = true;
    }

    @Override
    public String toString() {
        return String.format(
                "%s | User: %-6s | Book: %-6s | Copy: %d | %s to %s | Returned: %-5s | Return date: %s",
                transactionId,
                userId,
                bookId,
                copyNumber,
                DateUtils.formatDate(borrowDate),
                DateUtils.formatDate(dueDate),
                returned ? "Yes" : "No",
                DateUtils.formatDate(returnDate));
    }
}
