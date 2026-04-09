package model;

public class Student extends User {
    private String grade;
    private String group;

    public Student(String id, String name, String email, String grade, String group) {
        super(id, name, email);
        this.grade = grade;
        this.group = group;
    }

    public String getGrade() {
        return grade;
    }

    public String getGroup() {
        return group;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String toString() {
        return super.toString() + " | Grade: " + grade + " | Group: " + group;
    }
}
