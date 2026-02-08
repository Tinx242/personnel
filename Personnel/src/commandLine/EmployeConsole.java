package commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import commandLineMenus.ListOption;
import commandLineMenus.Menu;
import commandLineMenus.Option;
import personnel.Employe;

public class EmployeConsole 
{
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
	
	private Option afficher(final Employe employe)
	{
		return new Option("Afficher l'employé", "l", () -> {System.out.println(employe);});
	}

	ListOption<Employe> editerEmploye()
	{
		return (employe) -> editerEmploye(employe);		
	}

	Option editerEmploye(Employe employe)
	{
			Menu menu = new Menu("Gérer le compte " + employe.getNom(), "c");
			menu.add(afficher(employe));
			menu.add(changerNom(employe));
			menu.add(changerPrenom(employe));
			menu.add(changerMail(employe));
			menu.add(changerAdministrateur(employe));
			menu.add(changerDateArrivee(employe));
	        menu.add(changerDateDepart(employe));
			menu.add(changerPassword(employe));
			menu.addBack("q");
			return menu;
	}

	private Option changerNom(final Employe employe)
	{
		return new Option("Changer le nom", "n", 
				() -> {employe.setNom(getString("Nouveau nom : "));}
			);
	}
	
	private Option changerPrenom(final Employe employe)
	{
		return new Option("Changer le prénom", "p", () -> {employe.setPrenom(getString("Nouveau prénom : "));});
	}
	
	private Option changerMail(final Employe employe)
	{
		return new Option("Changer le mail", "e", () -> {employe.setMail(getString("Nouveau mail : "));});
	}
	
	private Option changerPassword(final Employe employe)
	{
		return new Option("Changer le password", "x", () -> {employe.setPassword(getString("Nouveau password : "));});
	}
	
	private Option changerDateArrivee(final Employe employe) {
		return new Option("Changer la date d'arrivée", "w",
				() -> 
        {
            try
            {
            	LocalDate dateArrivee = getDate("Date d'arrivée");
                if (dateArrivee != null)
                {
                	employe.setDateArrivee(dateArrivee);
                    System.out.println("Date d'arrivée modifiée : " + dateArrivee);
                }
            }
            catch (personnel.DatesIncoherentesException e)
            {
                System.err.println("ERREUR : " + e.getMessage());
                System.err.println("La date d'arrivée doit être avant la date de départ.");
            }
        });
	}
	
	private Option changerDateDepart(final Employe employe) {
		return new Option("Changer la date de départ", "z",
				() -> 
        {
            try
            {
                LocalDate dateDepart = getDate("Date de départ");
                if (dateDepart != null)
                {
                    employe.setDateDepart(dateDepart);
                    System.out.println("Date de départ modifiée : " + dateDepart);
                }
            }
            catch (personnel.DatesIncoherentesException e)
            {
                System.err.println("ERREUR : " + e.getMessage());
                System.err.println("La date de départ doit être après la date d'arrivée.");
            }
        });
	}
	
	
	private Option changerAdministrateur(final Employe employe)
	{
		return new Option("Définir comme administrateur", "a", 
				() -> 
				{
					employe.getLigue().setAdministrateur(employe);
					System.out.println(employe.getNom() + " " + employe.getPrenom() + 
							" est maintenant l'administrateur de " + employe.getLigue().getNom());
				}
		);
	}
	

}
