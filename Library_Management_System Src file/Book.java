import java.util.Objects;

/**
 * Represents one book title in the library catalogue.
 * Copies are identified by numbers from 1 to totalCopies.
 */
public class Book {
    private String bookId;
    private String title;
    private String author;
    private int totalCopies;

    public Book(String bookId, String title, String author, int totalCopies) {
        setBookId(bookId);
        setTitle(title);
        setAuthor(author);
        setTotalCopies(totalCopies);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be empty.");
        }
        this.bookId = bookId.trim().toUpperCase();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty.");
        }
        this.author = author.trim();
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        if (totalCopies < 1) {
            throw new IllegalArgumentException("Total copies must be at least 1.");
        }
        this.totalCopies = totalCopies;
    }

    @Override
    public String toString() {
        return String.format("%-6s | %-28s | %-22s | Copies: %d",
                bookId, title, author, totalCopies);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Book)) {
            return false;
        }
        Book other = (Book) object;
        return bookId.equalsIgnoreCase(other.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId.toUpperCase());
    }
}
