package in.vidyasetu.exception;

/**
 * Thrown when a business rule is violated.
 * Examples: voiding a payment that's already voided,
 * adding a student beyond plan limit, marking attendance on a holiday.
 */
public class BusinessRuleException extends RuntimeException {
    private final String code;

    public BusinessRuleException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
