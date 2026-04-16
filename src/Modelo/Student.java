package Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Student extends User{
    
    // ============================ ATRIBUTOS ============================
    
    private Grade grade;                     // Grade/class the student belongs to
    private List<Observation> observations;  // List of observations (disciplinary and academic)
    
    // ============================ CONSTRUCTOR ============================
    
    /**
     * Constructor for a student
     * @param id Unique identifier (e.g., "EST-001")
     * @param name Full name of the student
     * @param password Initial password
     */
    public Student(String id, String name, String password) {
        super(id, name, password);
        this.observations = new ArrayList<>();
        this.grade = null;
    }
    
    // ============================ MÉTODOS PRINCIPALES ============================
    
    /**
     * Gets the role of the user
     * @return "STUDENT"
     */
    @Override
    public String getRole() {
        return "STUDENT";
    }
    
    /**
     * RF-8: Views the student's digital record (hoja de vida)
     * Returns all observations sorted by date (newest first)
     * Students can only view their OWN observations (RF-3)
     * @return Unmodifiable list of observations sorted by date descending
     */
    public List<Observation> viewRecord() {
        List<Observation> sortedObservations = new ArrayList<>(observations);
        // Sort by date descending (newest first)
        sortedObservations.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        return Collections.unmodifiableList(sortedObservations);
    }
    
    /**
     * Adds an observation to the student's record
     * @param observation Observation to add (disciplinary or academic)
     */
    public void addObservation(Observation observation) {
        if (observation == null) {
            throw new IllegalArgumentException("Observation cannot be null");
        }
        observations.add(observation);
    }
    
    /**
     * Gets all observations (not sorted - internal use)
     * @return List of observations
     */
    public List<Observation> getObservations() {
        return observations;
    }
    
    // ============================ MÉTODOS DE REPORTE ============================
    
    /**
     * Gets only active observations (not cancelled)
     * @return List of active observations
     */
    public List<Observation> getActiveObservations() {
        List<Observation> active = new ArrayList<>();
        for (Observation obs : observations) {
            if (obs.isActive()) {
                active.add(obs);
            }
        }
        return Collections.unmodifiableList(active);
    }
    
    /**
     * Gets only cancelled observations
     * @return List of cancelled observations
     */
    public List<Observation> getCancelledObservations() {
        List<Observation> cancelled = new ArrayList<>();
        for (Observation obs : observations) {
            if (!obs.isActive()) {
                cancelled.add(obs);
            }
        }
        return Collections.unmodifiableList(cancelled);
    }
    
    /**
     * Gets only disciplinary observations
     * @return List of disciplinary observations
     */
    public List<DisciplinaryObservation> getDisciplinaryObservations() {
        List<DisciplinaryObservation> disciplinary = new ArrayList<>();
        for (Observation obs : observations) {
            if (obs instanceof DisciplinaryObservation) {
                disciplinary.add((DisciplinaryObservation) obs);
            }
        }
        return Collections.unmodifiableList(disciplinary);
    }
    
    /**
     * Gets only academic observations
     * @return List of academic observations
     */
    public List<AcademicObservation> getAcademicObservations() {
        List<AcademicObservation> academic = new ArrayList<>();
        for (Observation obs : observations) {
            if (obs instanceof AcademicObservation) {
                academic.add((AcademicObservation) obs);
            }
        }
        return Collections.unmodifiableList(academic);
    }
    
    /**
     * Gets total number of observations
     * @return Total observations count
     */
    public int getTotalObservations() {
        return observations.size();
    }
    
    /**
     * Gets number of active observations
     * @return Active observations count
     */
    public int getActiveObservationsCount() {
        int count = 0;
        for (Observation obs : observations) {
            if (obs.isActive()) {
                count++;
            }
        }
        return count;
    }
    
    // ============================ GETTERS Y SETTERS ============================
    
    public Grade getGrade() {
        return grade;
    }
    
    public void setGrade(Grade grade) {
        this.grade = grade;
    }
    
    // ============================ MÉTODOS UTILITARIOS ============================
    
    /**
     * Prints the student's complete record to console
     */
    public void printRecord() {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("📖 ACADEMIC RECORD");
        System.out.println("═".repeat(60));
        System.out.println("Student: " + name);
        System.out.println("ID: " + id);
        System.out.println("Grade: " + (grade != null ? grade.getName() : "Not assigned"));
        System.out.println("Status: " + (active ? "Active" : "Inactive"));
        System.out.println("Total Observations: " + observations.size());
        System.out.println("─".repeat(60));
        
        if (observations.isEmpty()) {
            System.out.println("   No observations recorded");
        } else {
            int index = 1;
            for (Observation obs : viewRecord()) {
                System.out.println("\n[" + index++ + "]");
                System.out.println(obs);
            }
        }
        System.out.println("═".repeat(60));
    }
    
    /**
     * Checks if the student has any observation
     * @return true if has at least one observation
     */
    public boolean hasObservations() {
        return !observations.isEmpty();
    }
    
    /**
     * Checks if the student has any active observation
     * @return true if has at least one active observation
     */
    public boolean hasActiveObservations() {
        return getActiveObservationsCount() > 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Student student = (Student) obj;
        return Objects.equals(id, student.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
    
    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s', grade='%s', active=%s, observations=%d}",
                id, name, grade != null ? grade.getName() : "None", active, observations.size());
    }
}
