package studentmanagement.dao;

import studentmanagement.exception.RecordNotFoundException;
import studentmanagement.model.Course;
import studentmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public int addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_name, credits) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, course.getCourseName());
            ps.setInt(2, course.getCredits());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public Course getCourseById(int id) throws SQLException, RecordNotFoundException {
        String sql = "SELECT * FROM courses WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        throw new RecordNotFoundException("No course found with ID " + id);
    }

    public List<Course> getAllCourses() throws SQLException {
        String sql = "SELECT * FROM courses ORDER BY id";
        List<Course> courses = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                courses.add(mapRow(rs));
            }
        }
        return courses;
    }

    public void updateCourse(Course course) throws SQLException, RecordNotFoundException {
        String sql = "UPDATE courses SET course_name = ?, credits = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCourseName());
            ps.setInt(2, course.getCredits());
            ps.setInt(3, course.getId());

            if (ps.executeUpdate() == 0) {
                throw new RecordNotFoundException("No course found with ID " + course.getId() + " to update.");
            }
        }
    }

    public void deleteCourse(int id) throws SQLException, RecordNotFoundException {
        String sql = "DELETE FROM courses WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new RecordNotFoundException("No course found with ID " + id + " to delete.");
            }
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        return new Course(rs.getInt("id"), rs.getString("course_name"), rs.getInt("credits"));
    }
}
