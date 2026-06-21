package swing;

import javax.swing.*;
import java.awt.Component;

import personnel.GestionPersonnel;
import personnel.SauvegardeImpossible;

/**
 * Boîte de dialogue affichée quand l'utilisateur clique sur QUITTER
 * (ou sur la croix de fermeture de la fenêtre). Correspond à la maquette
 * "quitter.png".
 */
public class DialogQuitter
{
	public static void afficher(Component parent, GestionPersonnel gestionPersonnel)
	{
		String[] options = { "Retour", "Quitter sans enregistrer", "Enregistrer et quitter" };

		int choix = JOptionPane.showOptionDialog(parent,
				"Voulez-vous enregistrer les données avant de quitter ?",
				"Connexion employé",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
				null, options, options[0]);

		if (choix == 1) // Quitter sans enregistrer
		{
			System.exit(0);
		}
		else if (choix == 2) // Enregistrer et quitter
		{
			try
			{
				gestionPersonnel.sauvegarder();
				System.exit(0);
			}
			catch (SauvegardeImpossible ex)
			{
				JOptionPane.showMessageDialog(parent, "Impossible d'enregistrer les données.",
						"Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}
		// choix == 0 (Retour), ou fenêtre de dialogue fermée : on ne fait rien, on reste sur la page actuelle.
	}
}
