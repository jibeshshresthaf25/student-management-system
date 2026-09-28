package studentmanagement.model;

/**
 * Represents a course that students can enroll in.
 * Plain encapsulated class: private fields + public getters/setters.
 */
public class Course {

    private int id;
    private String courseName;
    private int credits;

    public Course(int id, String courseName, int credits) {
        this.id = id;
        this.courseName = courseName;
        this.credits = credits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return String.format("ID: %-4d | Course: %-25s | Credits: %d", id, courseName, credits);
    }
}
