package fr.efrei.transport;

public class Notificateur {

    private final CanalNotification canalNotification;

    public Notificateur(CanalNotification canalNotification) {
        this.canalNotification = canalNotification;
    }

    public void notifierCreation(Client client, String idExpedition, String villeDepart, String villeArrivee, ModeTransport mode, double prixTTC, String dateLivraison) {
        canalNotification.envoyerEmail(client.getEmail(), "Confirmation de votre expédition " + idExpedition,
                "Votre expédition " + villeDepart + " -> " + villeArrivee + " (" + mode.getLibelle() + ") a été enregistrée. Montant : " + prixTTC + " € TTC.");
        if (client.getType() == TypeClient.PREMIUM) {
            canalNotification.envoyerSms(client.getTelephone(), "TransFret : expédition " + idExpedition + " créée, livraison prévue le " + dateLivraison);
        }
    }

    public void notifierDepart(Client client, String idExpedition, String villeDepart) {
        canalNotification.envoyerEmail(client.getEmail(), "Votre expédition " + idExpedition + " est en route",
                "Votre expédition a quitté " + villeDepart + ".");
        if (client.getType() == TypeClient.PREMIUM) {
            canalNotification.envoyerSms(client.getTelephone(), "TransFret : expédition " + idExpedition + " partie de " + villeDepart);
        }
    }

    public void notifierLivraison(Client client, String idExpedition, String villeArrivee) {
        canalNotification.envoyerEmail(client.getEmail(), "Votre expédition " + idExpedition + " est livrée",
                "Votre expédition est arrivée à " + villeArrivee + ". Merci de votre confiance.");
        if (client.getType() == TypeClient.PREMIUM) {
            canalNotification.envoyerSms(client.getTelephone(), "TransFret : expédition " + idExpedition + " livrée à " + villeArrivee);
        }
    }

    public void notifierAnnulation(Client client, String idExpedition, double fraisAnnulation) {
        canalNotification.envoyerEmail(client.getEmail(), "Annulation de votre expédition " + idExpedition,
                "Votre expédition a été annulée. Frais d'annulation : " + fraisAnnulation + " €");
        if (client.getType() == TypeClient.PREMIUM) {
            canalNotification.envoyerSms(client.getTelephone(), "TransFret : expédition " + idExpedition + " annulée");
        }
    }
}
