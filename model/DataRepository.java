package model;

import java.util.*;

public class DataRepository {
    private Map<String, User> users;
    private Map<String, Observation> observations;
    private static DataRepository instance;

    private DataRepository() {
        users = new HashMap<>();
        observations = new HashMap<>();
    }

    public static DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        for (User user : users.values()) {
            if (user instanceof Teacher) {
                teachers.add((Teacher) user);
            }
        }
        return teachers;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        for (User user : users.values()) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }
        return students;
    }

    public void addObservation(Observation observation) {
        observations.put(observation.getId(), observation);
    }

    public Observation getObservation(String id) {
        return observations.get(id);
    }

    public List<Observation> getAllObservations() {
        return new ArrayList<>(observations.values());
    }

    public List<Observation> getObservationsByStudent(String studentId) {
        List<Observation> result = new ArrayList<>();
        for (Observation obs : observations.values()) {
            if (obs.getStudentId().equals(studentId)) {
                result.add(obs);
            }
        }
        return result;
    }

    public List<Observation> getObservationsByTeacher(String teacherId) {
        List<Observation> result = new ArrayList<>();
        for (Observation obs : observations.values()) {
            if (obs.getTeacherId().equals(teacherId)) {
                result.add(obs);
            }
        }
        return result;
    }

    public List<Observation> getActiveObservations() {
        List<Observation> result = new ArrayList<>();
        for (Observation obs : observations.values()) {
            if (obs.getStatus() == ObservationStatus.ACTIVE) {
                result.add(obs);
            }
        }
        return result;
    }

    public void clearAll() {
        users.clear();
        observations.clear();
    }
}
