package commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
	
	private LocalDate getDate(String prompt)
    {
        while (true)
        {
            try
            {
                String input = getString(prompt + " (format aaaa-mm-jj) : ");
                if (input.isEmpty())
                    return null;
                return LocalDate.parse(input);
            }
            catch (DateTimeParseException e)
            {
                System.err.println("Format invalide. Utilisez aaaa-mm-jj");
            }
        }
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
		return new Option("Afficher les employes", "l", () -> {System.out.println(ligue.getEmployes());});
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
		menu.add(changerNom(ligue));
		menu.add(supprimer(ligue));
		menu.addBack("q");
		return menu;
	}

	private Option changerNom(final Ligue ligue)
	{
		return new Option("Renommer", "r", 
				() -> {ligue.setNom(getString("Nouveau nom : "));});
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
        return new Option("ajouter un employé", "a",
            () -> 
            {
                try 
                {
                    String nom = getString("nom : ");
                    String prenom = getString("prenom : ");
                    String mail = getString("mail : ");
                    String password = getString("password : ");
                    LocalDate dateArrivee;
                    LocalDate dateDepart;
                    
                    while (true)
                    {
                        dateArrivee = getDate("Date d'arrivée");
                        dateDepart = getDate("Date de départ");
                        
                        if (dateArrivee == null || dateDepart == null)
                            break;
                        
                        if (dateArrivee.isAfter(dateDepart))
                        {
                            System.err.println("ERREUR : La date d'arrivée doit être avant la date de départ.");
                            System.err.println("Veuillez ressaisir les deux dates.");
                        }
                        else
                        {
                            break;
                        }
                    }
                    
                    ligue.addEmploye(nom, prenom, mail, password, dateArrivee, dateDepart);
                    
                    System.out.println("Employé créé avec succès !");
                    if (dateArrivee != null)
                        System.out.println("Date d'arrivée : " + dateArrivee);
                    if (dateDepart != null)
                        System.out.println("Date de départ : " + dateDepart);
                } 
                catch (DatesIncoherentesException e) 
                {
                    System.err.println("ERREUR : " + e.getMessage());
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

	//rivate List<Employe> supprimerEmploye(final Ligue ligue)
	//
	//	return new List<>("Supprimer un employé", "s", 
	//			() -> new ArrayList<>(ligue.getEmployes()),
	//			(index, element) -> {element.remove();}
	//			);
	//
	
	private Option supprimerEmploye(final Employe employe) {
		return new Option("Supprimer cet employé","d",() ->{employe.remove();});
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
		return new Option("Supprimer", "d", () -> {ligue.remove();});
	}
	
}
