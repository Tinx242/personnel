package personnel;

/**
 * Exception levée lorsque les dates d'un employé sont incohérentes.
 */
public class ExceptionsEmploye {
    
    /**
     * Exception levée lorsque la date de départ est antérieure à la date d'arrivée.
     */
    public static class DatesIncoherentes extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
        public DatesIncoherentes() {
            super("La date de départ ne peut pas être antérieure à la date d'arrivée");
        }
        
        public DatesIncoherentes(String message) {
            super(message);
        }
    }
    
    /**
     * Exception levée lorsque la date d'arrivée est nulle alors qu'elle est requise.
     */
    public static class DateArriveeNulle extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
        public DateArriveeNulle() {
            super("La date d'arrivée ne peut pas être nulle");
        }
        
        public DateArriveeNulle(String message) {
            super(message);
        }
    }
}