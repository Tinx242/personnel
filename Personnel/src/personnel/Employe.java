package personnel;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Employé d'une ligue hébergée par la M2L. Certains peuvent
 * être administrateurs des employés de leur ligue.
 * Un seul employé, rattaché à aucune ligue, est le root.
 * Il est impossible d'instancier directement un employé,
 * il faut passer par la méthode {@link Ligue#addEmploye addEmploye}.
 */
public class Employe implements Serializable, Comparable<Employe> {
    private static final long serialVersionUID = 4795721718037994734L;
    private String nom, prenom, password, mail;
    private Ligue ligue;
    private GestionPersonnel gestionPersonnel;
    private LocalDate dateArrivee;
    private LocalDate dateDepart;
    private int id;

 // Nouvel employé : le mot de passe arrive EN CLAIR, on le hache.
    Employe(GestionPersonnel gestionPersonnel, Ligue ligue, String nom, String prenom,
            String mail, String password, LocalDate dateArrivee, LocalDate dateDepart)
            throws DatesIncoherentesException {
        this.gestionPersonnel = gestionPersonnel;
        this.nom = nom;
        this.prenom = prenom;
        this.password = MotDePasse.hacher(password);
        this.mail = mail;
        this.ligue = ligue;

        validerDates(dateArrivee, dateDepart);

        this.dateArrivee = dateArrivee;
        this.dateDepart = dateDepart;
    }

    // Chargement depuis la base : le mot de passe est DÉJÀ HACHÉ, on le stocke tel quel.
    Employe(GestionPersonnel gestionPersonnel, Ligue ligue, int id, String nom, String prenom,
            String mail, String password, LocalDate dateArrivee, LocalDate dateDepart)
            throws DatesIncoherentesException {
        this.gestionPersonnel = gestionPersonnel;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.mail = mail;
        this.ligue = ligue;

        validerDates(dateArrivee, dateDepart);

        this.dateArrivee = dateArrivee;
        this.dateDepart = dateDepart;
        this.id = id;
    }

    /**
     * Valide que les dates d'arrivée et de départ sont cohérentes.
     * @param dateArrivee La date d'arrivée.
     * @param dateDepart La date de départ.
     * @throws DatesIncoherentesException Si les dates sont incohérentes.
     */
    private void validerDates(LocalDate dateArrivee, LocalDate dateDepart) throws DatesIncoherentesException {
        if (dateArrivee != null && dateDepart != null && dateDepart.isBefore(dateArrivee)) {
            throw new DatesIncoherentesException("La date de départ ne peut pas être antérieure à la date d'arrivée.");
        }
    }

    /**
     * Retourne vrai si l'employé est administrateur de la ligue
     * passée en paramètre.
     * @param ligue La ligue pour laquelle vérifier si l'employé est administrateur.
     * @return Vrai si l'employé est administrateur de la ligue.
     */
    public boolean estAdmin(Ligue ligue) {
        return ligue.getAdministrateur() == this;
    }

    /**
     * Retourne vrai si l'employé est le root.
     * @return Vrai si l'employé est le root.
     */
    public boolean estRoot() {
        return gestionPersonnel.getRoot() == this;
    }

    // ===================== Droits d'accès =====================
    // Ces trois méthodes centralisent "qui a le droit de faire quoi".
    // Ce sont des règles métier (pas de l'affichage) : c'est pour ça
    // qu'elles vivent ici, sur Employe, et pas dispersées dans les pages
    // Swing. Toute page qui a besoin de savoir si un bouton doit être
    // affiché interroge l'employé connecté via ces méthodes, plutôt que de
    // refaire le test estRoot() / estAdmin() à chaque endroit.

    /**
     * Retourne vrai si l'employé peut créer/renommer/supprimer des ligues
     * (uniquement le root).
     */
    public boolean peutGererLigues() {
        return estRoot();
    }

    /**
     * Retourne vrai si l'employé peut consulter le détail (la liste des
     * employés) de la ligue passée en paramètre : le root peut consulter
     * n'importe quelle ligue, un employé seulement la sienne.
     */
    public boolean peutVoir(Ligue ligue) {
        return estRoot() || getLigue() == ligue;
    }

    /**
     * Retourne vrai si l'employé peut gérer (ajouter/modifier/supprimer des
     * employés, changer l'administrateur) la ligue passée en paramètre : le
     * root peut gérer n'importe quelle ligue, un administrateur seulement
     * la sienne, un simple employé aucune.
     */
    public boolean peutGerer(Ligue ligue) {
        return estRoot() || estAdmin(ligue);
    }

    /**
     * Retourne le nom de l'employé.
     * @return Le nom de l'employé.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Change le nom de l'employé.
     * @param nom Le nouveau nom.
     */
    public void setNom(String nom) throws SauvegardeImpossible {
        this.nom = nom;
        gestionPersonnel.update(this);
    }

    /**
     * Retourne le prénom de l'employé.
     * @return Le prénom de l'employé.
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Change le prénom de l'employé.
     * @param prenom Le nouveau prénom de l'employé.
     * @throws SauvegardeImpossible 
     */
    public void setPrenom(String prenom) throws SauvegardeImpossible {
        this.prenom = prenom;
        gestionPersonnel.update(this);
    }

    /**
     * Retourne le mail de l'employé.
     * @return Le mail de l'employé.
     */
    public String getMail() {
        return mail;
    }

    /**
     * Change le mail de l'employé.
     * @param mail Le nouveau mail de l'employé.
     * @throws SauvegardeImpossible 
     */
    public void setMail(String mail) throws SauvegardeImpossible {
        this.mail = mail;
        gestionPersonnel.update(this);
    }

    /**
     * Retourne vrai si le mot de passe passé en paramètre est celui de l'employé.
     * @param password Le mot de passe à comparer.
     * @return Vrai si le mot de passe est correct.
     */
    public boolean checkPassword(String password) {
        return MotDePasse.verifier(password, this.password);
    }

    
    public String getPassword() {
    	return password;
    }

    /**
     * Change le mot de passe de l'employé.
     * @param password Le nouveau mot de passe.
     * @throws SauvegardeImpossible 
     */
    public void setPassword(String password) throws SauvegardeImpossible {
        this.password = MotDePasse.hacher(password);
        gestionPersonnel.update(this);
    }

    /**
     * Retourne la ligue à laquelle l'employé est affecté.
     * @return La ligue de l'employé.
     */
    public Ligue getLigue() {
        return ligue;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    public LocalDate getDateArrivee() {
        return dateArrivee;
    }
    
    
    public void setDateArrivee(LocalDate dateArrivee) throws DatesIncoherentesException, SauvegardeImpossible {
        validerDates(dateArrivee, this.dateDepart);
        this.dateArrivee = dateArrivee;
        gestionPersonnel.update(this);
    }

    
    
    public LocalDate getDateDepart() {
        return dateDepart;
    }
    
    
    public void setDateDepart(LocalDate dateDepart) throws DatesIncoherentesException, SauvegardeImpossible {
        validerDates(this.dateArrivee, dateDepart);
        this.dateDepart = dateDepart;
        gestionPersonnel.update(this);
    }

    /**
     * Supprime l'employé. Si celui-ci est administrateur, le root
     * récupère les droits d'administration sur sa ligue.
     * @throws SauvegardeImpossible 
     * @throws ImpossibleDeSupprimerRoot Si l'employé est le root.
     */
    public void remove() throws SauvegardeImpossible {
        Employe root = gestionPersonnel.getRoot();
        if (this != root) {
            if (estAdmin(getLigue())) {
                getLigue().setAdministrateur(root);
            }
            getLigue().remove(this);
            gestionPersonnel.delete(this);
        } else {
            throw new ImpossibleDeSupprimerRoot();
        }
    }

    @Override
    public int compareTo(Employe autre) {
        int cmp = getNom().compareTo(autre.getNom());
        if (cmp != 0) {
            return cmp;
        }
        return getPrenom().compareTo(autre.getPrenom());
    }

    @Override
    public String toString() {
        String res = nom + " " + prenom + " " + mail + " (";
        if (estRoot()) {
            res += "super-utilisateur";
        } else {
            res += ligue.toString();
        }
        return res + ")";
    }

    public class ImpossibleDeSupprimerRoot extends RuntimeException {
        public ImpossibleDeSupprimerRoot() {
            super("Il est impossible de supprimer le root.");
        }
    }
}