package model;

/**
 * Abstract base class representing a user in the system.
 * This class follows the Single Responsibility Principle by focusing
 * only on user identification and basic information.
 *
 * SOLID Principles Applied:
 * - Single Responsibility: Manages user identity information only
 * - Open/Closed: Can be extended for new user types
 * - Liskov Substitution: Subclasses can substitute User anywhere
 */
public abstract class User {
    protected String id;
    protected String name;
    protected String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public abstract String getRole();

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Email: " + email + " | Role: " + getRole();
    }
}
