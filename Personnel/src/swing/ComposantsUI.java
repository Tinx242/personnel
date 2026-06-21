package swing;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * Petites fabriques de composants réutilisées par plusieurs pages,
 * pour ne pas réécrire dix fois le même code de mise en forme.
 */
public class ComposantsUI
{
	private static final Color COULEUR_BORDURE = new Color(190, 190, 190);
	private static final Color COULEUR_ENTETE = new Color(218, 218, 218);

	public static JLabel libelleGras(String texte)
	{
		JLabel label = new JLabel(texte);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		return label;
	}

	/**
	 * Encadre n'importe quel composant (un JLabel, un JPanel de boutons...)
	 * pour qu'il ressemble à une cellule de tableau : bordure fine grise,
	 * un peu de marge intérieure, fond blanc. C'est ce qui donne l'effet
	 * "grille" qu'on voit sur les maquettes, plutôt que des éléments qui
	 * flottent sans repère visuel.
	 */
	public static JComponent cellule(JComponent contenu)
	{
		contenu.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COULEUR_BORDURE),
				BorderFactory.createEmptyBorder(8, 10, 8, 10)));
		contenu.setOpaque(true);
		contenu.setBackground(Color.WHITE);
		return contenu;
	}

	/** Une cellule de texte simple, encadrée comme {@link #cellule}. */
	public static JComponent celluleTexte(String texte)
	{
		return cellule(new JLabel(texte));
	}

	/** Une cellule d'en-tête de tableau : texte en gras, fond gris clair. */
	public static JComponent celluleEntete(String texte)
	{
		JLabel label = new JLabel(texte);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setOpaque(true);
		label.setBackground(COULEUR_ENTETE);
		label.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COULEUR_BORDURE),
				BorderFactory.createEmptyBorder(8, 10, 8, 10)));
		return label;
	}

	/**
	 * Une grille de type "tableau" prête à l'emploi : on lui donne le nombre
	 * de colonnes, elle renvoie un JPanel sans espace entre les cellules
	 * (c'est volontaire : ce sont les bordures des cellules elles-mêmes,
	 * collées les unes aux autres, qui dessinent les traits de la grille).
	 */
	public static JPanel grilleTableau(int colonnes)
	{
		return new JPanel(new GridLayout(0, colonnes, 0, 0));
	}

	/** Un champ précédé de son libellé en gras, empilés verticalement. */
	public static JPanel ligneUnChamp(String libelle, JComponent champ)
	{
		JPanel ligne = new JPanel(new BorderLayout(5, 5));
		ligne.setOpaque(false);
		ligne.add(libelleGras(libelle), BorderLayout.NORTH);
		ligne.add(champ, BorderLayout.CENTER);
		return ligne;
	}

	/** Deux champs côte à côte, chacun avec son libellé (ex : NOM / PRENOM). */
	public static JPanel ligneDeuxChamps(String libelle1, JComponent champ1, String libelle2, JComponent champ2)
	{
		JPanel ligne = new JPanel(new GridLayout(1, 2, 20, 0));
		ligne.setOpaque(false);
		ligne.add(ligneUnChamp(libelle1, champ1));
		ligne.add(ligneUnChamp(libelle2, champ2));
		return ligne;
	}

	/**
	 * Convertit un texte au format AAAA-MM-JJ en LocalDate.
	 * Retourne null si le texte est vide (date non renseignée).
	 * @throws java.time.format.DateTimeParseException si le texte n'est pas une date valide.
	 */
	public static LocalDate texteVersDate(String texte)
	{
		return (texte == null || texte.trim().isEmpty()) ? null : LocalDate.parse(texte.trim());
	}

	public static String dateVersTexte(LocalDate date)
	{
		return date == null ? "" : date.toString();
	}
}