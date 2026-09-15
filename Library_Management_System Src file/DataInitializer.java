import java.util.ArrayList;
import java.util.List;

/**
 * Creates the required demonstration data only when the corresponding data
 * file is missing or contains no valid records. Existing records are preserved
 * so that Admin updates and deletions remain saved after restarting the app.
 */
public final class DataInitializer {

    private DataInitializer() {
    }

    public static void initializeIfNeeded() {
        initializeBooksIfNeeded();
        initializeUsersIfNeeded();

        if (!FileManager.transactionFileExists()) {
            FileManager.createEmptyTransactionFile();
        }
    }

    private static void initializeBooksIfNeeded() {
        if (FileManager.bookFileExists() && !FileManager.loadBooks().isEmpty()) {
            return;
        }

        List<Book> books = new ArrayList<Book>();
        books.add(new Book("B001", "Harry Potter Part 1", "J.K. Rowling", 3));
        books.add(new Book("B002", "Harry Potter Part 2", "J.K. Rowling", 3));
        books.add(new Book("B003", "Harry Potter Part 3", "J.K. Rowling", 4));
        books.add(new Book("B004", "The Hobbit", "J.R.R. Tolkien", 2));
        books.add(new Book("B005", "The Alchemist", "Paulo Coelho", 2));
        books.add(new Book("B006", "Clean Code", "Robert C. Martin", 1));
        FileManager.saveBooks(books);
    }

    private static void initializeUsersIfNeeded() {
        if (FileManager.userFileExists() && !FileManager.loadUsers().isEmpty()) {
            return;
        }

        List<User> users = new ArrayList<User>();
        users.add(new Admin("A001", "Rakib", "rakib", "rakib123"));
        users.add(new Admin("A002", "Tanvir", "tanvir", "tanvir123"));
        users.add(new User("U001", "Devid", "devid", "devid123"));
        users.add(new User("U002", "Jack", "jack", "jack123"));
        users.add(new User("U003", "Angela", "angela", "angela123"));
        users.add(new User("U004", "Adam", "adam", "adam123"));
        FileManager.saveUsers(users);
    }
}
