package commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import commandLineMenus.ListOption;
import commandLineMenus.Menu;
import commandLineMenus.Option;
import personnel.DatesIncoherentesException;
import personnel.Employe;
import personnel.SauvegardeImpossible;

public class EmployeConsole 
{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
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
		menu.add(changerPassword(employe));
		menu.add(changerDateArrivee(employe));
		menu.add(changerDateDepart(employe));
		menu.addBack("q");
		return menu;
	}

	private Option changerNom(final Employe employe)
	{
		return new Option("Changer le nom", "n", 
				() -> {try {
					employe.setNom(getString("Nouveau nom : "));
				} catch (SauvegardeImpossible e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
		);
	}
	
	private Option changerPrenom(final Employe employe)
	{
		return new Option("Changer le prénom", "p", () -> {try {
			employe.setPrenom(getString("Nouveau prénom : "));
		} catch (SauvegardeImpossible e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
	}
	
	private Option changerMail(final Employe employe)
	{
		return new Option("Changer le mail", "e", () -> {try {
			employe.setMail(getString("Nouveau mail : "));
		} catch (SauvegardeImpossible e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
	}
	
	private Option changerPassword(final Employe employe)
	{
		return new Option("Changer le password", "x", () -> {try {
			employe.setPassword(getString("Nouveau password : "));
		} catch (SauvegardeImpossible e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
	}
	
	private Option changerDateArrivee(final Employe employe)
	{
		return new Option("Changer la date d'arrivée", "da", () -> 
		{
			System.out.println("Date d'arrivée actuelle : " + formaterDate(employe.getDateArrivee()));
			if (employe.getDateDepart() != null)
			{
				System.out.println("Date de départ actuelle : " + formaterDate(employe.getDateDepart()));
			}
			
			LocalDate dateArrivee = saisirDate("Nouvelle date d'arrivée (JJ/MM/AAAA) : ");
			if (dateArrivee != null)
			{
				try
				{
					employe.setDateArrivee(dateArrivee);
					System.out.println("✓ Date d'arrivée modifiée avec succès : " + formaterDate(dateArrivee));
				}
				catch (DatesIncoherentesException e)
				{
					System.err.println("✗ Erreur : " + e.getMessage());
					System.err.println("La date d'arrivée n'a pas été modifiée.");
				}
				catch (Exception e)
				{
					System.err.println("✗ Erreur inattendue : " + e.getMessage());
				}
			}
			else
			{
				System.out.println("Modification annulée.");
			}
		});
	}
	
	private Option changerDateDepart(final Employe employe)
	{
		return new Option("Changer la date de départ", "dd", () -> 
		{
			System.out.println("Date d'arrivée : " + formaterDate(employe.getDateArrivee()));
			System.out.println("Date de départ actuelle : " + formaterDate(employe.getDateDepart()));
			
			String choix = getString("Nouvelle date de départ (JJ/MM/AAAA) ou 'supprimer' pour retirer la date : ");
			
			if (choix != null && choix.trim().equalsIgnoreCase("supprimer"))
			{
				try
				{
					try {
						employe.setDateDepart(null);
					} catch (SauvegardeImpossible e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("✓ Date de départ supprimée avec succès.");
				}
				catch (DatesIncoherentesException e)
				{
					System.err.println("✗ Erreur : " + e.getMessage());
				}
			}
			else
			{
				LocalDate dateDepart = saisirDate("Nouvelle date de départ (JJ/MM/AAAA) : ", true);
				if (dateDepart != null)
				{
					try
					{
						employe.setDateDepart(dateDepart);
						System.out.println("✓ Date de départ modifiée avec succès : " + formaterDate(dateDepart));
					}
					catch (DatesIncoherentesException e)
					{
						System.err.println("✗ Erreur : " + e.getMessage());
						System.err.println("La date de départ doit être postérieure à la date d'arrivée (" + 
								formaterDate(employe.getDateArrivee()) + ")");
						System.err.println("La date de départ n'a pas été modifiée.");
					}
					catch (Exception e)
					{
						System.err.println("✗ Erreur inattendue : " + e.getMessage());
					}
				}
				else
				{
					System.out.println("Modification annulée.");
				}
			}
		});
	}
	
	/**
	 * Saisit une date au format JJ/MM/AAAA avec validation
	 * @param message Le message à afficher
	 * @return La date saisie ou null en cas d'annulation
	 */
	public static LocalDate saisirDate(String message)
	{
		return saisirDate(message, false);
	}
	
	/**
	 * Saisit une date au format JJ/MM/AAAA avec validation
	 * @param message Le message à afficher
	 * @param nullable Si true, permet de retourner null en cas de saisie vide
	 * @return La date saisie ou null
	 */
	public static LocalDate saisirDate(String message, boolean nullable)
	{
		while (true)
		{
			String input = getString(message);
			
			// Permettre une saisie vide si nullable
			if (nullable && (input == null || input.trim().isEmpty()))
			{
				return null;
			}
			
			// Vérifier que la saisie n'est pas vide
			if (input == null || input.trim().isEmpty())
			{
				System.err.println("La date ne peut pas être vide. Veuillez réessayer.");
				continue;
			}
			
			try
			{
				LocalDate date = LocalDate.parse(input.trim(), DATE_FORMATTER);
				return date;
			}
			catch (DateTimeParseException e)
			{
				System.err.println("Format de date invalide. Veuillez utiliser le format JJ/MM/AAAA (ex: 15/03/2024)");
				String retry = getString("Voulez-vous réessayer ? (o/n) : ");
				if (retry != null && retry.trim().toLowerCase().equals("n"))
				{
					return null;
				}
			}
		}
	}
	
	/**
	 * Formate une date pour l'affichage
	 * @param date La date à formater
	 * @return La date formatée ou "Non définie"
	 */
	public static String formaterDate(LocalDate date)
	{
		return date != null ? date.format(DATE_FORMATTER) : "Non définie";
	}
}