package studentmanagement.model;

/**
 * Represents one row of the "enrollments" table: a link between
 * a student and a course, plus the marks obtained (nullable - a
 * student can be enrolled before marks are recorded).
 */
public class Enrollment {

    private int id;
    private int studentId;
    private int courseId;
    private Double marks; // Double (wrapper) so it can be null = "not graded yet"

    public Enrollment(int id, int studentId, int courseId, Double marks) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.marks = marks;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        String markStr = (marks == null) ? "Not graded" : String.valueOf(marks);
        return "Enrollment{studentId=" + studentId + ", courseId=" + courseId + ", marks=" + markStr + "}";
    }
}
