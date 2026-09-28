package studentmanagement.exception;

/**
 * A second CUSTOM CHECKED EXCEPTION, thrown when trying to enroll
 * a student into a course they are already enrolled in.
 */
public class DuplicateEnrollmentException extends Exception {

    public DuplicateEnrollmentException(String message) {
        super(message);
    }
}
