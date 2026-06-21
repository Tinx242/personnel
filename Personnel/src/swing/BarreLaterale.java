package swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import personnel.Employe;

/**
 * Barre latérale gauche réutilisée par plusieurs pages : logo M2L,
 * d'éventuels boutons de navigation (différents selon la page), puis en bas
 * les informations de l'employé CONNECTÉ (cliquables pour aller modifier
 * son propre profil) et le bouton QUITTER.
 *
 * Important : cette barre affiche toujours l'employé connecté
 * (fenetre.getEmployeConnecte()), pas forcément l'employé qu'on est en train
 * de consulter ou modifier dans la zone principale de la page.
 */
public class BarreLaterale extends JPanel
{
	public BarreLaterale(FenetrePrincipale fenetre, Runnable actionProfil, JButton... boutonsNavigation)
	{
		setPreferredSize(new Dimension(220, 0));
		setLayout(new BorderLayout());
		setBackground(new Color(232, 230, 222));

		JLabel logo = new JLabel("M2L", SwingConstants.CENTER);
		logo.setFont(logo.getFont().deriveFont(Font.BOLD, 30f));
		logo.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
		add(logo, BorderLayout.NORTH);

		JPanel zoneNavigation = new JPanel();
		zoneNavigation.setOpaque(false);
		zoneNavigation.setLayout(new BoxLayout(zoneNavigation, BoxLayout.Y_AXIS));
		for (JButton bouton : boutonsNavigation)
		{
			bouton.setAlignmentX(Component.CENTER_ALIGNMENT);
			zoneNavigation.add(Box.createVerticalStrut(15));
			zoneNavigation.add(bouton);
		}
		add(zoneNavigation, BorderLayout.CENTER);

		add(construireZoneBas(fenetre, actionProfil), BorderLayout.SOUTH);
	}

	private JPanel construireZoneBas(FenetrePrincipale fenetre, Runnable actionProfil)
	{
		Employe employe = fenetre.getEmployeConnecte();

		JPanel zoneProfil = new JPanel();
		zoneProfil.setOpaque(false);
		zoneProfil.setLayout(new BoxLayout(zoneProfil, BoxLayout.Y_AXIS));

		JLabel nomEmploye = new JLabel(employe.getNom(), SwingConstants.CENTER);
		nomEmploye.setAlignmentX(Component.CENTER_ALIGNMENT);
		nomEmploye.setFont(nomEmploye.getFont().deriveFont(Font.BOLD));
		zoneProfil.add(nomEmploye);

		if (!employe.estRoot())
		{
			JLabel nomLigue = new JLabel(employe.getLigue().getNom(), SwingConstants.CENTER);
			nomLigue.setAlignmentX(Component.CENTER_ALIGNMENT);
			zoneProfil.add(nomLigue);
		}

		String role = employe.estRoot() ? "root"
				: employe.estAdmin(employe.getLigue()) ? "administrateur" : "employé";
		JLabel labelRole = new JLabel(role, SwingConstants.CENTER);
		labelRole.setAlignmentX(Component.CENTER_ALIGNMENT);
		zoneProfil.add(labelRole);

		// Toute la zone "profil" est cliquable : c'est notre façon d'accéder à
		// "Modifier mon profil" (la maquette ne montre pas explicitement ce clic,
		// mais c'est le seul moyen logique d'atteindre cette page depuis ici).
		zoneProfil.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		zoneProfil.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				actionProfil.run();
			}
		});

		JButton boutonQuitter = new JButton("QUITTER");
		boutonQuitter.setForeground(Color.RED);
		boutonQuitter.setAlignmentX(Component.CENTER_ALIGNMENT);
		boutonQuitter.addActionListener(e -> DialogQuitter.afficher(fenetre, fenetre.getGestionPersonnel()));

		JPanel zoneBas = new JPanel();
		zoneBas.setOpaque(false);
		zoneBas.setLayout(new BoxLayout(zoneBas, BoxLayout.Y_AXIS));
		zoneBas.add(zoneProfil);
		zoneBas.add(Box.createVerticalStrut(20));
		zoneBas.add(boutonQuitter);
		zoneBas.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

		return zoneBas;
	}
}