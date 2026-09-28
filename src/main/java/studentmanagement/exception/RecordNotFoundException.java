package studentmanagement.exception;

/**
 * A CUSTOM CHECKED EXCEPTION.
 *
 * Thrown whenever the code tries to look up, update, or delete a
 * student/course/enrollment that does not exist in the database
 * (e.g. searching for student ID 999 when no such student exists).
 *
 * It extends Exception (not RuntimeException) so callers are FORCED
 * to either catch it or declare "throws RecordNotFoundException",
 * which makes the assignment requirement of "meaningful exception
 * handling" explicit rather than silent.
 */
public class RecordNotFoundException extends Exception {

    public RecordNotFoundException(String message) {
        super(message);
    }
}
