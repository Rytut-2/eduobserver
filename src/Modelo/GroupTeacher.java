package Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupTeacher extends Teacher {
    
    private Grade assignedGrade;
    
    public GroupTeacher(String id, String name, String password) {
        super(id, name, password);
        this.assignedGrade = null;
    }
    
    @Override
    public String getRole() {
        return "GROUP_TEACHER";
    }
    
    // RF-13: View full grade history
    public List<Observation> viewFullGradeHistory() {
        if (assignedGrade == null) {
            System.out.println("No grade assigned");
            return Collections.emptyList();
        }
        
        List<Observation> allObservations = new ArrayList<>();
        for (Student student : assignedGrade.getStudents()) {
            allObservations.addAll(student.getObservations());
        }
        
        allObservations.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        return Collections.unmodifiableList(allObservations);
    }
    
    // Get statistics for assigned grade
    public GradeStatistics getGradeStatistics() {
        if (assignedGrade == null) return null;
        
        int totalStudents = assignedGrade.getStudents().size();
        int totalObservations = 0;
        int totalDisciplinary = 0;
        int totalAcademic = 0;
        
        for (Student student : assignedGrade.getStudents()) {
            totalObservations += student.getTotalObservations();
            totalDisciplinary += student.getDisciplinaryObservations().size();
            totalAcademic += student.getAcademicObservations().size();
        }
        
        return new GradeStatistics(assignedGrade.getName(), totalStudents, 
                                   totalObservations, totalDisciplinary, totalAcademic);
    }
    
    // Get students with specific severity
    public List<Student> getStudentsWithSeverity(Severity severity) {
        if (assignedGrade == null) return Collections.emptyList();
        
        List<Student> result = new ArrayList<>();
        for (Student student : assignedGrade.getStudents()) {
            for (DisciplinaryObservation obs : student.getDisciplinaryObservations()) {
                if (obs.getSeverity() == severity && obs.isActive()) {
                    result.add(student);
                    break;
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    // Get students with specific academic type
    public List<Student> getStudentsWithAcademicType(AcademicType academicType) {
        if (assignedGrade == null) return Collections.emptyList();
        
        List<Student> result = new ArrayList<>();
        for (Student student : assignedGrade.getStudents()) {
            for (AcademicObservation obs : student.getAcademicObservations()) {
                if (obs.getAcademicType() == academicType && obs.isActive()) {
                    result.add(student);
                    break;
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    // Print full grade report
    public void printFullGradeReport() {
        if (assignedGrade == null) {
            System.out.println("No grade assigned");
            return;
        }
        
        System.out.println("\n" + line(60));
        System.out.println("GRADE REPORT: " + assignedGrade.getName());
        System.out.println("Group Teacher: " + this.getName());
        System.out.println(line(60));
        
        List<Student> students = assignedGrade.getStudents();
        if (students.isEmpty()) {
            System.out.println("No students in this grade");
            return;
        }
        
        for (Student student : students) {
            System.out.println("\nStudent: " + student.getName());
            System.out.println("  Total observations: " + student.getTotalObservations());
            System.out.println("  Disciplinary: " + student.getDisciplinaryObservations().size());
            System.out.println("  Academic: " + student.getAcademicObservations().size());
        }
        
        GradeStatistics stats = getGradeStatistics();
        if (stats != null) {
            System.out.println("\n" + line(60));
            System.out.println("SUMMARY");
            System.out.println(stats);
        }
        System.out.println(line(60));
    }
    
    // Getters and Setters
    public Grade getAssignedGrade() { return assignedGrade; }
    
    public void setAssignedGrade(Grade assignedGrade) {
        this.assignedGrade = assignedGrade;
        if (assignedGrade != null) {
            System.out.println("Group Teacher " + this.getName() + " assigned to " + assignedGrade.getName());
        }
    }
    
    public boolean hasAssignedGrade() { return assignedGrade != null; }
    public int getStudentCount() { return assignedGrade == null ? 0 : assignedGrade.getStudents().size(); }
    
    private String line(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append("=");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "GroupTeacher{id='" + id + "', name='" + name + "', grade=" + 
               (assignedGrade != null ? assignedGrade.getName() : "None") + "}";
    }
}

// Clase auxiliar para estadísticas
class GradeStatistics {
    private String gradeName;
    private int totalStudents;
    private int totalObservations;
    private int totalDisciplinary;
    private int totalAcademic;
    
    public GradeStatistics(String gradeName, int totalStudents, int totalObservations, 
                          int totalDisciplinary, int totalAcademic) {
        this.gradeName = gradeName;
        this.totalStudents = totalStudents;
        this.totalObservations = totalObservations;
        this.totalDisciplinary = totalDisciplinary;
        this.totalAcademic = totalAcademic;
    }
    
    public double getAveragePerStudent() {
        return totalStudents == 0 ? 0 : (double) totalObservations / totalStudents;
    }
    
    @Override
    public String toString() {
        return "Grade: " + gradeName + "\n" +
               "Students: " + totalStudents + "\n" +
               "Total Observations: " + totalObservations + "\n" +
               "  Disciplinary: " + totalDisciplinary + "\n" +
               "  Academic: " + totalAcademic + "\n" +
               "Average: " + String.format("%.2f", getAveragePerStudent());
    }
}