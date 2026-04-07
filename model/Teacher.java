package model;

/**
 * Teacher class representing a teacher user in the system.
 * Teachers can create observations for students.
 *
 * SOLID Principles Applied:
 * - Single Responsibility: Handles teacher-specific data only
 * - Liskov Substitution: Can substitute User without breaking functionality
 */
public class Teacher extends User {
    private String department;

    public Teacher(String id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getRole() {
        return "Teacher";
    }

    @Override
    public String toString() {
        return super.toString() + " | Department: " + department;
    }
}
