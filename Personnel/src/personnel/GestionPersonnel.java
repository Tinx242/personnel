package personnel;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Gestion du personnel. Un seul objet de cette classe existe.
 * Il n'est pas possible d'instancier directement cette classe, 
 * la méthode {@link #getGestionPersonnel getGestionPersonnel} 
 * le fait automatiquement et retourne toujours le même objet.
 * Dans le cas où {@link #sauvegarder()} a été appelé lors 
 * d'une exécution précédente, c'est l'objet sauvegardé qui est
 * retourné.
 */

public class GestionPersonnel implements Serializable
{
	private static final long serialVersionUID = -105283113987886425L;
	private static GestionPersonnel gestionPersonnel = null;
	private SortedSet<Ligue> ligues;
	private Employe root;
	public final static int SERIALIZATION = 1, JDBC = 2, 
			TYPE_PASSERELLE = JDBC;  
	private static Passerelle passerelle = TYPE_PASSERELLE == JDBC ? new jdbc.JDBC() : new serialisation.Serialization();	
	
	/**
	 * Retourne l'unique instance de cette classe.
	 * Crée cet objet s'il n'existe déjà.
	 * @return l'unique objet de type {@link GestionPersonnel}.
	 */
	
	public static GestionPersonnel getGestionPersonnel()
	{
		if (gestionPersonnel == null)
		{
			gestionPersonnel = passerelle.getGestionPersonnel();
			if (gestionPersonnel == null)
				gestionPersonnel = new GestionPersonnel();
		}
		return gestionPersonnel;
	}

	public GestionPersonnel()
	{
		if (gestionPersonnel != null)
			throw new RuntimeException("Vous ne pouvez créer qu'une seuls instance de cet objet.");
		ligues = new TreeSet<>();
		root = addRoot("root", "toor");
		gestionPersonnel = this;
	}
	
	public void sauvegarder() throws SauvegardeImpossible
	{
		passerelle.sauvegarderGestionPersonnel(this);
	}
	
	/**
	 * Retourne la ligue dont administrateur est l'administrateur,
	 * null s'il n'est pas un administrateur.
	 * @param administrateur l'administrateur de la ligue recherchée.
	 * @return la ligue dont administrateur est l'administrateur.
	 */
	
	public Ligue getLigue(Employe administrateur)
	{
		if (administrateur.estAdmin(administrateur.getLigue()))
			return administrateur.getLigue();
		else
			return null;
	}

	/**
	 * Retourne toutes les ligues enregistrées.
	 * @return toutes les ligues enregistrées.
	 */
	
	public SortedSet<Ligue> getLigues()
	{
		return Collections.unmodifiableSortedSet(ligues);
	}

	public Ligue addLigue(String nom) throws SauvegardeImpossible
	{
		Ligue ligue = new Ligue(this, nom); 
		ligues.add(ligue);
		return ligue;
	}
	
	public Ligue addLigue(int id, String nom)
	{
		Ligue ligue = new Ligue(this, id, nom);
		ligues.add(ligue);
		return ligue;
	}
	
	public Employe addRoot(String nom, String password)
	{
	    try {
	        root = new Employe(this, null, nom, "", "", password, null, null);
	    } catch (DatesIncoherentesException e) {
	        throw new AssertionError("Ne devrait jamais arriver car les dates sont null", e);
	    }
	    return root;
	}
	
	public Employe addRoot(int id, String nom, String password)
	{
	    try {
	        root = new Employe(this, null, id, nom, "", "", password, null, null);
	    } catch (DatesIncoherentesException e) {
	        throw new AssertionError("Ne devrait jamais arriver car les dates sont null", e);
	    }
	    return root;
	}

	void remove(Ligue ligue)
	{
		ligues.remove(ligue);
	}
	
	int insert(Ligue ligue) throws SauvegardeImpossible
	{
		return passerelle.insert(ligue);
	}
	
	void update(Ligue ligue) throws SauvegardeImpossible
	{
		passerelle.update(ligue);
	}
	
	void updateAdministrateur(Ligue ligue) throws SauvegardeImpossible
	{
		passerelle.updateAdministrateur(ligue);
	}
	
	void delete(Ligue ligue) throws SauvegardeImpossible
	{
	    passerelle.delete(ligue);
	}
	
	int insert(Employe employe) throws SauvegardeImpossible
	{
	    return passerelle.insert(employe);
	}
	
	void update(Employe employe) throws SauvegardeImpossible
	{
		passerelle.update(employe);
	}
	
	void delete(Employe employe) throws SauvegardeImpossible
	{
	    passerelle.delete(employe);
	}

	/**
	 * Retourne le root (super-utilisateur).
	 * @return le root.
	 */
	
	public Employe getRoot()
	{
		return root;
	}

	/**
	 * Authentifie un employé à partir d'un identifiant et d'un mot de passe
	 * en clair. L'identifiant est soit le nom du root, soit le mail d'un
	 * employé d'une ligue. Utilisé par l'écran de connexion Swing (la
	 * version console ne vérifiait, elle, que le mot de passe du root).
	 * @param identifiant le nom du root, ou le mail de l'employé.
	 * @param motDePasse le mot de passe en clair saisi par l'utilisateur.
	 * @return l'employé authentifié, ou null si l'identifiant ou le mot de
	 * passe est incorrect.
	 */
	
	public Employe authentifier(String identifiant, String motDePasse)
	{
		if (identifiant.equals(root.getNom()) && root.checkPassword(motDePasse))
			return root;

		for (Ligue ligue : ligues)
			for (Employe employe : ligue.getEmployes())
				if (identifiant.equals(employe.getMail()) && employe.checkPassword(motDePasse))
					return employe;

		return null;
	}
}