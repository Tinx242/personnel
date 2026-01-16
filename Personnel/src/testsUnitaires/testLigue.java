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
}