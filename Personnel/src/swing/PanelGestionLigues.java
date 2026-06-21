package swing;

import javax.swing.*;
import java.awt.*;

import personnel.Employe;
import personnel.GestionPersonnel;
import personnel.Ligue;
import personnel.SauvegardeImpossible;

/**
 * Page de liste des ligues, désormais commune à TOUS les employés
 * connectés (root, administrateur ou simple employé). Correspond à
 * "ligue.png". Ce qui change selon le rôle, c'est uniquement quels
 * boutons sont affichés (voir Employe.peutGererLigues/peutVoir/peutGerer) :
 *  - root : AJOUTER LIGUE, et OUVRIR/RENOMMER/SUPPRIMER sur chaque ligne.
 *  - administrateur : seulement OUVRIR/RENOMMER/SUPPRIMER sur SA ligue.
 *  - simple employé : seulement OUVRIR sur SA ligue (lecture).
 */
public class PanelGestionLigues extends JPanel
{
	public PanelGestionLigues(FenetrePrincipale fenetre)
	{
		GestionPersonnel gestionPersonnel = fenetre.getGestionPersonnel();
		setLayout(new BorderLayout());

		add(construireBarreLaterale(fenetre), BorderLayout.WEST);
		add(construireContenu(fenetre, gestionPersonnel), BorderLayout.CENTER);
	}

	private BarreLaterale construireBarreLaterale(FenetrePrincipale fenetre)
	{
		// "LIGUES" et non "GERER LIGUES" : cette page n'est plus réservée à la
		// gestion (un simple employé l'utilise aussi, juste pour consulter).
		JButton boutonLigues = new JButton("LIGUES");
		boutonLigues.addActionListener(e -> fenetre.afficherPage(new PanelGestionLigues(fenetre)));

		Runnable actionProfil = () -> fenetre.afficherPage(new PanelModifierProfil(
				fenetre, fenetre.getEmployeConnecte(),
				() -> fenetre.afficherPage(new PanelGestionLigues(fenetre))));

		return new BarreLaterale(fenetre, actionProfil, boutonLigues);
	}

	private JPanel construireContenu(FenetrePrincipale fenetre, GestionPersonnel gestionPersonnel)
	{
		Employe connecte = fenetre.getEmployeConnecte();

		JPanel contenu = new JPanel(new BorderLayout(10, 10));
		contenu.setBackground(Color.WHITE);
		contenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel entete = new JPanel(new BorderLayout());
		entete.setOpaque(false);
		entete.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
				BorderFactory.createEmptyBorder(0, 0, 15, 0)));
		JLabel titre = new JLabel("LIGUES");
		titre.setFont(titre.getFont().deriveFont(Font.BOLD, 22f));
		entete.add(titre, BorderLayout.WEST);

		// Seul le root peut créer des ligues.
		if (connecte.peutGererLigues())
		{
			JButton boutonAjouterLigue = new JButton("AJOUTER LIGUE");
			boutonAjouterLigue.addActionListener(e -> ajouterLigue(fenetre, gestionPersonnel, contenu));
			entete.add(boutonAjouterLigue, BorderLayout.EAST);
		}
		contenu.add(entete, BorderLayout.NORTH);

		JPanel grille = construireGrilleLigues(fenetre, gestionPersonnel, connecte);
		JPanel conteneurGrille = new JPanel(new BorderLayout());
		conteneurGrille.setOpaque(false);
		conteneurGrille.add(grille, BorderLayout.NORTH); // NORTH : la grille garde sa hauteur naturelle, ne s'étire pas
		contenu.add(new JScrollPane(conteneurGrille), BorderLayout.CENTER);

		return contenu;
	}

	private JPanel construireGrilleLigues(FenetrePrincipale fenetre, GestionPersonnel gestionPersonnel, Employe connecte)
	{
		JPanel grille = ComposantsUI.grilleTableau(2);
		grille.add(ComposantsUI.celluleEntete("Nom de Ligue"));
		grille.add(ComposantsUI.celluleEntete("Actions"));

		for (Ligue ligue : gestionPersonnel.getLigues())
		{
			grille.add(ComposantsUI.celluleTexte(ligue.getNom()));
			grille.add(ComposantsUI.cellule(construireActionsLigne(fenetre, ligue, connecte)));
		}
		return grille;
	}

	/**
	 * Construit, pour une ligne donnée, uniquement les boutons que l'employé
	 * connecté est autorisé à utiliser sur CETTE ligue précise. Une ligue
	 * qu'un simple employé ne peut ni voir ni gérer se retrouve donc avec
	 * une cellule "Actions" vide — ce qui est normal et attendu.
	 */
	private JPanel construireActionsLigne(FenetrePrincipale fenetre, Ligue ligue, Employe connecte)
	{
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

		if (connecte.peutVoir(ligue))
		{
			JButton boutonOuvrir = new JButton("OUVRIR");
			boutonOuvrir.addActionListener(e -> fenetre.afficherPage(new PanelGestionEmployes(fenetre, ligue)));
			actions.add(boutonOuvrir);
		}

		if (connecte.peutGerer(ligue))
		{
			JButton boutonRenommer = new JButton("RENOMMER");
			boutonRenommer.addActionListener(e -> renommerLigue(fenetre, ligue, actions));
			actions.add(boutonRenommer);

			JButton boutonSupprimer = new JButton("SUPPRIMER");
			boutonSupprimer.addActionListener(e -> supprimerLigue(fenetre, ligue, actions));
			actions.add(boutonSupprimer);
		}

		return actions;
	}

	private void ajouterLigue(FenetrePrincipale fenetre, GestionPersonnel gestionPersonnel, Component parent)
	{
		String nom = JOptionPane.showInputDialog(parent, "Nom de la nouvelle ligue :");
		if (nom == null || nom.trim().isEmpty())
			return;

		try
		{
			gestionPersonnel.addLigue(nom);
			fenetre.afficherPage(new PanelGestionLigues(fenetre));
		}
		catch (SauvegardeImpossible ex)
		{
			JOptionPane.showMessageDialog(parent, "Impossible d'enregistrer la nouvelle ligue.",
					"Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void renommerLigue(FenetrePrincipale fenetre, Ligue ligue, Component parent)
	{
		String nouveauNom = JOptionPane.showInputDialog(parent, "Nouveau nom :", ligue.getNom());
		if (nouveauNom == null || nouveauNom.trim().isEmpty())
			return;

		try
		{
			ligue.setNom(nouveauNom);
			fenetre.afficherPage(new PanelGestionLigues(fenetre));
		}
		catch (SauvegardeImpossible ex)
		{
			JOptionPane.showMessageDialog(parent, "Impossible d'enregistrer.", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void supprimerLigue(FenetrePrincipale fenetre, Ligue ligue, Component parent)
	{
		int confirmation = JOptionPane.showConfirmDialog(parent,
				"Supprimer la ligue \"" + ligue.getNom() + "\" et tous ses employés ?",
				"Confirmation", JOptionPane.YES_NO_OPTION);
		if (confirmation != JOptionPane.YES_OPTION)
			return;

		try
		{
			ligue.remove();
			fenetre.afficherPage(new PanelGestionLigues(fenetre));
		}
		catch (SauvegardeImpossible ex)
		{
			JOptionPane.showMessageDialog(parent, "Impossible de supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
}