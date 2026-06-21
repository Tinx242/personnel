package swing;

import javax.swing.*;
import java.awt.*;

import personnel.Employe;
import personnel.GestionPersonnel;

/**
 * Page de connexion. Correspond à la maquette "login.png".
 * Contrairement à la version console (qui ne vérifiait que le mot de passe
 * du root), cette page authentifie n'importe quel employé à partir de son
 * identifiant + mot de passe, grâce à la nouvelle méthode
 * {@link GestionPersonnel#authentifier}.
 */
public class PanelConnexion extends JPanel
{
	public PanelConnexion(FenetrePrincipale fenetre)
	{
		setLayout(new GridBagLayout());
		GridBagConstraints contraintes = new GridBagConstraints();
		contraintes.insets = new Insets(8, 8, 8, 8);

		JPanel formulaire = new JPanel(new GridLayout(2, 2, 10, 10));
		JTextField champIdentifiant = new JTextField(15);
		JPasswordField champPassword = new JPasswordField(15);
		formulaire.add(new JLabel("Identifiant"));
		formulaire.add(champIdentifiant);
		formulaire.add(new JLabel("Mot de passe"));
		formulaire.add(champPassword);

		JButton boutonConnexion = new JButton("Connexion");

		contraintes.gridy = 0;
		add(formulaire, contraintes);
		contraintes.gridy = 1;
		add(boutonConnexion, contraintes);

		GestionPersonnel gestionPersonnel = fenetre.getGestionPersonnel();

		// Valide aussi en appuyant sur Entrée dans le champ mot de passe.
		champPassword.addActionListener(e -> boutonConnexion.doClick());

		boutonConnexion.addActionListener(e ->
		{
			String identifiant = champIdentifiant.getText().trim();
			String motDePasse = new String(champPassword.getPassword());

			Employe employe = gestionPersonnel.authentifier(identifiant, motDePasse);
			if (employe == null)
				JOptionPane.showMessageDialog(this,
						"Identifiant ou mot de passe incorrect.",
						"Connexion refusée", JOptionPane.ERROR_MESSAGE);
			else
				fenetre.connecter(employe);
		});
	}
}
