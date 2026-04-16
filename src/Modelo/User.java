package Modelo;

import java.util.Objects;

/**
 * 
 */

public abstract class User {
    
    // ============================ ATRIBUTOS ============================
    
    protected String id;           // Unique identifier (e.g., "EST-001", "DOC-001", "COORD-001")
    protected String name;         // Full name of the user
    protected String password;     // User's password (in a real system, this should be encrypted)
    protected boolean active;      // Account status (active/inactive)
    protected boolean firstTime;   // RF-15: Mandatory password change on first login
    
    // ============================ CONSTRUCTOR ============================
    
    /**
     * Constructor for a new user
     * @param id Unique identifier (String format, e.g., "EST-001")
     * @param name Full name of the user
     * @param password Initial password
     */
    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.active = true;        // New users are active by default
        this.firstTime = true;     // RF-15: Must change password on first login
    }
    
    // ============================ MÉTODOS PRINCIPALES ============================
    
    /**
     * Authenticates a user with their ID and password (RF-2)
     * @param id User's unique identifier
     * @param password User's password
     * @return true if credentials are valid and account is active
     */
    public boolean login(String id, String password) {
        return this.id.equals(id) && this.password.equals(password) && this.active;
    }
    
    /**
     * Changes the user's password (RF-15)
     * @param newPassword New password (should be validated before calling)
     * @throws IllegalArgumentException if password is null, empty, or too short
     */
    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (newPassword.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
        this.password = newPassword;
        this.firstTime = false;    // Password changed, no longer first time
    }
    
    /**
     * Gets the role of the user (must be implemented by subclasses)
     * @return Role as String (STUDENT, TEACHER, COORDINATOR, GROUP_TEACHER, REPRESENTATIVE)
     */
    public abstract String getRole();
    
    // ============================ GETTERS Y SETTERS ============================
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isFirstTime() {
        return firstTime;
    }
    
    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }
    
    // ============================ MÉTODOS DE VERIFICACIÓN DE TIPO ============================
    
    /**
     * Checks if the user is a student
     * @return true if user is an instance of Student
     */
    public boolean isStudent() {
        return this instanceof Student;
    }
    
    /**
     * Checks if the user is a teacher
     * @return true if user is an instance of Teacher
     */
    public boolean isTeacher() {
        return this instanceof Teacher;
    }
    
    /**
     * Checks if the user is a coordinator
     * @return true if user is an instance of Coordinator
     */
    public boolean isCoordinator() {
        return this instanceof Coordinator;
    }
    
    /**
     * Checks if the user is a group teacher
     * @return true if user is an instance of GroupTeacher
     */
    public boolean isGroupTeacher() {
        return this instanceof GroupTeacher;
    }
    
    /**
     * Checks if the user is a representative
     * @return true if user is an instance of Representative
     */
    public boolean isRepresentative() {
        return this instanceof Representative;
    }
    
    // ============================ MÉTODOS DE CONVERSIÓN ============================
    
    /**
     * Converts user to Student (safe cast)
     * @return Student object or null if not a student
     */
    public Student asStudent() {
        return isStudent() ? (Student) this : null;
    }
    
    /**
     * Converts user to Teacher (safe cast)
     * @return Teacher object or null if not a teacher
     */
    public Teacher asTeacher() {
        return isTeacher() ? (Teacher) this : null;
    }
    
    /**
     * Converts user to Coordinator (safe cast)
     * @return Coordinator object or null if not a coordinator
     */
    public Coordinator asCoordinator() {
        return isCoordinator() ? (Coordinator) this : null;
    }
    
    /**
     * Converts user to GroupTeacher (safe cast)
     * @return GroupTeacher object or null if not a group teacher
     */
    public GroupTeacher asGroupTeacher() {
        return isGroupTeacher() ? (GroupTeacher) this : null;
    }
    
    /**
     * Converts user to Representative (safe cast)
     * @return Representative object or null if not a representative
     */
    public Representative asRepresentative() {
        return isRepresentative() ? (Representative) this : null;
    }
    
    // ============================ MÉTODOS UTILITARIOS ============================
    
    /**
     * Gets a formatted string with user information
     * @return Formatted user info
     */
    public String getInfo() {
        return String.format("[%s] %s (ID: %s) - Active: %s",
                getRole(), name, id, active ? "Yes" : "No");
    }
    
    /**
     * Gets a short summary of the user
     * @return Short summary string
     */
    public String getSummary() {
        return String.format("%s: %s", getRole(), name);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', role='%s', active=%s, firstTime=%s}",
                id, name, getRole(), active, firstTime);
    }
    
}
