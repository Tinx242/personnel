package commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.time.LocalDate;
import java.util.ArrayList;

import commandLineMenus.List;
import commandLineMenus.Menu;
import commandLineMenus.Option;

import personnel.*;

public class LigueConsole 
{
	private GestionPersonnel gestionPersonnel;
	private EmployeConsole employeConsole;

	public LigueConsole(GestionPersonnel gestionPersonnel, EmployeConsole employeConsole)
	{
		this.gestionPersonnel = gestionPersonnel;
		this.employeConsole = employeConsole;
	}

	Menu menuLigues()
	{
		Menu menu = new Menu("Gérer les ligues", "l");
		menu.add(afficherLigues());
		menu.add(ajouterLigue());
		menu.add(selectionnerLigue());
		menu.addBack("q");
		return menu;
	}

	private Option afficherLigues()
	{
		return new Option("Afficher les ligues", "l", () -> {System.out.println(gestionPersonnel.getLigues());});
	}

	private Option afficher(final Ligue ligue)
	{
		return new Option("Afficher la ligue", "l", 
				() -> 
				{
					System.out.println(ligue);
					System.out.println("administrée par " + ligue.getAdministrateur());
				}
		);
	}
	
	private Option afficherEmployes(final Ligue ligue)
	{
		return new Option("Afficher les employés", "l", () -> {System.out.println(ligue.getEmployes());});
	}

	private Option ajouterLigue()
	{
		return new Option("Ajouter une ligue", "a", () -> 
		{
			try
			{
				gestionPersonnel.addLigue(getString("nom : "));
			}
			catch(SauvegardeImpossible exception)
			{
				System.err.println("Impossible de sauvegarder cette ligue");
			}
		});
	}
	
	private Menu editerLigue(Ligue ligue)
	{
		Menu menu = new Menu("Editer " + ligue.getNom());
		menu.add(afficher(ligue));
		menu.add(gererEmployes(ligue));
		menu.add(changerAdministrateur(ligue));
		menu.add(changerNom(ligue));
		menu.add(supprimer(ligue));
		menu.addBack("q");
		return menu;
	}

	private Option changerNom(final Ligue ligue)
	{
		return new Option("Renommer", "r", 
				() -> {try {
					ligue.setNom(getString("Nouveau nom : "));
				} catch (SauvegardeImpossible e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}});
	}

	private List<Ligue> selectionnerLigue()
	{
		return new List<Ligue>("Sélectionner une ligue", "e", 
				() -> new ArrayList<>(gestionPersonnel.getLigues()),
				(element) -> editerLigue(element)
		);
	}
	
	private Option ajouterEmploye(final Ligue ligue)
	{
		return new Option("Ajouter un employé", "a", () -> 
		{
			try 
			{
				// Saisie des informations de base
				String nom = getString("Nom : ");
				String prenom = getString("Prénom : ");
				String mail = getString("Mail : ");
				String password = getString("Password : ");
				
				// Saisie des dates avec gestion des erreurs
				System.out.println("\n--- Saisie des dates ---");
				System.out.println("Format attendu : JJ/MM/AAAA (ex: 15/03/2024)");
				
				LocalDate dateArrivee = null;
				LocalDate dateDepart = null;
				
				// Saisie de la date d'arrivée
				boolean datesValides = false;
				while (!datesValides)
				{
					dateArrivee = EmployeConsole.saisirDate("Date d'arrivée : ");
					
					if (dateArrivee == null)
					{
						System.out.println("Ajout d'employé annulé.");
						return;
					}
					
					// Saisie optionnelle de la date de départ
					String saisirDepart = getString("Voulez-vous saisir une date de départ ? (o/n) : ");
					
					if (saisirDepart != null && saisirDepart.trim().toLowerCase().equals("o"))
					{
						dateDepart = EmployeConsole.saisirDate("Date de départ : ", true);
						
						// Vérification de la cohérence des dates
						if (dateDepart != null && dateDepart.isBefore(dateArrivee))
						{
							System.err.println("Erreur : La date de départ ne peut pas être antérieure à la date d'arrivée.");
							String retry = getString("Voulez-vous ressaisir les dates ? (o/n) : ");
							if (retry == null || !retry.trim().toLowerCase().equals("o"))
							{
								System.out.println("Ajout d'employé annulé.");
								return;
							}
							continue; // Recommencer la saisie des dates
						}
					}
					
					datesValides = true;
				}
				
				// Ajout de l'employé avec les dates
				ligue.addEmploye(nom, prenom, mail, password, dateArrivee, dateDepart);
				
				System.out.println("\n✓ Employé ajouté avec succès !");
				System.out.println("  Nom : " + nom + " " + prenom);
				System.out.println("  Date d'arrivée : " + EmployeConsole.formaterDate(dateArrivee));
				System.out.println("  Date de départ : " + EmployeConsole.formaterDate(dateDepart));
			} 
			catch (DatesIncoherentesException e) 
			{
				System.err.println("Erreur : Dates incohérentes - " + e.getMessage());
				System.err.println("L'employé n'a pas été ajouté.");
			}
			catch (Exception e)
			{
				System.err.println("Erreur inattendue lors de l'ajout de l'employé : " + e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	private Menu gererEmployes(Ligue ligue)
	{
		Menu menu = new Menu("Gérer les employés de " + ligue.getNom(), "e");
		menu.add(afficherEmployes(ligue));
		menu.add(ajouterEmploye(ligue));
		menu.add(selectionnerEmploye(ligue));
		menu.addBack("q");
		return menu;
	}
	
	private List<Employe> selectionnerEmploye(final Ligue ligue)
	{
		return new List<>("Sélectionner un employé", "s", 
				() -> new ArrayList<>(ligue.getEmployes()),
				(element) -> gererEmploye(element)
		);
	}
	
	private Menu gererEmploye(Employe employe)
	{
		Menu menu = new Menu("Gérer " + employe.getNom() + " " + employe.getPrenom(), "e");
		menu.add(employeConsole.editerEmploye(employe));
		menu.add(supprimerEmploye(employe));
		menu.addBack("q");
		return menu;
	}

	private Option supprimerEmploye(final Employe employe) 
	{
		return new Option("Supprimer cet employé", "d", () -> {try {
			employe.remove();
		} catch (SauvegardeImpossible e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
	}
	
	private List<Employe> changerAdministrateur(final Ligue ligue)
	{
		return new List<>("Changer l'administrateur", "c", 
				() -> new ArrayList<>(ligue.getEmployes()),
				(employe) -> new Option("Définir " + employe.getNom() + " comme administrateur", "a", 
						() -> 
						{
							ligue.setAdministrateur(employe);
							System.out.println(employe.getNom() + " " + employe.getPrenom() + 
									" est maintenant l'administrateur de " + ligue.getNom());
						}
				)
		);
	}			

	private List<Employe> modifierEmploye(final Ligue ligue)
	{
		return new List<>("Modifier un employé", "e", 
				() -> new ArrayList<>(ligue.getEmployes()),
				employeConsole.editerEmploye()
		);
	}
	
	private Option supprimer(Ligue ligue)
	{
		return new Option("Supprimer", "d", () -> {try {
			ligue.remove();
		} catch (SauvegardeImpossible e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
	}
}