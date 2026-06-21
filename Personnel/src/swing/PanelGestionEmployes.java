package swing;

import javax.swing.*;
import java.awt.*;

import personnel.Employe;
import personnel.Ligue;
import personnel.SauvegardeImpossible;

/**
 * Page listant les employés d'une ligue. Correspond à "employes_.png".
 *
 * NB pédagogique : sur la maquette, le bouton en haut à droite est étiqueté
 * "AJOUTER LIGUE", ce qui est très probablement une coquille de copier-coller
 * depuis la page des ligues (image 6). Dans ce contexte (la liste des
 * employés), on l'a corrigé en "AJOUTER EMPLOYE" — vérifiez avec votre
 * équipe si c'était bien volontaire.
 */
public class PanelGestionEmployes extends JPanel
{
	public PanelGestionEmployes(FenetrePrincipale fenetre, Ligue ligue)
	{
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		add(construireEntete(fenetre, ligue), BorderLayout.NORTH);

		JPanel grille = construireGrilleEmployes(fenetre, ligue);
		JPanel conteneurGrille = new JPanel(new BorderLayout());
		conteneurGrille.setBackground(Color.WHITE);
		conteneurGrille.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		conteneurGrille.add(grille, BorderLayout.NORTH); // garde sa hauteur naturelle, ne s'étire pas verticalement
		add(new JScrollPane(conteneurGrille), BorderLayout.CENTER);
	}

	private JPanel construireEntete(FenetrePrincipale fenetre, Ligue ligue)
	{
		Employe connecte = fenetre.getEmployeConnecte();

		JPanel entete = new JPanel(new BorderLayout());
		entete.setBackground(Color.WHITE);
		entete.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
				BorderFactory.createEmptyBorder(15, 15, 15, 15)));

		JButton boutonRetour = new JButton("RETOUR");
		boutonRetour.addActionListener(e -> fenetre.afficherPage(new PanelGestionLigues(fenetre)));
		entete.add(boutonRetour, BorderLayout.WEST);

		JLabel titre = new JLabel(ligue.getNom().toUpperCase(), SwingConstants.CENTER);
		titre.setFont(titre.getFont().deriveFont(Font.BOLD, 20f));
		entete.add(titre, BorderLayout.CENTER);

		// Seul root (ou l'administrateur de cette ligue précise) peut ajouter un employé.
		if (connecte.peutGerer(ligue))
		{
			JButton boutonAjouterEmploye = new JButton("AJOUTER EMPLOYE");
			boutonAjouterEmploye.addActionListener(e -> fenetre.afficherPage(new PanelAjoutEmploye(fenetre, ligue)));
			entete.add(boutonAjouterEmploye, BorderLayout.EAST);
		}

		return entete;
	}

	private JPanel construireGrilleEmployes(FenetrePrincipale fenetre, Ligue ligue)
	{
		JPanel grille = ComposantsUI.grilleTableau(6);
		for (String colonne : new String[] { "Nom", "Prénom", "Mail", "Arrivée", "Départ", "Actions" })
			grille.add(ComposantsUI.celluleEntete(colonne));

		for (Employe employe : ligue.getEmployes())
		{
			grille.add(ComposantsUI.celluleTexte(employe.getNom()));
			grille.add(ComposantsUI.celluleTexte(employe.getPrenom()));
			grille.add(ComposantsUI.celluleTexte(employe.getMail()));
			grille.add(ComposantsUI.celluleTexte(
					employe.getDateArrivee() == null ? "---" : employe.getDateArrivee().toString()));
			grille.add(ComposantsUI.celluleTexte(
					employe.getDateDepart() == null ? "---" : employe.getDateDepart().toString()));
			grille.add(ComposantsUI.cellule(construireActionsLigne(fenetre, ligue, employe)));
		}
		return grille;
	}

	private JPanel construireActionsLigne(FenetrePrincipale fenetre, Ligue ligue, Employe employe)
	{
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		Employe connecte = fenetre.getEmployeConnecte();

		// Un simple employé qui consulte sa propre ligue voit la liste de ses
		// collègues, mais sans aucun bouton d'action — exactement ce qu'on a
		// décidé : "voir, mais pas agir" pour qui n'est ni root ni admin.
		if (connecte.peutGerer(ligue))
		{
			JButton boutonGerer = new JButton("GERER");
			boutonGerer.addActionListener(e -> fenetre.afficherPage(new PanelModifierProfil(
					fenetre, employe, () -> fenetre.afficherPage(new PanelGestionEmployes(fenetre, ligue)))));
			actions.add(boutonGerer);

			JButton boutonSupprimer = new JButton("SUPPRIMER");
			boutonSupprimer.addActionListener(e -> supprimerEmploye(fenetre, ligue, employe, actions));
			actions.add(boutonSupprimer);
		}

		return actions;
	}

	private void supprimerEmploye(FenetrePrincipale fenetre, Ligue ligue, Employe employe, Component parent)
	{
		int confirmation = JOptionPane.showConfirmDialog(parent,
				"Supprimer " + employe.getNom() + " " + employe.getPrenom() + " ?",
				"Confirmation", JOptionPane.YES_NO_OPTION);
		if (confirmation != JOptionPane.YES_OPTION)
			return;

		try
		{
			employe.remove();
			fenetre.afficherPage(new PanelGestionEmployes(fenetre, ligue));
		}
		catch (SauvegardeImpossible ex)
		{
			JOptionPane.showMessageDialog(parent, "Impossible de supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
}