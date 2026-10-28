package fr.efrei.transport;

public class ServiceClient {

    private ValidateurClient validateur = new ValidateurClient();

    public Client creerClient(String nom, String email, String telephone, String adresse, TypeClient type, String siret) {
        String erreur = validateur.valider(nom, email, telephone, adresse, type, siret);
        if (erreur != null) {
            System.out.println(erreur);
            return null;
        }
        Client c = new Client(nom, email, telephone, adresse, type, siret);
        Database.getInstance().sauvegarderClient(c);
        EmailService.envoyer(c.getEmail(), "Bienvenue chez TransFret", "Bonjour " + c.getNom() + ", votre compte " + type.getLibelle().toLowerCase() + " a été créé.");
        return c;
    }
}
