package swing;

import javax.swing.SwingUtilities;
import personnel.GestionPersonnel;

/**
 * Point d'entrée de l'interface Swing (équivalent de PersonnelConsole.main
 * pour la console).
 */
public class PersonnelSwing
{
	public static void main(String[] args)
	{
		// Toute manipulation de Swing doit se faire dans le thread d'événements
		// (l'EDT), jamais directement dans main().
		SwingUtilities.invokeLater(() ->
		{
			FenetrePrincipale fenetre = new FenetrePrincipale(GestionPersonnel.getGestionPersonnel());
			fenetre.setVisible(true);
		});
	}
}