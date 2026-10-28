package fr.efrei.transport;

public class ServiceClient {

    private final ClientRepository clientRepository;
    private final ValidateurClient validateur;
    private final CanalNotification canalNotification;

    public ServiceClient(ClientRepository clientRepository, ValidateurClient validateur, CanalNotification canalNotification) {
        this.clientRepository = clientRepository;
        this.validateur = validateur;
        this.canalNotification = canalNotification;
    }

    public Client creerClient(String nom, String email, String telephone, String adresse, TypeClient type, String siret) {
        String erreur = validateur.valider(nom, email, telephone, adresse, type, siret);
        if (erreur != null) {
            System.out.println(erreur);
            return null;
        }
        Client c = new Client(nom, email, telephone, adresse, type, siret);
        clientRepository.sauvegarder(c);
        canalNotification.envoyerEmail(c.getEmail(), "Bienvenue chez TransFret", "Bonjour " + c.getNom() + ", votre compte " + type.getLibelle().toLowerCase() + " a été créé.");
        return c;
    }
}
