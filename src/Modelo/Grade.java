package Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Grade {
    
    // ============================ ATRIBUTOS ============================
    
    private int id;                          // Unique identifier for the grade
    private String name;                     // Grade name (e.g., "Grade 10A", "11B")
    private List<Student> students;          // List of students in the grade
    private GroupTeacher docenteGrupo;       // RF-12: Group teacher assigned to the grade
    private Representative representante;    // RF-12: Student representative for the grade
    
    // ============================ CONSTRUCTOR ============================
    
    /**
     * Constructor for a new grade
     * @param id Unique identifier for the grade
     * @param name Name of the grade (e.g., "Grade 10A")
     */
    public Grade(int id, String name) {
        this.id = id;
        this.name = name;
        this.students = new ArrayList<>();
        this.docenteGrupo = null;
        this.representante = null;
    }
    
    // ============================ MÉTODOS DE GESTIÓN DE ESTUDIANTES ============================
    
    /**
     * Adds a student to the grade
     * @param student Student to add
     * @return true if student was added successfully
     */
    public boolean addStudent(Student student) {
        if (student == null) {
            System.out.println("❌ Cannot add null student");
            return false;
        }
        
        if (students.contains(student)) {
            System.out.println("❌ Student " + student.getName() + " is already in this grade");
            return false;
        }
        
        students.add(student);
        student.setGrade(this);
        System.out.println("✅ Student " + student.getName() + " added to grade " + this.name);
        return true;
    }
    
    /**
     * Removes a student from the grade
     * @param student Student to remove
     * @return true if student was removed successfully
     */
    public boolean removeStudent(Student student) {
        if (student == null) {
            System.out.println("❌ Cannot remove null student");
            return false;
        }
        
        if (students.remove(student)) {
            student.setGrade(null);
            System.out.println("✅ Student " + student.getName() + " removed from grade " + this.name);
            return true;
        }
        
        System.out.println("❌ Student " + student.getName() + " not found in this grade");
        return false;
    }
    
    /**
     * Removes a student by ID
     * @param studentId ID of the student to remove
     * @return true if student was removed successfully
     */
    public boolean removeStudentById(String studentId) {
        Student studentToRemove = findStudentById(studentId);
        if (studentToRemove != null) {
            return removeStudent(studentToRemove);
        }
        System.out.println("❌ Student with ID " + studentId + " not found");
        return false;
    }
    
    /**
     * Finds a student by ID
     * @param studentId Student ID to search for
     * @return Student object or null if not found
     */
    public Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.getId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }
    
    /**
     * Finds a student by name (case-insensitive, partial match)
     * @param name Name to search for
     * @return List of matching students
     */
    public List<Student> findStudentsByName(String name) {
        List<Student> result = new ArrayList<>();
        String searchLower = name.toLowerCase();
        for (Student student : students) {
            if (student.getName().toLowerCase().contains(searchLower)) {
                result.add(student);
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    // ============================ MÉTODOS DE ESTADÍSTICAS ============================
    
    /**
     * Gets the total number of students in the grade
     * @return Number of students
     */
    public int getStudentCount() {
        return students.size();
    }
    
    /**
     * Gets the total number of observations from all students in the grade
     * @return Total observations count
     */
    public int getTotalObservations() {
        int total = 0;
        for (Student student : students) {
            total += student.getTotalObservations();
        }
        return total;
    }
    
    /**
     * Gets the total number of disciplinary observations in the grade
     * @return Total disciplinary observations
     */
    public int getTotalDisciplinaryObservations() {
        int total = 0;
        for (Student student : students) {
            total += student.getDisciplinaryObservations().size();
        }
        return total;
    }
    
    /**
     * Gets the total number of academic observations in the grade
     * @return Total academic observations
     */
    public int getTotalAcademicObservations() {
        int total = 0;
        for (Student student : students) {
            total += student.getAcademicObservations().size();
        }
        return total;
    }
    
    /**
     * Gets the average number of observations per student
     * @return Average observations per student
     */
    public double getAverageObservationsPerStudent() {
        if (students.isEmpty()) {
            return 0.0;
        }
        return (double) getTotalObservations() / students.size();
    }
    
    // ============================ MÉTODOS DE REPORTE ============================
    
    /**
     * Gets all observations from all students in the grade
     * @return List of all observations sorted by date (newest first)
     */
    public List<Observation> getAllObservations() {
        List<Observation> allObservations = new ArrayList<>();
        for (Student student : students) {
            allObservations.addAll(student.getObservations());
        }
        allObservations.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        return Collections.unmodifiableList(allObservations);
    }
    
    /**
     * Gets the student with the most observations
     * @return Student with most observations or null if no students
     */
    public Student getStudentWithMostObservations() {
        if (students.isEmpty()) {
            return null;
        }
        
        Student maxStudent = students.get(0);
        int maxObservations = maxStudent.getTotalObservations();
        
        for (Student student : students) {
            if (student.getTotalObservations() > maxObservations) {
                maxObservations = student.getTotalObservations();
                maxStudent = student;
            }
        }
        return maxStudent;
    }
    
    /**
     * Gets the student with the least observations
     * @return Student with least observations or null if no students
     */
    public Student getStudentWithLeastObservations() {
        if (students.isEmpty()) {
            return null;
        }
        
        Student minStudent = students.get(0);
        int minObservations = minStudent.getTotalObservations();
        
        for (Student student : students) {
            if (student.getTotalObservations() < minObservations) {
                minObservations = student.getTotalObservations();
                minStudent = student;
            }
        }
        return minStudent;
    }
    
    // ============================ MÉTODOS DE IMPRESIÓN ============================
    
    /**
     * Prints the complete grade roster with student information
     */
    public void printRoster() {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("🏫 GRADE ROSTER: " + name);
        System.out.println("═".repeat(60));
        System.out.println("ID: " + id);
        System.out.println("Group Teacher: " + (docenteGrupo != null ? docenteGrupo.getName() : "Not assigned"));
        System.out.println("Representative: " + (representante != null ? representante.getName() : "Not assigned"));
        System.out.println("Total Students: " + students.size());
        System.out.println("─".repeat(60));
        
        if (students.isEmpty()) {
            System.out.println("   No students enrolled");
        } else {
            System.out.printf("%-5s %-25s %-15s %-10s\n", "No.", "Name", "ID", "Observations");
            System.out.println("─".repeat(60));
            int index = 1;
            for (Student student : students) {
                System.out.printf("%-5d %-25s %-15s %-10d\n", 
                    index++, 
                    truncate(student.getName(), 25), 
                    student.getId(),
                    student.getTotalObservations());
            }
        }
        System.out.println("═".repeat(60));
    }
    
    /**
     * Prints grade statistics
     */
    public void printStatistics() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("📊 GRADE STATISTICS: " + name);
        System.out.println("═".repeat(50));
        System.out.println("Students: " + students.size());
        System.out.println("Total Observations: " + getTotalObservations());
        System.out.println("  • Disciplinary: " + getTotalDisciplinaryObservations());
        System.out.println("  • Academic: " + getTotalAcademicObservations());
        System.out.println("Average per student: " + String.format("%.2f", getAverageObservationsPerStudent()));
        
        if (!students.isEmpty()) {
            Student most = getStudentWithMostObservations();
            Student least = getStudentWithLeastObservations();
            System.out.println("\nMost observations: " + most.getName() + " (" + most.getTotalObservations() + ")");
            System.out.println("Least observations: " + least.getName() + " (" + least.getTotalObservations() + ")");
        }
        System.out.println("═".repeat(50));
    }
    
    // ============================ GETTERS Y SETTERS ============================
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }
    
    public GroupTeacher getDocenteGrupo() {
        return docenteGrupo;
    }
    
    /**
     * RF-12: Assigns a group teacher to the grade
     * @param docenteGrupo Group teacher to assign
     */
    public void setDocenteGrupo(GroupTeacher docenteGrupo) {
        this.docenteGrupo = docenteGrupo;
        if (docenteGrupo != null) {
            docenteGrupo.setAssignedGrade(this);
            System.out.println("✅ Group Teacher " + docenteGrupo.getName() + " assigned to grade " + this.name);
        }
    }
    
    public Representative getRepresentante() {
        return representante;
    }
    
    /**
     * RF-12: Assigns a student representative to the grade
     * @param representante Representative to assign
     */
    public void setRepresentante(Representative representante) {
        this.representante = representante;
        if (representante != null) {
            representante.setGrade(this);
            System.out.println("✅ Representative " + representante.getName() + " assigned to grade " + this.name);
        }
    }
    
    // ============================ MÉTODOS UTILITARIOS ============================
    
    /**
     * Checks if the grade has a group teacher assigned
     * @return true if group teacher exists
     */
    public boolean hasGroupTeacher() {
        return docenteGrupo != null;
    }
    
    /**
     * Checks if the grade has a representative assigned
     * @return true if representative exists
     */
    public boolean hasRepresentative() {
        return representante != null;
    }
    
    /**
     * Checks if the grade has any students
     * @return true if at least one student
     */
    public boolean hasStudents() {
        return !students.isEmpty();
    }
    
    /**
     * Truncates a string to a maximum length
     * @param str String to truncate
     * @param maxLength Maximum length
     * @return Truncated string
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Grade grade = (Grade) obj;
        return id == grade.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Grade{id=%d, name='%s', students=%d, groupTeacher=%s, representative=%s}",
                id, name, students.size(),
                docenteGrupo != null ? docenteGrupo.getName() : "None",
                representante != null ? representante.getName() : "None");
    }
    
}
