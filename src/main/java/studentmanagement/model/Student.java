package studentmanagement.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Student extends Person (INHERITANCE).
 * It overrides displayInfo() and getRole() (POLYMORPHISM - method overriding).
 *
 * It also keeps an in-memory Map of courseId -> marks so we can quickly
 * calculate an average without hitting the database every time
 * (COLLECTIONS FRAMEWORK usage: HashMap).
 */
public class Student extends Person {

    private String email;

    // key = courseId, value = marks obtained in that course
    private Map<Integer, Double> courseMarks = new HashMap<>();

    public Student(int id, String name, String contact, String email) {
        super(id, name, contact); // calls Person's constructor
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<Integer, Double> getCourseMarks() {
        return courseMarks;
    }

    public void setCourseMarks(Map<Integer, Double> courseMarks) {
        this.courseMarks = courseMarks;
    }

    public void addOrUpdateMark(int courseId, double marks) {
        courseMarks.put(courseId, marks);
    }

    /**
     * Calculates the average of all recorded marks for this student.
     * Returns 0.0 if no marks have been recorded yet.
     */
    public double calculateAverage() {
        if (courseMarks.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (double m : courseMarks.values()) {
            total += m;
        }
        return total / courseMarks.size();
    }

    // ---- POLYMORPHISM: overriding the abstract methods from Person ----
    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String displayInfo() {
        return String.format(
                "ID: %-4d | Name: %-20s | Contact: %-15s | Email: %-25s | Average: %.2f",
                id, name, contact, email, calculateAverage());
    }

    @Override
    public String toString() {
        return displayInfo();
    }
}
