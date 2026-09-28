package studentmanagement;

import studentmanagement.exception.DuplicateEnrollmentException;
import studentmanagement.exception.RecordNotFoundException;
import studentmanagement.model.Course;
import studentmanagement.model.Student;
import studentmanagement.service.StudentService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point of the application. Shows a text menu in the console,
 * reads the user's choice, and calls the right method on StudentService.
 *
 * All the messy "what if the user types letters instead of a number?"
 * and "what if the database throws an error?" handling lives here,
 * so the service/DAO layers can stay clean.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentService service = new StudentService();

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   STUDENT MANAGEMENT SYSTEM");
        System.out.println("==================================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> viewAllStudents();
                    case 3 -> updateStudent();
                    case 4 -> deleteStudent();
                    case 5 -> searchStudent();
                    case 6 -> addCourse();
                    case 7 -> viewAllCourses();
                    case 8 -> enrollStudent();
                    case 9 -> unenrollStudent();
                    case 10 -> recordMarks();
                    case 11 -> listStudentsByCourse();
                    case 12 -> classRankingReport();
                    case 13 -> {
                        running = false;
                        System.out.println("Goodbye!");
                    }
                    default -> System.out.println("Invalid choice. Please pick a number from the menu.");
                }
            } catch (RecordNotFoundException | DuplicateEnrollmentException e) {
                // These are OUR custom, expected exceptions -> show a friendly message.
                System.out.println("[Notice] " + e.getMessage());
            } catch (SQLException e) {
                // Something went wrong talking to the database.
                // We handle it meaningfully instead of crashing or silently ignoring it.
                System.out.println("[Database error] " + e.getMessage());
            }

            System.out.println(); // blank line for readability
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("---------------------- MENU ----------------------");
        System.out.println(" 1. Add Student");
        System.out.println(" 2. View All Students");
        System.out.println(" 3. Update Student");
        System.out.println(" 4. Delete Student");
        System.out.println(" 5. Search Student (by ID or name)");
        System.out.println(" 6. Add Course");
        System.out.println(" 7. View All Courses");
        System.out.println(" 8. Enroll Student in Course");
        System.out.println(" 9. Unenroll Student from Course");
        System.out.println("10. Record / Update Marks");
        System.out.println("11. List Students by Course");
        System.out.println("12. Generate Class Ranking Report");
        System.out.println("13. Exit");
        System.out.println("----------------------------------------------------");
    }

    // ------------------- Menu actions -------------------

    private static void addStudent() throws SQLException {
        String name = readNonEmptyString("Name: ");
        String contact = readNonEmptyString("Contact number: ");
        String email = readNonEmptyString("Email: ");

        int newId = service.addStudent(name, contact, email);
        System.out.println("Student added successfully with ID: " + newId);
    }

    private static void viewAllStudents() throws SQLException {
        List<Student> students = service.listAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("All students:");
        for (Student s : students) {
            System.out.println(" " + s.displayInfo());
        }
    }

    private static void updateStudent() throws SQLException, RecordNotFoundException {
        int id = readInt("Student ID to update: ");
        String name = readNonEmptyString("New name: ");
        String contact = readNonEmptyString("New contact: ");
        String email = readNonEmptyString("New email: ");

        service.updateStudent(id, name, contact, email);
        System.out.println("Student " + id + " updated successfully.");
    }

    private static void deleteStudent() throws SQLException, RecordNotFoundException {
        int id = readInt("Student ID to delete: ");
        service.deleteStudent(id);
        System.out.println("Student " + id + " deleted successfully.");
    }

    private static void searchStudent() throws SQLException, RecordNotFoundException {
        System.out.println("Search by: 1) ID   2) Name");
        int option = readInt("Choice: ");

        if (option == 1) {
            int id = readInt("Enter student ID: ");
            Student s = service.searchStudent(id); // overloaded: int version
            System.out.println("Found: " + s.displayInfo());
        } else if (option == 2) {
            String name = readNonEmptyString("Enter (part of) the name: ");
            List<Student> results = service.searchStudent(name); // overloaded: String version
            if (results.isEmpty()) {
                System.out.println("No students matched \"" + name + "\".");
            } else {
                results.forEach(s -> System.out.println(" " + s.displayInfo()));
            }
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void addCourse() throws SQLException {
        String courseName = readNonEmptyString("Course name: ");
        int credits = readInt("Credits: ");

        int newId = service.addCourse(courseName, credits);
        System.out.println("Course added successfully with ID: " + newId);
    }

    private static void viewAllCourses() throws SQLException {
        List<Course> courses = service.listAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }
        System.out.println("All courses:");
        for (Course c : courses) {
            System.out.println(" " + c);
        }
    }

    private static void enrollStudent() throws SQLException, DuplicateEnrollmentException, RecordNotFoundException {
        int studentId = readInt("Student ID: ");
        int courseId = readInt("Course ID: ");
        service.enrollStudent(studentId, courseId);
        System.out.println("Student " + studentId + " enrolled in course " + courseId + ".");
    }

    private static void unenrollStudent() throws SQLException, RecordNotFoundException {
        int studentId = readInt("Student ID: ");
        int courseId = readInt("Course ID: ");
        service.unenrollStudent(studentId, courseId);
        System.out.println("Student " + studentId + " unenrolled from course " + courseId + ".");
    }

    private static void recordMarks() throws SQLException, RecordNotFoundException {
        int studentId = readInt("Student ID: ");
        int courseId = readInt("Course ID: ");
        double marks = readDouble("Marks (0-100): ");
        service.recordMarks(studentId, courseId, marks);
        System.out.println("Marks recorded successfully.");
    }

    private static void listStudentsByCourse() throws SQLException {
        int courseId = readInt("Course ID: ");
        List<Student> students = service.listStudentsByCourse(courseId);
        if (students.isEmpty()) {
            System.out.println("No students are enrolled in this course.");
            return;
        }
        System.out.println("Students enrolled in course " + courseId + ":");
        for (Student s : students) {
            System.out.println(" " + s.displayInfo());
        }
    }

    private static void classRankingReport() throws SQLException {
        int courseId = readInt("Course ID: ");
        List<Student> ranked = service.generateClassRanking(courseId);

        if (ranked.isEmpty()) {
            System.out.println("No students enrolled in this course yet.");
            return;
        }

        System.out.println("========== CLASS RANKING (Course " + courseId + ") ==========");
        int rank = 1;
        for (Student s : ranked) {
            double marks = s.getCourseMarks().getOrDefault(courseId, 0.0);
            System.out.printf(" Rank %-3d | %-20s | Marks: %.2f%n", rank, s.getName(), marks);
            rank++;
        }
    }

    // ------------------- Input validation helpers -------------------

    /**
     * Keeps asking until the user types a valid whole number.
     * This satisfies the "reject non-numeric input" requirement.
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (e.g. 85 or 85.5).");
            }
        }
    }

    /**
     * Keeps asking until the user types something that isn't blank.
     * This satisfies the "handle empty input" requirement.
     */
    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }
}
