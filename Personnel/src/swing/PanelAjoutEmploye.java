package swing;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import personnel.DatesIncoherentesException;
import personnel.Ligue;
import personnel.SauvegardeImpossible;

/**
 * Formulaire de création d'un employé. Correspond à "ajout_dun_employe.png".
 */
public class PanelAjoutEmploye extends JPanel
{
	private JTextField champNom = new JTextField();
	private JTextField champPrenom = new JTextField();
	private JTextField champMail = new JTextField();
	private JPasswordField champPassword = new JPasswordField();
	private JTextField champDateArrivee = new JTextField();
	private JTextField champDateDepart = new JTextField();

	public PanelAjoutEmploye(FenetrePrincipale fenetre, Ligue ligue)
	{
		setLayout(new BorderLayout());

		Runnable retourListeEmployes = () -> fenetre.afficherPage(new PanelGestionEmployes(fenetre, ligue));

		add(construireEntete(retourListeEmployes), BorderLayout.NORTH);
		add(new JScrollPane(construireFormulaire(fenetre, ligue, retourListeEmployes)), BorderLayout.CENTER);
	}

	private JPanel construireEntete(Runnable retourListeEmployes)
	{
		JPanel entete = new JPanel(new BorderLayout());
		entete.setBackground(Color.WHITE);
		entete.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
				BorderFactory.createEmptyBorder(15, 15, 15, 15)));

		JButton boutonRetour = new JButton("RETOUR");
		boutonRetour.setForeground(Color.RED);
		boutonRetour.addActionListener(e -> retourListeEmployes.run());
		entete.add(boutonRetour, BorderLayout.WEST);

		JLabel titre = new JLabel("Ajouter un employé", SwingConstants.CENTER);
		titre.setFont(titre.getFont().deriveFont(Font.BOLD, 20f));
		entete.add(titre, BorderLayout.CENTER);

		return entete;
	}

	private JPanel construireFormulaire(FenetrePrincipale fenetre, Ligue ligue, Runnable retourListeEmployes)
	{
		JPanel formulaire = new JPanel();
		formulaire.setBackground(Color.WHITE);
		formulaire.setLayout(new BoxLayout(formulaire, BoxLayout.Y_AXIS));
		formulaire.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

		formulaire.add(ComposantsUI.ligneDeuxChamps("NOM", champNom, "PRENOM", champPrenom));
		formulaire.add(Box.createVerticalStrut(10));
		formulaire.add(ComposantsUI.ligneUnChamp("MAIL", champMail));
		formulaire.add(Box.createVerticalStrut(10));
		formulaire.add(ComposantsUI.ligneUnChamp("MOT DE PASSE", champPassword));
		formulaire.add(Box.createVerticalStrut(10));
		formulaire.add(ComposantsUI.ligneDeuxChamps("DATE D'ARRIVEE", champDateArrivee,
				"DATE DE DEPART", champDateDepart));
		formulaire.add(Box.createVerticalStrut(25));

		JButton boutonCreer = new JButton("CREER EMPLOYE");
		boutonCreer.addActionListener(e -> creerEmploye(ligue, retourListeEmployes));

		JPanel ligneBouton = new JPanel();
		ligneBouton.setBackground(Color.WHITE);
		ligneBouton.add(boutonCreer);
		formulaire.add(ligneBouton);

		return formulaire;
	}

	private void creerEmploye(Ligue ligue, Runnable retourListeEmployes)
	{
		try
		{
			LocalDate dateArrivee = ComposantsUI.texteVersDate(champDateArrivee.getText());
			LocalDate dateDepart = ComposantsUI.texteVersDate(champDateDepart.getText());

			ligue.addEmploye(champNom.getText(), champPrenom.getText(), champMail.getText(),
					new String(champPassword.getPassword()), dateArrivee, dateDepart);

			retourListeEmployes.run();
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
		catch (SauvegardeImpossible ex)
		{
			JOptionPane.showMessageDialog(this, "Impossible d'enregistrer le nouvel employé.",
					"Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
}
