package studentmanagement.service;

import studentmanagement.dao.CourseDAO;
import studentmanagement.dao.EnrollmentDAO;
import studentmanagement.dao.StudentDAO;
import studentmanagement.exception.DuplicateEnrollmentException;
import studentmanagement.exception.RecordNotFoundException;
import studentmanagement.model.Course;
import studentmanagement.model.Student;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The "brain" of the app that sits between Main (the menu) and the DAOs
 * (the database). It also keeps an in-memory cache so we can demonstrate
 * ArrayList + HashMap usage clearly:
 *
 *   - studentCache (HashMap<Integer, Student>): fast lookup by ID.
 *   - the ArrayList returned by listAllStudents() is an ordered list,
 *     useful for printing / iterating / sorting.
 *
 * NOTE: the database is always the single source of truth. The cache
 * is refreshed from the database every time listAllStudents() is called,
 * so the app still meets the "data must persist between runs" requirement.
 */
public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    // HashMap cache: id -> Student, refreshed on demand
    private final Map<Integer, Student> studentCache = new HashMap<>();

    // ---------------- Students ----------------

    public int addStudent(String name, String contact, String email) throws SQLException {
        Student student = new Student(0, name, contact, email);
        return studentDAO.addStudent(student);
    }

    public List<Student> listAllStudents() throws SQLException {
        List<Student> students = studentDAO.getAllStudents(); // ArrayList under the hood
        refreshCache(students);
        return students;
    }

    /**
     * Rebuilds the HashMap cache from a fresh list of students.
     */
    private void refreshCache(List<Student> students) {
        studentCache.clear();
        for (Student s : students) {
            studentCache.put(s.getId(), s);
        }
    }

    /**
     * OVERLOADED METHOD #1: search by numeric ID.
     * (POLYMORPHISM via method overloading - same method name,
     * different parameter type/behaviour.)
     */
    public Student searchStudent(int id) throws SQLException, RecordNotFoundException {
        return studentDAO.getStudentById(id);
    }

    /**
     * OVERLOADED METHOD #2: search by (partial) name.
     */
    public List<Student> searchStudent(String namePart) throws SQLException {
        return studentDAO.searchByName(namePart);
    }

    public void updateStudent(int id, String name, String contact, String email)
            throws SQLException, RecordNotFoundException {
        Student student = new Student(id, name, contact, email);
        studentDAO.updateStudent(student);
    }

    public void deleteStudent(int id) throws SQLException, RecordNotFoundException {
        studentDAO.deleteStudent(id);
        studentCache.remove(id);
    }

    // ---------------- Courses ----------------

    public int addCourse(String courseName, int credits) throws SQLException {
        return courseDAO.addCourse(new Course(0, courseName, credits));
    }

    public List<Course> listAllCourses() throws SQLException {
        return courseDAO.getAllCourses();
    }

    public Course getCourse(int courseId) throws SQLException, RecordNotFoundException {
        return courseDAO.getCourseById(courseId);
    }

    // ---------------- Enrollment / Marks ----------------

    public void enrollStudent(int studentId, int courseId)
            throws SQLException, DuplicateEnrollmentException, RecordNotFoundException {
        // make sure both exist first -- gives a clearer error message
        studentDAO.getStudentById(studentId);
        courseDAO.getCourseById(courseId);
        enrollmentDAO.enrollStudent(studentId, courseId);
    }

    public void unenrollStudent(int studentId, int courseId) throws SQLException, RecordNotFoundException {
        enrollmentDAO.unenrollStudent(studentId, courseId);
    }

    public void recordMarks(int studentId, int courseId, double marks) throws SQLException, RecordNotFoundException {
        enrollmentDAO.recordMarks(studentId, courseId, marks);
    }

    public List<Student> listStudentsByCourse(int courseId) throws SQLException {
        return enrollmentDAO.getStudentsByCourse(courseId);
    }

    /**
     * Builds the class ranking report (List sorted with Comparator inside the DAO).
     */
    public List<Student> generateClassRanking(int courseId) throws SQLException {
        return enrollmentDAO.getClassRanking(courseId);
    }
}
