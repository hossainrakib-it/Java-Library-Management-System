import java.util.Objects;

/**
 * Represents a normal library user account.
 * Admin extends this class to demonstrate inheritance and polymorphism.
 */
public class User {
    private String userId;
    private String name;
    private String username;
    private String password;

    public User(String userId, String name, String username, String password) {
        setUserId(userId);
        setName(name);
        setUsername(username);
        setPassword(password);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }
        this.userId = userId.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.username = username.trim().toLowerCase();
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must contain at least 4 characters.");
        }
        this.password = password;
    }

    public boolean checkPassword(String enteredPassword) {
        return password.equals(enteredPassword);
    }

    /**
     * Used only by FileManager when saving local demonstration data.
     * In a real system passwords should be hashed and salted.
     */
    public String getPasswordForStorage() {
        return password;
    }

    public String getRole() {
        return "USER";
    }

    @Override
    public String toString() {
        return String.format("%-6s | %-22s | %-14s | %s",
                userId, name, username, getRole());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        return userId.equalsIgnoreCase(other.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId.toUpperCase());
    }
}
