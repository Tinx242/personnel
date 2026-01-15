package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import personnel.*;

class testLigue
{
	GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();

	@Test
	void createLigue() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		assertEquals("Fléchettes", ligue.getNom());
	}

	@Test
	void addEmploye() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", null, null);
		assertEquals(employe, ligue.getEmployes().first());
	}
	
	// ========== Tests des getters et setters ==========
	
	@Test
	void getNom() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		assertEquals("Tennis", ligue.getNom());
	}
	
	@Test
	void setNom() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		ligue.setNom("Football");
		assertEquals("Football", ligue.getNom());
	}
	
	@Test
	void getAdministrateurParDefaut() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		assertEquals(gestionPersonnel.getRoot(), ligue.getAdministrateur());
	}
	
	// ========== Tests de setAdministrateur ==========
	
	@Test
	void setAdministrateurAvecEmployeDeLaLigue() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Employe employe = ligue.addEmploye("Dupont", "Jean", "jean@mail.com", "pass123", LocalDate.now(), null);
		
		ligue.setAdministrateur(employe);
		assertEquals(employe, ligue.getAdministrateur());
	}
	
	@Test
	void setAdministrateurAvecRoot() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Employe employe = ligue.addEmploye("Martin", "Sophie", "sophie@mail.com", "pass456", LocalDate.now(), null);
		ligue.setAdministrateur(employe);
		
		ligue.setAdministrateur(gestionPersonnel.getRoot());
		assertEquals(gestionPersonnel.getRoot(), ligue.getAdministrateur());
	}
	
	@Test
	void setAdministrateurAvecEmployeAutreLigue() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Ligue autreLigue = gestionPersonnel.addLigue("Basket");
		Employe employeAutreLigue = autreLigue.addEmploye("Durand", "Pierre", "pierre@mail.com", "pass789", LocalDate.now(), null);
		
		assertThrows(DroitsInsuffisants.class, () -> {
			ligue.setAdministrateur(employeAutreLigue);
		});
	}
	
	@Test
	void changementAdministrateur() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Employe employe1 = ligue.addEmploye("Admin1", "Premier", "admin1@mail.com", "pass1", LocalDate.now(), null);
		Employe employe2 = ligue.addEmploye("Admin2", "Second", "admin2@mail.com", "pass2", LocalDate.now(), null);
		
		ligue.setAdministrateur(employe1);
		assertEquals(employe1, ligue.getAdministrateur());
		
		ligue.setAdministrateur(employe2);
		assertEquals(employe2, ligue.getAdministrateur());
		assertFalse(employe1.estAdmin(ligue));
		assertTrue(employe2.estAdmin(ligue));
	}
	
	// ========== Tests d'ajout d'employés ==========
	
	@Test
	void addPlusieursEmployes() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Employe emp1 = ligue.addEmploye("Aaa", "Premier", "aaa@mail.com", "pass1", LocalDate.now(), null);
		Employe emp2 = ligue.addEmploye("Zzz", "Dernier", "zzz@mail.com", "pass2", LocalDate.now(), null);
		Employe emp3 = ligue.addEmploye("Mmm", "Milieu", "mmm@mail.com", "pass3", LocalDate.now(), null);
		
		assertEquals(3, ligue.getEmployes().size());
		
		Employe[] tableau = ligue.getEmployes().toArray(new Employe[0]);
		assertEquals(emp1, tableau[0]);
		assertEquals(emp3, tableau[1]);
		assertEquals(emp2, tableau[2]);
	}
	
	@Test
	void getEmployesRetourneCollectionNonModifiable() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		ligue.addEmploye("Test", "User", "test@mail.com", "pass", LocalDate.now(), null);
		
		assertThrows(UnsupportedOperationException.class, () -> {
			ligue.getEmployes().clear();
		});
	}
	
	// ========== Tests de suppression d'employés ==========
	
	@Test
	void suppressionEmployeSimple() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Employe employe = ligue.addEmploye("Temp", "User", "temp@mail.com", "pass", LocalDate.now(), null);
		
		assertTrue(ligue.getEmployes().contains(employe));
		employe.remove();
		assertFalse(ligue.getEmployes().contains(employe));
	}
	
	@Test
	void suppressionEmployeAdministrateur() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Employe admin = ligue.addEmploye("Admin", "User", "admin@mail.com", "pass", LocalDate.now(), null);
		ligue.setAdministrateur(admin);
		
		assertEquals(admin, ligue.getAdministrateur());
		admin.remove();
		
		assertEquals(gestionPersonnel.getRoot(), ligue.getAdministrateur());
		assertFalse(ligue.getEmployes().contains(admin));
	}
	
	@Test
	void suppressionPlusieursEmployes() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Employe emp1 = ligue.addEmploye("Emp1", "User", "emp1@mail.com", "pass1", LocalDate.now(), null);
		Employe emp2 = ligue.addEmploye("Emp2", "User", "emp2@mail.com", "pass2", LocalDate.now(), null);
		Employe emp3 = ligue.addEmploye("Emp3", "User", "emp3@mail.com", "pass3", LocalDate.now(), null);
		
		assertEquals(3, ligue.getEmployes().size());
		
		emp1.remove();
		assertEquals(2, ligue.getEmployes().size());
		assertFalse(ligue.getEmployes().contains(emp1));
		
		emp3.remove();
		assertEquals(1, ligue.getEmployes().size());
		assertTrue(ligue.getEmployes().contains(emp2));
	}
	
	// ========== Tests de suppression de ligue ==========
	
	@Test
	void suppressionLigue() throws SauvegardeImpossible
	{
		Ligue ligueTemp = gestionPersonnel.addLigue("Ligue Temporaire");
		
		assertTrue(gestionPersonnel.getLigues().contains(ligueTemp));
		ligueTemp.remove();
		assertFalse(gestionPersonnel.getLigues().contains(ligueTemp));
	}
	
	@Test
	void suppressionLigueAvecEmployes() throws SauvegardeImpossible
	{
		Ligue ligueTemp = gestionPersonnel.addLigue("Ligue avec Employés");
		Employe emp1 = ligueTemp.addEmploye("Emp1", "User", "emp1@mail.com", "pass1", LocalDate.now(), null);
		Employe emp2 = ligueTemp.addEmploye("Emp2", "User", "emp2@mail.com", "pass2", LocalDate.now(), null);
		
		assertEquals(2, ligueTemp.getEmployes().size());
		ligueTemp.remove();
		
		assertFalse(gestionPersonnel.getLigues().contains(ligueTemp));
	}
	
	@Test
	void suppressionLigueAvecAdministrateur() throws SauvegardeImpossible
	{
		Ligue ligueTemp = gestionPersonnel.addLigue("Ligue Admin");
		Employe admin = ligueTemp.addEmploye("Admin", "User", "admin@mail.com", "pass", LocalDate.now(), null);
		ligueTemp.setAdministrateur(admin);
		
		assertEquals(admin, ligueTemp.getAdministrateur());
		ligueTemp.remove();
		assertFalse(gestionPersonnel.getLigues().contains(ligueTemp));
	}
	
	// ========== Tests de compareTo ==========
	
	@Test
	void compareToOrdreAlphabetique() throws SauvegardeImpossible
	{
		Ligue ligueA = gestionPersonnel.addLigue("AAA Ligue");
		Ligue ligueZ = gestionPersonnel.addLigue("ZZZ Ligue");
		
		assertTrue(ligueA.compareTo(ligueZ) < 0);
		assertTrue(ligueZ.compareTo(ligueA) > 0);
		assertTrue(ligueA.compareTo(ligueA) == 0);
	}
	
	// ========== Tests de toString ==========
	
	@Test
	void toStringLigue() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		assertEquals("Tennis", ligue.toString());
	}
	
	// ========== Tests de cas limites ==========
	
	@Test
	void ligueSansEmploye() throws SauvegardeImpossible
	{
		Ligue ligueVide = gestionPersonnel.addLigue("Ligue Vide");
		assertEquals(0, ligueVide.getEmployes().size());
		assertEquals(gestionPersonnel.getRoot(), ligueVide.getAdministrateur());
	}
	
	@Test
	void modificationAdministrateurApresSuppressionEmploye() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Employe emp1 = ligue.addEmploye("Premier", "User", "premier@mail.com", "pass1", LocalDate.now(), null);
		Employe emp2 = ligue.addEmploye("Second", "User", "second@mail.com", "pass2", LocalDate.now(), null);
		
		ligue.setAdministrateur(emp1);
		emp1.remove();
		
		assertEquals(gestionPersonnel.getRoot(), ligue.getAdministrateur());
		
		ligue.setAdministrateur(emp2);
		assertEquals(emp2, ligue.getAdministrateur());
	}
	
	@Test
	void nomLigueVide() throws SauvegardeImpossible
	{
		Ligue ligueNomVide = gestionPersonnel.addLigue("");
		assertEquals("", ligueNomVide.getNom());
	}
	
	@Test
	void setAdministrateurChangementsMultiples() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Employe emp1 = ligue.addEmploye("Un", "User", "un@mail.com", "pass1", LocalDate.now(), null);
		Employe emp2 = ligue.addEmploye("Deux", "User", "deux@mail.com", "pass2", LocalDate.now(), null);
		Employe emp3 = ligue.addEmploye("Trois", "User", "trois@mail.com", "pass3", LocalDate.now(), null);
		
		ligue.setAdministrateur(emp1);
		assertTrue(emp1.estAdmin(ligue));
		
		ligue.setAdministrateur(emp2);
		assertFalse(emp1.estAdmin(ligue));
		assertTrue(emp2.estAdmin(ligue));
		
		ligue.setAdministrateur(emp3);
		assertFalse(emp2.estAdmin(ligue));
		assertTrue(emp3.estAdmin(ligue));
		
		ligue.setAdministrateur(gestionPersonnel.getRoot());
		assertFalse(emp3.estAdmin(ligue));
		assertTrue(gestionPersonnel.getRoot().estAdmin(ligue));
	}
}