package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import personnel.Employe;
import personnel.ExceptionsEmploye;
import personnel.GestionPersonnel;
import personnel.Ligue;
import personnel.SauvegardeImpossible;
import java.time.LocalDate;

/**
 * Tests unitaires pour les dates d'arrivée et de départ des employés.
 */
public class TestDatesEmploye 
{
	private GestionPersonnel gestionPersonnel;
	private Ligue ligue;
	
	@BeforeEach
	public void setUp() throws SauvegardeImpossible
	{
		gestionPersonnel = GestionPersonnel.getGestionPersonnel();
		ligue = gestionPersonnel.addLigue("Ligue Test");
	}
	
	/**
	 * Test de création d'un employé avec des dates valides.
	 */
	@Test
	public void testCreationEmployeAvecDatesValides()
	{
		LocalDate dateArrivee = LocalDate.of(2023, 1, 15);
		LocalDate dateDepart = LocalDate.of(2024, 6, 30);
		
		Employe employe = ligue.addEmploye("Dupont", "Jean", "jean.dupont@test.fr", 
				"password123", dateArrivee, dateDepart);
		
		assertEquals(dateArrivee, employe.getDateArrivee());
		assertEquals(dateDepart, employe.getDateDepart());
	}
	
	/**
	 * Test de création d'un employé avec dates nulles (autorisé).
	 */
	@Test
	public void testCreationEmployeAvecDatesNulles()
	{
		Employe employe = ligue.addEmploye("Martin", "Sophie", "sophie.martin@test.fr", 
				"password456", null, null);
		
		assertNull(employe.getDateArrivee());
		assertNull(employe.getDateDepart());
	}
	
	/**
	 * Test de création d'un employé avec date d'arrivée uniquement.
	 */
	@Test
	public void testCreationEmployeAvecDateArriveeSeule()
	{
		LocalDate dateArrivee = LocalDate.of(2023, 3, 1);
		
		Employe employe = ligue.addEmploye("Bernard", "Paul", "paul.bernard@test.fr", 
				"password789", dateArrivee, null);
		
		assertEquals(dateArrivee, employe.getDateArrivee());
		assertNull(employe.getDateDepart());
	}
	
	/**
	 * Test de création d'un employé avec des dates identiques (cas limite valide).
	 */
	@Test
	public void testCreationEmployeAvecDatesIdentiques()
	{
		LocalDate date = LocalDate.of(2023, 5, 15);
		
		Employe employe = ligue.addEmploye("Petit", "Marie", "marie.petit@test.fr", 
				"password000", date, date);
		
		assertEquals(date, employe.getDateArrivee());
		assertEquals(date, employe.getDateDepart());
	}
	
	/**
	 * Test de création d'un employé avec date de départ antérieure à date d'arrivée.
	 * Doit lever une exception DatesIncoherentes.
	 */
	@Test
	public void testCreationEmployeAvecDatesIncoherentes()
	{
		LocalDate dateArrivee = LocalDate.of(2024, 6, 30);
		LocalDate dateDepart = LocalDate.of(2023, 1, 15);
		
		assertThrows(ExceptionsEmploye.DatesIncoherentes.class, () -> {
			ligue.addEmploye("Durand", "Pierre", "pierre.durand@test.fr", 
					"password111", dateArrivee, dateDepart);
		});
	}
	
	/**
	 * Test de modification de la date d'arrivée avec une valeur valide.
	 */
	@Test
	public void testModificationDateArriveeValide()
	{
		LocalDate dateArrivee = LocalDate.of(2023, 1, 15);
		LocalDate dateDepart = LocalDate.of(2024, 6, 30);
		
		Employe employe = ligue.addEmploye("Moreau", "Luc", "luc.moreau@test.fr", 
				"password222", dateArrivee, dateDepart);
		
		LocalDate nouvelleDateArrivee = LocalDate.of(2023, 2, 1);
		employe.setDateArrivee(nouvelleDateArrivee);
		
		assertEquals(nouvelleDateArrivee, employe.getDateArrivee());
	}
	
	/**
	 * Test de modification de la date d'arrivée avec une valeur incohérente.
	 * Doit lever une exception DatesIncoherentes.
	 */
	@Test
	public void testModificationDateArriveeIncoherente()
	{
		LocalDate dateArrivee = LocalDate.of(2023, 1, 15);
		LocalDate dateDepart = LocalDate.of(2024, 6, 30);
		
		Employe employe = ligue.addEmploye("Simon", "Claire", "claire.simon@test.fr", 
				"password333", dateArrivee, dateDepart);
		
		LocalDate nouvelleDateArrivee = LocalDate.of(2025, 1, 1);
		
		assertThrows(ExceptionsEmploye.DatesIncoherentes.class, () -> {
			employe.setDateArrivee(nouvelleDateArrivee);
		});
		
		// Vérifier que la date n'a pas changé
		assertEquals(dateArrivee, employe.getDateArrivee());
	}
	
	/**
	 * Test de modification de la date de départ avec une valeur valide.
	 */
	@Test
	public void testModificationDateDepartValide()
	{
		LocalDate dateArrivee = LocalDate.of(2023, 1, 15);
		LocalDate dateDepart = LocalDate.of(2024, 6, 30);
		
		Employe employe = ligue.addEmploye("Laurent", "Anne", "anne.laurent@test.fr", 
				"password444", dateArrivee, dateDepart);
		
		LocalDate nouvelleDateDepart = LocalDate.of(2024, 12, 31);
		employe.setDateDepart(nouvelleDateDepart);
		
		assertEquals(nouvelleDateDepart, employe.getDateDepart());
	}
	
	/**
	 * Test de modification de la date de départ avec une valeur incohérente.
	 * Doit lever une exception DatesIncoherentes.
	 */
	@Test
	public void testModificationDateDepartIncoherente()
	{
		LocalDate dateArrivee = LocalDate.of(2023, 1, 15);
		LocalDate dateDepart = LocalDate.of(2024, 6, 30);
		
		Employe employe = ligue.addEmploye("Girard", "Thomas", "thomas.girard@test.fr", 
				"password555", dateArrivee, dateDepart);
		
		LocalDate nouvelleDateDepart = LocalDate.of(2022, 12, 31);
		
		assertThrows(ExceptionsEmploye.DatesIncoherentes.class, () -> {
			employe.setDateDepart(nouvelleDateDepart);
		});
		
		// Vérifier que la date n'a pas changé
		assertEquals(dateDepart, employe.getDateDepart());
	}
	
	/**
	 * Test de modification des dates avec passage à null.
	 */
	@Test
	public void testModificationDatesVersNull()
	{
		LocalDate dateArrivee = LocalDate.of(2023, 1, 15);
		LocalDate dateDepart = LocalDate.of(2024, 6, 30);
		
		Employe employe = ligue.addEmploye("Roux", "Julie", "julie.roux@test.fr", 
				"password666", dateArrivee, dateDepart);
		
		employe.setDateDepart(null);
		assertNull(employe.getDateDepart());
		
		employe.setDateArrivee(null);
		assertNull(employe.getDateArrivee());
	}
	
	/**
	 * Test avec un employé actuel (pas encore de date de départ).
	 */
	@Test
	public void testEmployeActuelSansDateDepart()
	{
		LocalDate dateArrivee = LocalDate.of(2023, 1, 15);
		
		Employe employe = ligue.addEmploye("Vincent", "Marc", "marc.vincent@test.fr", 
				"password777", dateArrivee, null);
		
		assertEquals(dateArrivee, employe.getDateArrivee());
		assertNull(employe.getDateDepart());
		
		// Vérifier qu'on peut ajouter une date de départ ultérieure
		LocalDate dateDepart = LocalDate.of(2025, 12, 31);
		employe.setDateDepart(dateDepart);
		assertEquals(dateDepart, employe.getDateDepart());
	}
}