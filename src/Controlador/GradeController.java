package Controlador;

import Modelo.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for grade management (RF-12, RF-13)
 * Handles grades, student assignments, group teachers, and representatives
 */
public class GradeController {
    private List<Grade> grades;
    private AuthController authController;
    private UserController userController;
    private int nextGradeId;
    
    public GradeController(AuthController authController, UserController userController) {
        this.grades = new ArrayList<>();
        this.authController = authController;
        this.userController = userController;
        this.nextGradeId = 1;
        initializeDefaultGrades();
    }
    
    /**
     * Initializes default grades
     */
    private void initializeDefaultGrades() {
        grades.add(new Grade(nextGradeId++, "Grade 10A"));
        grades.add(new Grade(nextGradeId++, "Grade 10B"));
        grades.add(new Grade(nextGradeId++, "Grade 11A"));
        
        // Assign some students to grades
        List<Student> allStudents = userController.getAllStudents();
        if (allStudents.size() >= 3) {
            grades.get(0).addStudent(allStudents.get(0));
            grades.get(0).addStudent(allStudents.get(1));
            grades.get(1).addStudent(allStudents.get(2));
        }
    }
    
    /**
     * Gets all grades
     * @return List of grades
     */
    public List<Grade> getAllGrades() {
        return new ArrayList<>(grades);
    }
    
    /**
     * Adds a new grade (Coordinator only)
     * @param gradeName Name of the grade
     * @return true if grade created successfully
     */
    public boolean addGrade(String gradeName) {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Coordinator can add grades");
            return false;
        }
        
        Grade newGrade = new Grade(nextGradeId++, gradeName);
        grades.add(newGrade);
        System.out.println("✅ Grade created: " + gradeName);
        return true;
    }
    
    /**
     * Assigns a student to a grade
     * @param studentId Student ID
     * @param gradeId Grade ID
     * @return true if assignment successful
     */
    public boolean assignStudentToGrade(String studentId, int gradeId) {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Coordinator can assign students");
            return false;
        }
        
        Student student = (Student) userController.findUserById(studentId);
        Grade grade = findGradeById(gradeId);
        
        if (student == null || grade == null) {
            System.out.println("❌ Student or Grade not found");
            return false;
        }
        
        grade.addStudent(student);
        return true;
    }
    
    /**
     * Assigns a group teacher to a grade (RF-12)
     * @param teacherId Teacher ID (must be GroupTeacher)
     * @param gradeId Grade ID
     * @return true if assignment successful
     */
    public boolean assignGroupTeacherToGrade(String teacherId, int gradeId) {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Coordinator can assign group teachers");
            return false;
        }
        
        User user = userController.findUserById(teacherId);
        Grade grade = findGradeById(gradeId);
        
        if (!(user instanceof GroupTeacher)) {
            System.out.println("❌ User is not a Group Teacher");
            return false;
        }
        
        if (grade == null) {
            System.out.println("❌ Grade not found");
            return false;
        }
        
        GroupTeacher groupTeacher = (GroupTeacher) user;
        grade.setDocenteGrupo(groupTeacher);
        groupTeacher.setAssignedGrade(grade);
        
        return true;
    }
    
    /**
     * Assigns a representative to a grade (RF-12)
     * @param studentId Student ID (will be converted to Representative)
     * @param gradeId Grade ID
     * @return true if assignment successful
     */
    public boolean assignRepresentativeToGrade(String studentId, int gradeId) {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Coordinator can assign representatives");
            return false;
        }
        
        User user = userController.findUserById(studentId);
        Grade grade = findGradeById(gradeId);
        
        if (!(user instanceof Student)) {
            System.out.println("❌ User is not a Student");
            return false;
        }
        
        if (grade == null) {
            System.out.println("❌ Grade not found");
            return false;
        }
        
        // Convert to Representative if not already
        Representative representative;
        if (user instanceof Representative) {
            representative = (Representative) user;
        } else {
            Student student = (Student) user;
            representative = new Representative("REP-" + student.getId(), student.getName(), student.getPassword());
            representative.setActive(student.isActive());
            representative.setGrade(grade);
            for (Observation obs : student.getObservations()) {
                representative.addObservation(obs);
            }
            // Add to user list
            userController.createUser(representative);
        }
        
        grade.setRepresentante(representative);
        return true;
    }
    
    /**
     * Gets full history of all students in a grade (RF-13 - Group Teacher only)
     * @param gradeId Grade ID
     * @return List of all observations or null if not authorized
     */
    public List<Observation> getFullGradeHistory(int gradeId) {
        User currentUser = authController.getCurrentUser();
        
        // Only Group Teacher can view full grade history
        if (!(currentUser instanceof GroupTeacher) && !authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Group Teacher can view full grade history");
            return null;
        }
        
        Grade grade = findGradeById(gradeId);
        if (grade == null) {
            System.out.println("❌ Grade not found");
            return null;
        }
        
        // Check if current user is the group teacher of this grade
        if (currentUser instanceof GroupTeacher) {
            GroupTeacher groupTeacher = (GroupTeacher) currentUser;
            if (groupTeacher.getAssignedGrade() == null || groupTeacher.getAssignedGrade().getId() != gradeId) {
                System.out.println("❌ You are not the group teacher for this grade");
                return null;
            }
        }
        
        List<Observation> allObservations = new ArrayList<>();
        for (Student student : grade.getStudents()) {
            allObservations.addAll(student.getObservations());
        }
        
        allObservations.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        return allObservations;
    }
    
    /**
     * Prints full grade history
     * @param gradeId Grade ID
     */
    public void printFullGradeHistory(int gradeId) {
        List<Observation> observations = getFullGradeHistory(gradeId);
        Grade grade = findGradeById(gradeId);
        
        if (grade == null) return;
        
        System.out.println("\n" + line(60));
        System.out.println("📖 FULL GRADE HISTORY - " + grade.getName());
        System.out.println(line(60));
        
        if (observations == null || observations.isEmpty()) {
            System.out.println("No observations found for this grade");
            return;
        }
        
        for (int i = 0; i < observations.size(); i++) {
            System.out.println((i + 1) + ". " + observations.get(i).getSummary());
        }
        System.out.println(line(60));
        System.out.println("Total observations: " + observations.size());
    }
    
    /**
     * Prints grade statistics
     * @param gradeId Grade ID
     */
    public void printGradeStatistics(int gradeId) {
        Grade grade = findGradeById(gradeId);
        if (grade == null) {
            System.out.println("❌ Grade not found");
            return;
        }
        
        grade.printStatistics();
    }
    
    /**
     * Finds a grade by ID
     * @param gradeId Grade ID
     * @return Grade object or null
     */
    public Grade findGradeById(int gradeId) {
        for (Grade grade : grades) {
            if (grade.getId() == gradeId) {
                return grade;
            }
        }
        return null;
    }
    
    /**
     * Gets available students (not assigned to any grade)
     * @return List of available students
     */
    public List<Student> getAvailableStudents() {
        List<Student> allStudents = userController.getAllStudents();
        List<Student> assignedStudents = new ArrayList<>();
        
        for (Grade grade : grades) {
            assignedStudents.addAll(grade.getStudents());
        }
        
        List<Student> available = new ArrayList<>();
        for (Student student : allStudents) {
            if (!assignedStudents.contains(student)) {
                available.add(student);
            }
        }
        return available;
    }
    
    /**
     * Gets all teachers
     * @return List of teachers
     */
    public List<Teacher> getAllTeachers() {
        return userController.getAllTeachers();
    }
    
    /**
     * Gets all students
     * @return List of students
     */
    public List<Student> getAllStudents() {
        return userController.getAllStudents();
    }
    
    private String line(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("═");
        }
        return sb.toString();
    }
}