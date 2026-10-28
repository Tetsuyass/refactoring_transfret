package fr.efrei.transport;

public class ValidateurClient {

    private static final int LONGUEUR_SIRET = 14;

    public String valider(String nom, String email, String telephone, String adresse, TypeClient type, String siret) {
        if (nom == null || nom.trim().length() == 0) {
            return "ERREUR : nom obligatoire";
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            return "ERREUR : email invalide (" + email + ")";
        }
        if (type == null) {
            return "ERREUR : type de client inconnu";
        }
        if (type == TypeClient.ENTREPRISE && (siret == null || siret.length() != LONGUEUR_SIRET)) {
            return "ERREUR : SIRET obligatoire (14 chiffres) pour une entreprise";
        }
        return null; // Validation OK
    }
}
