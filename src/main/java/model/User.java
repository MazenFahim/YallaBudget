package model;

public class User {
    public String username;
    private String password;
    private String email;
    private static int UsersCounter = 0;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        UsersCounter++;
    }

    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    public boolean matches(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
}
