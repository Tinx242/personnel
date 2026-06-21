package swing;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import personnel.Employe;
import personnel.GestionPersonnel;

/**
 * Fenêtre unique de l'application. Contrairement à la console qui boucle sur
 * des menus, on affiche une seule JFrame dont on remplace le contenu
 * ("contentPane") à chaque navigation.
 *
 * Pourquoi pas un CardLayout (vu en cours la dernière fois) ? Parce que la
 * plupart de nos pages dépendent de données qui n'existent qu'à l'exécution
 * (telle ligue précise, tel employé précis) : on ne peut pas les enregistrer
 * toutes à l'avance sous un nom fixe. On construit donc la page voulue au
 * moment de la navigation, avec les bonnes données, et on la pose dans la
 * fenêtre. Cela a aussi l'avantage de "rafraîchir" automatiquement les
 * listes : comme on reconstruit la page à chaque fois, elle relit toujours
 * les données à jour dans gestionPersonnel.
 */
public class FenetrePrincipale extends JFrame
{
	private GestionPersonnel gestionPersonnel;
	private Employe employeConnecte;

	public FenetrePrincipale(GestionPersonnel gestionPersonnel)
	{
		super("Gestion du personnel");
		this.gestionPersonnel = gestionPersonnel;

		setSize(1100, 650);
		setLocationRelativeTo(null);

		// On gère nous-mêmes la fermeture (croix de la fenêtre) pour proposer
		// la même boîte de dialogue que le bouton QUITTER.
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				DialogQuitter.afficher(FenetrePrincipale.this, gestionPersonnel);
			}
		});

		afficherPage(new PanelConnexion(this));
	}

	public GestionPersonnel getGestionPersonnel()
	{
		return gestionPersonnel;
	}

	public Employe getEmployeConnecte()
	{
		return employeConnecte;
	}

	/** Remplace la page actuellement affichée par celle donnée. */
	public void afficherPage(JPanel page)
	{
		setContentPane(page);
		revalidate();
		repaint();
	}

	/**
	 * Appelée par PanelConnexion une fois l'authentification réussie.
	 * Tout le monde arrive sur la même page (la liste des ligues) : c'est
	 * elle qui décide quels boutons afficher selon les droits de
	 * l'employé connecté (voir Employe.peutGererLigues/peutVoir/peutGerer).
	 */
	public void connecter(Employe employe)
	{
		this.employeConnecte = employe;
		afficherPage(new PanelGestionLigues(this));
	}
}