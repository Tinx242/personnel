package personnel;

public class DatesIncoherentesException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public DatesIncoherentesException(String message) {
        super(message);
    }
}