package fr.efrei.transport;

public class Notificateur {

    public void notifierCreation(Client client, String idExpedition, String villeDepart, String villeArrivee, ModeTransport mode, double prixTTC, String dateLivraison) {
        EmailService.envoyer(client.getEmail(), "Confirmation de votre expédition " + idExpedition,
                "Votre expédition " + villeDepart + " -> " + villeArrivee + " (" + mode.getLibelle() + ") a été enregistrée. Montant : " + prixTTC + " € TTC.");
        if (client.getType() == TypeClient.PREMIUM) {
            new SmsService().envoyerSms(client.getTelephone(), "TransFret : expédition " + idExpedition + " créée, livraison prévue le " + dateLivraison);
        }
    }

    public void notifierDepart(Client client, String idExpedition, String villeDepart) {
        EmailService.envoyer(client.getEmail(), "Votre expédition " + idExpedition + " est en route",
                "Votre expédition a quitté " + villeDepart + ".");
        if (client.getType() == TypeClient.PREMIUM) {
            new SmsService().envoyerSms(client.getTelephone(), "TransFret : expédition " + idExpedition + " partie de " + villeDepart);
        }
    }

    public void notifierLivraison(Client client, String idExpedition, String villeArrivee) {
        EmailService.envoyer(client.getEmail(), "Votre expédition " + idExpedition + " est livrée",
                "Votre expédition est arrivée à " + villeArrivee + ". Merci de votre confiance.");
        if (client.getType() == TypeClient.PREMIUM) {
            new SmsService().envoyerSms(client.getTelephone(), "TransFret : expédition " + idExpedition + " livrée à " + villeArrivee);
        }
    }

    public void notifierAnnulation(Client client, String idExpedition, double fraisAnnulation) {
        EmailService.envoyer(client.getEmail(), "Annulation de votre expédition " + idExpedition,
                "Votre expédition a été annulée. Frais d'annulation : " + fraisAnnulation + " €");
        if (client.getType() == TypeClient.PREMIUM) {
            new SmsService().envoyerSms(client.getTelephone(), "TransFret : expédition " + idExpedition + " annulée");
        }
    }
}
