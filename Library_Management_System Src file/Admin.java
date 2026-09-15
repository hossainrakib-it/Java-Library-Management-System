/**
 * Administrator account with additional permissions.
 */
public class Admin extends User {

    public Admin(String adminId, String name, String username, String password) {
        super(adminId, name, username, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
