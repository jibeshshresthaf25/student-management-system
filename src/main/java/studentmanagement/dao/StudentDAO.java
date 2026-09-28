package studentmanagement.dao;

import studentmanagement.exception.RecordNotFoundException;
import studentmanagement.model.Student;
import studentmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the "students" table.
 * This is the ONLY class that talks directly to the database for students.
 * Every method:
 *   - uses try-with-resources so Connection/PreparedStatement/ResultSet
 *     are always closed, even if something goes wrong.
 *   - uses PreparedStatement with "?" placeholders instead of gluing
 *     Strings together, which prevents SQL injection.
 */
public class StudentDAO {

    // ---------------- CREATE ----------------
    public int addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, contact, email) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getContact());
            ps.setString(3, student.getEmail());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1); // the new auto-generated student id
                }
            }
        }
        return -1;
    }

    // ---------------- READ (single) ----------------
    public Student getStudentById(int id) throws SQLException, RecordNotFoundException {
        String sql = "SELECT * FROM students WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        throw new RecordNotFoundException("No student found with ID " + id);
    }

    // ---------------- READ (search by name - overloaded search) ----------------
    public List<Student> searchByName(String namePart) throws SQLException {
        String sql = "SELECT * FROM students WHERE name LIKE ?";
        List<Student> results = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + namePart + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    // ---------------- READ (all) ----------------
    public List<Student> getAllStudents() throws SQLException {
        String sql = "SELECT * FROM students ORDER BY id";
        List<Student> students = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                students.add(mapRow(rs));
            }
        }
        return students;
    }

    // ---------------- UPDATE ----------------
    public void updateStudent(Student student) throws SQLException, RecordNotFoundException {
        String sql = "UPDATE students SET name = ?, contact = ?, email = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getContact());
            ps.setString(3, student.getEmail());
            ps.setInt(4, student.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new RecordNotFoundException("No student found with ID " + student.getId() + " to update.");
            }
        }
    }

    // ---------------- DELETE ----------------
    public void deleteStudent(int id) throws SQLException, RecordNotFoundException {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new RecordNotFoundException("No student found with ID " + id + " to delete.");
            }
        }
    }

    // ---------------- helper ----------------
    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("contact"),
                rs.getString("email")
        );
    }
}
