package personnel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hachage des mots de passe avec SHA-256 + sel aléatoire.
 * La chaîne stockée a la forme : selBase64:hashBase64
 */
public final class MotDePasse
{
    private static final int LONGUEUR_SEL = 16; // octets
    private static final SecureRandom RANDOM = new SecureRandom();

    private MotDePasse() {} // classe utilitaire : pas d'instanciation

    /** Hache un mot de passe en clair et renvoie la chaîne à stocker. */
    public static String hacher(String motDePasseEnClair)
    {
        if (motDePasseEnClair == null)
            motDePasseEnClair = "";
        byte[] sel = new byte[LONGUEUR_SEL];
        RANDOM.nextBytes(sel);                       // un sel différent à chaque fois
        byte[] hash = sha256(sel, motDePasseEnClair);
        return Base64.getEncoder().encodeToString(sel) + ":"
             + Base64.getEncoder().encodeToString(hash);
    }

    /** Vérifie qu'un mot de passe en clair correspond à la chaîne stockée. */
    public static boolean verifier(String motDePasseEnClair, String stocke)
    {
        if (motDePasseEnClair == null || stocke == null)
            return false;
        String[] parties = stocke.split(":");
        if (parties.length != 2)
            return false; // format inattendu (ex : ancien mot de passe en clair)
        byte[] sel         = Base64.getDecoder().decode(parties[0]);
        byte[] hashAttendu = Base64.getDecoder().decode(parties[1]);
        byte[] hashCalcule = sha256(sel, motDePasseEnClair);
        // isEqual compare à temps constant (anti-attaque temporelle)
        return MessageDigest.isEqual(hashAttendu, hashCalcule);
    }

    private static byte[] sha256(byte[] sel, String motDePasse)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(sel);                                           // on sale d'abord
            md.update(motDePasse.getBytes(StandardCharsets.UTF_8));   // puis le mot de passe
            return md.digest();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Algorithme SHA-256 indisponible", e);
        }
    }
}