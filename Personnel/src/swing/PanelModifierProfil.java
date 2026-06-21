package swing;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import personnel.DatesIncoherentesException;
import personnel.DroitsInsuffisants;
import personnel.Employe;
import personnel.Ligue;
import personnel.SauvegardeImpossible;

/**
 * Formulaire d'édition d'un profil employé. Une seule classe couvre 3
 * situations différentes des maquettes :
 *  - un employé modifie son propre profil juste après connexion
 *    (modifier_un_profil.png) → actionRetour = null (pas de bouton retour,
 *    comme sur la maquette : la seule sortie est QUITTER).
 *  - le root modifie son propre profil (modifier_un_profil_root.png),
 *    atteint en cliquant sur son nom dans la barre latérale depuis la page
 *    des ligues → actionRetour = retour vers la liste des ligues.
 *  - le root modifie le profil d'un autre employé, via le bouton GERER de
 *    la liste des employés → actionRetour = retour vers cette liste.
 *
 * Important : la barre latérale affiche toujours l'employé CONNECTÉ
 * (voir BarreLaterale), qui n'est pas forcément celui qu'on édite ici.
 */
public class PanelModifierProfil extends JPanel
{
	private FenetrePrincipale fenetre;
	private Employe employeEdite;

	private JTextField champNom;
	private JTextField champPrenom;
	private JTextField champMail;
	private JPasswordField champPassword;
	private JTextField champDateArrivee;
	private JTextField champDateDepart;
	private JCheckBox caseAdministrateur; // null si on n'a pas le droit de changer le rôle ici

	public PanelModifierProfil(FenetrePrincipale fenetre, Employe employeEdite, Runnable actionRetour)
	{
		this.fenetre = fenetre;
		this.employeEdite = employeEdite;
		setLayout(new BorderLayout());

		add(construireBarreLaterale(fenetre, actionRetour), BorderLayout.WEST);
		add(new JScrollPane(construireFormulaire()), BorderLayout.CENTER);
	}

	private BarreLaterale construireBarreLaterale(FenetrePrincipale fenetre, Runnable actionRetour)
	{
		JButton[] boutonsNavigation;
		if (actionRetour == null)
		{
			boutonsNavigation = new JButton[0];
		}
		else
		{
			JButton boutonRetour = new JButton("RETOUR");
			boutonRetour.addActionListener(e -> actionRetour.run());
			boutonsNavigation = new JButton[] { boutonRetour };
		}

		// Cliquer sur son propre profil alors qu'on y est déjà : on se contente
		// de réafficher la même page (rien à faire de plus).
		Runnable actionProfil = () -> fenetre.afficherPage(
				new PanelModifierProfil(fenetre, fenetre.getEmployeConnecte(), actionRetour));

		return new BarreLaterale(fenetre, actionProfil, boutonsNavigation);
	}

	private JPanel construireFormulaire()
	{
		JPanel contenu = new JPanel();
		contenu.setBackground(Color.WHITE);
		contenu.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
		contenu.setLayout(new BoxLayout(contenu, BoxLayout.Y_AXIS));

		JLabel titre = new JLabel("Modifier profil");
		titre.setFont(titre.getFont().deriveFont(Font.BOLD, 22f));
		titre.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenu.add(titre);
		contenu.add(Box.createVerticalStrut(20));

		champNom = new JTextField(employeEdite.getNom());
		champPrenom = new JTextField(employeEdite.getPrenom());
		champMail = new JTextField(employeEdite.getMail());
		// Le mot de passe n'est jamais pré-rempli : on ne connaît que son hash,
		// pas sa valeur en clair (voir MotDePasse.java). Champ vide = on ne le change pas.
		champPassword = new JPasswordField();
		champDateArrivee = new JTextField(ComposantsUI.dateVersTexte(employeEdite.getDateArrivee()));
		champDateDepart = new JTextField(ComposantsUI.dateVersTexte(employeEdite.getDateDepart()));

		contenu.add(ComposantsUI.ligneDeuxChamps("NOM", champNom, "PRENOM", champPrenom));
		contenu.add(Box.createVerticalStrut(10));
		contenu.add(ComposantsUI.ligneUnChamp("MAIL", champMail));
		contenu.add(Box.createVerticalStrut(10));
		contenu.add(ComposantsUI.ligneUnChamp("MOT DE PASSE", champPassword));
		contenu.add(Box.createVerticalStrut(10));
		contenu.add(ComposantsUI.ligneDeuxChamps("DATE D'ARRIVEE", champDateArrivee,
				"DATE DE DEPART", champDateDepart));
		contenu.add(Box.createVerticalStrut(20));

		ajouterCaseAdministrateurSiAutorise(contenu);

		JButton boutonEnregistrer = new JButton("ENREGISTRER");
		boutonEnregistrer.setAlignmentX(Component.LEFT_ALIGNMENT);
		boutonEnregistrer.addActionListener(e -> enregistrer());
		contenu.add(boutonEnregistrer);

		return contenu;
	}

	/**
	 * N'affiche la case "Administrateur de la ligue" que si :
	 *  - l'employé édité appartient bien à une ligue (donc n'est pas le root) ;
	 *  - ET la personne connectée a le droit de changer ça : le root, ou
	 *    l'administrateur actuel de cette même ligue.
	 * C'est ce contrôle qui manquait : auparavant, rien dans l'interface ne
	 * permettait de changer le rôle d'un employé.
	 */
	private void ajouterCaseAdministrateurSiAutorise(JPanel contenu)
	{
		Ligue ligue = employeEdite.getLigue();
		if (ligue == null)
			return; // employeEdite est le root : pas de ligue, pas de rôle à gérer

		Employe connecte = fenetre.getEmployeConnecte();
		if (!connecte.peutGerer(ligue))
			return;

		caseAdministrateur = new JCheckBox("Administrateur de la ligue", ligue.getAdministrateur() == employeEdite);
		caseAdministrateur.setOpaque(false);
		caseAdministrateur.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenu.add(caseAdministrateur);
		contenu.add(Box.createVerticalStrut(20));
	}

	private void enregistrer()
	{
		try
		{
			employeEdite.setNom(champNom.getText());
			employeEdite.setPrenom(champPrenom.getText());
			employeEdite.setMail(champMail.getText());

			String motDePasse = new String(champPassword.getPassword());
			if (!motDePasse.trim().isEmpty())
				employeEdite.setPassword(motDePasse);

			employeEdite.setDateArrivee(ComposantsUI.texteVersDate(champDateArrivee.getText()));
			employeEdite.setDateDepart(ComposantsUI.texteVersDate(champDateDepart.getText()));

			enregistrerRoleSiPresent();

			JOptionPane.showMessageDialog(this, "Profil enregistré.");
		}
		catch (DateTimeParseException ex)
		{
			JOptionPane.showMessageDialog(this, "Format de date invalide (attendu : AAAA-MM-JJ).",
					"Erreur de saisie", JOptionPane.ERROR_MESSAGE);
		}
		catch (DatesIncoherentesException ex)
		{
			JOptionPane.showMessageDialog(this, "La date de départ ne peut pas précéder la date d'arrivée.",
					"Dates incohérentes", JOptionPane.ERROR_MESSAGE);
		}
		catch (DroitsInsuffisants ex)
		{
			JOptionPane.showMessageDialog(this, "Cet employé ne peut pas être administrateur de cette ligue.",
					"Droits insuffisants", JOptionPane.ERROR_MESSAGE);
		}
		catch (SauvegardeImpossible ex)
		{
			JOptionPane.showMessageDialog(this, "Impossible d'enregistrer.", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Si la case "Administrateur" est cochée, l'employé édité devient
	 * administrateur de sa ligue. Si elle est décochée alors qu'il l'était,
	 * l'administration revient au root (il n'existe pas de "pas
	 * d'administrateur" dans le modèle : c'est toujours le root par défaut).
	 */
	private void enregistrerRoleSiPresent() throws SauvegardeImpossible
	{
		if (caseAdministrateur == null)
			return;

		Ligue ligue = employeEdite.getLigue();
		if (caseAdministrateur.isSelected())
			ligue.setAdministrateur(employeEdite);
		else if (ligue.getAdministrateur() == employeEdite)
			ligue.setAdministrateur(fenetre.getGestionPersonnel().getRoot());
	}
}