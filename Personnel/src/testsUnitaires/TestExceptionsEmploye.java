package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import personnel.Employe;
import personnel.ExceptionsEmploye;
import personnel.GestionPersonnel;
import personnel.ImpossibleDeSupprimerRoot;
import personnel.Ligue;

import java.time.LocalDate;

public class TestExceptionsEmploye {
    
    private GestionPersonnel gestionPersonnel;
    private Ligue ligue;
    private Employe root;
    
    @BeforeEach
    public void setUp() throws Exception {
        gestionPersonnel = GestionPersonnel.getGestionPersonnel();
        root = gestionPersonnel.getRoot();
        ligue = gestionPersonnel.addLigue("Ligue de Football");
    }
    
    /**
     * Test de l'exception DatesIncoherentes lors de la création d'un employé
     * avec une date de départ antérieure à la date d'arrivée
     */
    @Test
    public void testDatesIncoherentesConstructeur() {
        LocalDate dateArrivee = LocalDate.of(2024, 1, 15);
        LocalDate dateDepart = LocalDate.of(2023, 12, 1);
        
        assertThrows(ExceptionsEmploye.DatesIncoherentes.class, () -> {
            new Employe(gestionPersonnel, ligue, "Dupont", "Jean", 
                       "jean.dupont@mail.com", "password123", 
                       dateArrivee, dateDepart);
        }, "Une exception DatesIncoherentes devrait être levée");
    }
    
    /**
     * Test de l'exception DatesIncoherentes avec setDateArrivee
     */
    @Test
    public void testDatesIncoherentesSetDateArrivee() throws Exception {
        Employe employe = ligue.addEmploye("Martin", "Sophie", 
                                          "sophie.martin@mail.com", "pass456", 
                                          LocalDate.of(2023, 1, 1), 
                                          LocalDate.of(2024, 1, 1));
        
        LocalDate nouvelleDateArrivee = LocalDate.of(2024, 6, 1);
        
        assertThrows(ExceptionsEmploye.DatesIncoherentes.class, () -> {
            employe.setDateArrivee(nouvelleDateArrivee);
        }, "Une exception devrait être levée car la nouvelle date d'arrivée est après la date de départ");
    }
    
    /**
     * Test de l'exception DatesIncoherentes avec setDateDepart
     */
    @Test
    public void testDatesIncoherentesSetDateDepart() throws Exception {
        Employe employe = ligue.addEmploye("Durand", "Pierre", 
                                          "pierre.durand@mail.com", "pass789", 
                                          LocalDate.of(2023, 6, 1), 
                                          null);
        
        LocalDate nouvelleDateDepart = LocalDate.of(2023, 1, 1);
        
        assertThrows(ExceptionsEmploye.DatesIncoherentes.class, () -> {
            employe.setDateDepart(nouvelleDateDepart);
        }, "Une exception devrait être levée car la date de départ est avant la date d'arrivée");
    }
    
    /**
     * Test de l'exception ImpossibleDeSupprimerRoot
     */
    @Test
    public void testImpossibleDeSupprimerRoot() {
        assertThrows(ImpossibleDeSupprimerRoot.class, () -> {
            root.remove();
        }, "Il devrait être impossible de supprimer le root");
    }
    
    /**
     * Test que la création avec des dates cohérentes ne lève pas d'exception
     */
    @Test
    public void testDatesCoherentesConstructeur() {
        LocalDate dateArrivee = LocalDate.of(2023, 1, 1);
        LocalDate dateDepart = LocalDate.of(2024, 1, 1);
        
        assertDoesNotThrow(() -> {
            new Employe(gestionPersonnel, ligue, "Test", "Test", 
                       "test@mail.com", "password", 
                       dateArrivee, dateDepart);
        }, "Aucune exception ne devrait être levée avec des dates cohérentes");
    }
    
    /**
     * Test avec dates nulles (ne devrait pas lever d'exception)
     */
    @Test
    public void testDatesNulles() {
        assertDoesNotThrow(() -> {
            new Employe(gestionPersonnel, ligue, "Test", "Test", 
                       "test@mail.com", "password", null, null);
        }, "Aucune exception ne devrait être levée avec des dates nulles");
    }
    
    /**
     * Test avec date d'arrivée nulle et date de départ définie
     */
    @Test
    public void testDateArriveeNulleDateDepartDefinie() {
        assertDoesNotThrow(() -> {
            new Employe(gestionPersonnel, ligue, "Test", "Test", 
                       "test@mail.com", "password", 
                       null, LocalDate.of(2024, 1, 1));
        }, "Aucune exception ne devrait être levée");
    }
    
    /**
     * Test modification de date d'arrivée vers null avec date de départ existante
     */
    @Test
    public void testSetDateArriveeNullAvecDateDepart() throws Exception {
        Employe employe = ligue.addEmploye("Toto", "Titi", 
                                          "toto@mail.com", "pass", 
                                          LocalDate.of(2023, 1, 1), 
                                          LocalDate.of(2024, 1, 1));
        
        assertDoesNotThrow(() -> {
            employe.setDateArrivee(null);
        }, "Aucune exception ne devrait être levée");
    }
    
    /**
     * Test que la suppression d'un employé normal ne lève pas d'exception
     */
    @Test
    public void testSuppressionEmployeNormal() throws Exception {
        Employe employe = ligue.addEmploye("Normal", "User", 
                                          "normal@mail.com", "pass", 
                                          LocalDate.now(), null);
        
        assertDoesNotThrow(() -> {
            employe.remove();
        }, "La suppression d'un employé normal ne devrait pas lever d'exception");
    }
    
    /**
     * Test que la suppression d'un administrateur ne lève pas d'exception
     */
    @Test
    public void testSuppressionAdministrateur() throws Exception {
        Employe admin = ligue.addEmploye("Admin", "User", 
                                        "admin@mail.com", "pass", 
                                        LocalDate.now(), null);
        ligue.setAdministrateur(admin);
        
        assertDoesNotThrow(() -> {
            admin.remove();
        }, "La suppression d'un administrateur ne devrait pas lever d'exception");
        
        // Vérifier que le root a récupéré les droits d'administration
        assertEquals(root, ligue.getAdministrateur(), 
                    "Le root devrait être l'administrateur après suppression");
    }
}