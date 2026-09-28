package studentmanagement.dao;

import studentmanagement.exception.DuplicateEnrollmentException;
import studentmanagement.exception.RecordNotFoundException;
import studentmanagement.model.Student;
import studentmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Handles the "enrollments" table, which links students <-> courses
 * and stores marks. This is where "Enroll/Unenroll" and "Record marks"
 * and the "Class ranking" report live.
 */
public class EnrollmentDAO {

    /**
     * Enrolls a student in a course, after checking they are not
     * already enrolled (custom exception: DuplicateEnrollmentException).
     */
    public void enrollStudent(int studentId, int courseId) throws SQLException, DuplicateEnrollmentException {
        String checkSql = "SELECT id FROM enrollments WHERE student_id = ? AND course_id = ?";
        String insertSql = "INSERT INTO enrollments (student_id, course_id, marks) VALUES (?, ?, NULL)";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, studentId);
                check.setInt(2, courseId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        throw new DuplicateEnrollmentException(
                                "Student " + studentId + " is already enrolled in course " + courseId);
                    }
                }
            }

            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setInt(1, studentId);
                insert.setInt(2, courseId);
                insert.executeUpdate();
            }
        }
    }

    /**
     * Removes a student's enrollment from a course.
     */
    public void unenrollStudent(int studentId, int courseId) throws SQLException, RecordNotFoundException {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);

            if (ps.executeUpdate() == 0) {
                throw new RecordNotFoundException(
                        "No enrollment found for student " + studentId + " in course " + courseId);
            }
        }
    }

    /**
     * Records (or updates, if it already exists) the marks a student
     * got in a particular course.
     */
    public void recordMarks(int studentId, int courseId, double marks) throws SQLException, RecordNotFoundException {
        String sql = "UPDATE enrollments SET marks = ? WHERE student_id = ? AND course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, marks);
            ps.setInt(2, studentId);
            ps.setInt(3, courseId);

            if (ps.executeUpdate() == 0) {
                throw new RecordNotFoundException(
                        "Student " + studentId + " is not enrolled in course " + courseId +
                        " -- enroll them first before recording marks.");
            }
        }
    }

    /**
     * Returns every student enrolled in a given course, each Student
     * object pre-loaded with their marks for that course so the
     * caller can print averages / build a report.
     */
    public List<Student> getStudentsByCourse(int courseId) throws SQLException {
        String sql = "SELECT s.id, s.name, s.contact, s.email, e.marks " +
                     "FROM enrollments e JOIN students s ON e.student_id = s.id " +
                     "WHERE e.course_id = ?";

        List<Student> students = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student(
                            rs.getInt("id"), rs.getString("name"),
                            rs.getString("contact"), rs.getString("email"));

                    double marks = rs.getDouble("marks");
                    if (!rs.wasNull()) {
                        s.addOrUpdateMark(courseId, marks);
                    }
                    students.add(s);
                }
            }
        }
        return students;
    }

    /**
     * Builds a class ranking report for a course: students sorted by
     * marks in that course, highest first.
     *
     * Demonstrates: Comparator + sorting a List (Collections Framework requirement).
     */
    public List<Student> getClassRanking(int courseId) throws SQLException {
        List<Student> students = getStudentsByCourse(courseId);

        students.sort(new Comparator<Student>() {
            @Override
            public int compare(Student a, Student b) {
                Double marksA = a.getCourseMarks().getOrDefault(courseId, -1.0);
                Double marksB = b.getCourseMarks().getOrDefault(courseId, -1.0);
                return marksB.compareTo(marksA); // descending order (highest marks first)
            }
        });

        return students;
    }
}
