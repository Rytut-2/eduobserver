package Controlador;

import Modelo.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for user management (RF-9)
 * Handles CRUD operations for users (Coordinator only)
 */
public class UserController {
    private List<User> users;
    private AuthController authController;
    
    public UserController(AuthController authController) {
        this.users = new ArrayList<>();
        this.authController = authController;
        initializeDefaultUsers();
    }
    
    /**
     * Initializes default users for testing
     */
    private void initializeDefaultUsers() {
        users.add(new Student("EST-001", "Jesús Beltrán", "12345"));
        users.add(new Student("EST-002", "Daniel Castillo", "12345"));
        users.add(new Student("EST-003", "Sergio Martínez", "12345"));
        users.add(new Teacher("DOC-001", "Prof. Ana Rodríguez", "12345"));
        users.add(new Coordinator("COORD-001", "Dr. Carlos Méndez", "12345"));
        users.add(new GroupTeacher("GRP-001", "Prof. Luis Fernández", "12345"));
        users.add(new Representative("REP-001", "Ana López", "12345"));
    }
    
    /**
     * Gets all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    /**
     * Creates a new user (RF-9 - Coordinator only)
     * @param user User to create
     * @return true if user created successfully
     */
    public boolean createUser(User user) {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Coordinator can create users");
            return false;
        }
        
        for (User existingUser : users) {
            if (existingUser.getId().equals(user.getId())) {
                System.out.println("❌ User with this ID already exists");
                return false;
            }
        }
        
        users.add(user);
        authController.updateUsers(users);
        System.out.println("✅ User created: " + user.getId() + " - " + user.getName());
        return true;
    }
    
    /**
     * Edits an existing user (RF-9 - Coordinator only)
     * @param userId User ID to edit
     * @param newName New name
     * @param newPassword New password (null to keep)
     * @return true if user edited successfully
     */
    public boolean editUser(String userId, String newName, String newPassword) {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Coordinator can edit users");
            return false;
        }
        
        User user = findUserById(userId);
        if (user == null) {
            System.out.println("❌ User not found");
            return false;
        }
        
        user.setName(newName);
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }
        
        System.out.println("✅ User edited: " + user.getId() + " - " + user.getName());
        return true;
    }
    
    /**
     * Disables a user account (RF-9 - Coordinator only)
     * @param userId User ID to disable
     * @return true if user disabled successfully
     */
    public boolean disableUser(String userId) {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Coordinator can disable users");
            return false;
        }
        
        User user = findUserById(userId);
        if (user == null) {
            System.out.println("❌ User not found");
            return false;
        }
        
        user.setActive(false);
        System.out.println("✅ User disabled: " + user.getId() + " - " + user.getName());
        return true;
    }
    
    /**
     * Finds a user by ID
     * @param userId User ID
     * @return User object or null
     */
    public User findUserById(String userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Finds a user by name (partial match)
     * @param name Name to search
     * @return List of matching users
     */
    public List<User> findUsersByName(String name) {
        List<User> result = new ArrayList<>();
        String searchLower = name.toLowerCase();
        for (User user : users) {
            if (user.getName().toLowerCase().contains(searchLower)) {
                result.add(user);
            }
        }
        return result;
    }
    
    /**
     * Gets all students
     * @return List of students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }
        return students;
    }
    
    /**
     * Gets all teachers (including GroupTeacher)
     * @return List of teachers
     */
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Teacher) {
                teachers.add((Teacher) user);
            }
        }
        return teachers;
    }
    
    /**
     * Gets all coordinators
     * @return List of coordinators
     */
    public List<Coordinator> getAllCoordinators() {
        List<Coordinator> coordinators = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Coordinator) {
                coordinators.add((Coordinator) user);
            }
        }
        return coordinators;
    }
    
    /**
     * Gets all group teachers
     * @return List of group teachers
     */
    public List<GroupTeacher> getAllGroupTeachers() {
        List<GroupTeacher> groupTeachers = new ArrayList<>();
        for (User user : users) {
            if (user instanceof GroupTeacher) {
                groupTeachers.add((GroupTeacher) user);
            }
        }
        return groupTeachers;
    }
    
    /**
     * Gets all representatives
     * @return List of representatives
     */
    public List<Representative> getAllRepresentatives() {
        List<Representative> representatives = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Representative) {
                representatives.add((Representative) user);
            }
        }
        return representatives;
    }
}