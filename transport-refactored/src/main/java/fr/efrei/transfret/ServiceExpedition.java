package fr.efrei.transfret;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ServiceExpedition {

    // identifiant : "EXP-<compteur>-<suffixe aléatoire à 4 chiffres>"
    private static final int SUFFIXE_ID_MIN = 1000;
    private static final int SUFFIXE_ID_PLAGE = 9000;

    // tarification
    private static final double COEFF_TTC = 1.2; // TVA 20 %
    private static final double TAUX_FRAIS_ANNULATION = 0.5; // du prix TTC, si l'expédition est déjà partie

    // délais de livraison
    private static final double ROUTE_VITESSE_KMH = 70.0;
    private static final double ROUTE_HEURES_PAR_JOUR = 9;
    private static final double ROUTE_HEURES_PAR_JOUR_URGENT = 18;
    private static final double RAIL_KM_PAR_JOUR = 1440.0;
    private static final int RAIL_JOURS_SUPPLEMENTAIRES = 1;
    private static final double MER_KM_PAR_JOUR = 720.0;
    private static final int MER_JOURS_SUPPLEMENTAIRES = 2;
    private static final int AIR_DELAI_COURT_JOURS = 1;
    private static final int AIR_DELAI_LONG_COURRIER_JOURS = 2;
    private static final int AIR_SEUIL_LONG_COURRIER_KM = 3000;
    private static final int DELAI_MINIMUM_JOURS = 1;

    // départ du véhicule
    private static final double KM_PAR_MILLE_NAUTIQUE = 1.852;
    private static final int AIR_ROULAGE_KM = 2;
    private static final int AIR_ALTITUDE_CROISIERE_PIEDS = 35000;
    private static final double TEMPERATURE_REFRIGERATION_C = 4;

    public static int compteur = 0;

    private Database db = Database.getInstance();
    private CalculateurPrix calculateur = new CalculateurPrix();
    private AffectationVehicule affectation = new AffectationVehicule();

    public Expedition creerExpedition(Client client, List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode, boolean urgent, boolean assurance) {
        if (client == null) {
            System.out.println("ERREUR : client obligatoire");
            return null;
        }
        if (colis == null || colis.isEmpty()) {
            System.out.println("ERREUR : au moins un colis est nécessaire");
            return null;
        }
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            if (c.getPoids() <= 0) {
                System.out.println("ERREUR : poids invalide pour " + c.getDescription());
                return null;
            }
            if (c.getType() == TypeMarchandise.DANGEREUX) dangereux = true;
            if (c.getType() == TypeMarchandise.REFRIGERE) refrigere = true;
        }
        if (dangereux && mode == ModeTransport.AIR) {
            System.out.println("ERREUR : les matières dangereuses sont interdites en transport aérien");
            return null;
        }
        if (refrigere && !(mode == ModeTransport.ROUTE || mode == ModeTransport.MER)) {
            System.out.println("ERREUR : le transport réfrigéré n'est possible que par route ou par mer");
            return null;
        }
        if (urgent && mode == ModeTransport.MER) {
            System.out.println("ERREUR : pas d'option urgente pour le transport maritime");
            return null;
        }
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == -1) {
            System.out.println("ERREUR : trajet inconnu " + villeDepart + " -> " + villeArrivee);
            return null;
        }

        Vehicule v = affectation.trouverVehicule(colis, mode);
        if (v == null) {
            System.out.println("ERREUR : aucun véhicule disponible pour cette expédition");
            return null;
        }
        for (Colis c : colis) {
            v.charger(c);
        }

        Expedition e = new Expedition();
        compteur++;
        e.setId("EXP-" + compteur + "-" + (SUFFIXE_ID_MIN + new Random().nextInt(SUFFIXE_ID_PLAGE)));
        e.setClient(client);
        e.setColis(colis);
        e.setVilleDepart(villeDepart);
        e.setVilleArrivee(villeArrivee);
        e.setDistance(distance);
        e.setMode(mode);
        e.setUrgent(urgent);
        e.setAssurance(assurance);
        e.setStatut(StatutExpedition.CREEE);
        e.setVehicule(v);
        e.setPrixHT(calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, client.getType()));
        e.setPrixTTC(versTTC(e.getPrixHT()));
        e.setDateCreation(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(e.getDateCreation());
        cal.add(Calendar.DAY_OF_MONTH, calculerDelai(distance, mode, urgent));
        e.setDateLivraisonPrevue(cal.getTime());

        db.sauvegarderExpedition(e);
        notifier(client, "Confirmation de votre expédition " + e.getId(),
                "Votre expédition " + villeDepart + " -> " + villeArrivee + " (" + mode + ") a été enregistrée. Montant : " + e.getPrixTTC() + " € TTC.",
                "TransFret : expédition " + e.getId() + " créée, livraison prévue le " + Utils.formaterDate(e.getDateLivraisonPrevue()));
        return e;
    }

    // retourne le prix TTC, -1 si le trajet est inconnu
    public double calculerDevis(List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == -1) {
            return -1;
        }
        double ht = calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, typeClient);
        return versTTC(ht);
    }

    private static double versTTC(double prixHT) {
        return Utils.arrondir(prixHT * COEFF_TTC);
    }

    // retourne le délai de livraison en jours
    public int calculerDelai(int distance, ModeTransport mode, boolean urgent) {
        int jours = 0;
        switch (mode) {
            case ROUTE:
                double heures = distance / ROUTE_VITESSE_KMH;
                if (urgent) {
                    jours = (int) Math.ceil(heures / ROUTE_HEURES_PAR_JOUR_URGENT);
                } else {
                    jours = (int) Math.ceil(heures / ROUTE_HEURES_PAR_JOUR);
                }
                break;
            case RAIL:
                jours = (int) Math.ceil(distance / RAIL_KM_PAR_JOUR) + RAIL_JOURS_SUPPLEMENTAIRES;
                if (urgent) jours = jours - 1;
                break;
            case MER:
                jours = (int) Math.ceil(distance / MER_KM_PAR_JOUR) + MER_JOURS_SUPPLEMENTAIRES;
                break;
            case AIR:
                jours = AIR_DELAI_COURT_JOURS;
                if (distance > AIR_SEUIL_LONG_COURRIER_KM) jours = AIR_DELAI_LONG_COURRIER_JOURS;
                if (urgent) jours = AIR_DELAI_COURT_JOURS;
                break;
        }
        if (jours < DELAI_MINIMUM_JOURS) jours = DELAI_MINIMUM_JOURS;
        return jours;
    }

    public boolean demarrerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || e.getStatut() != StatutExpedition.CREEE) {
            System.out.println("ERREUR : impossible de démarrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.EN_COURS);
        Vehicule v = e.getVehicule();
        v.setDisponible(false);
        try {
            v.faireLePlein();
        } catch (Exception ex) {
        }
        if (e.getMode() == ModeTransport.ROUTE || e.getMode() == ModeTransport.RAIL) {
            v.rouler(e.getDistance());
        } else if (e.getMode() == ModeTransport.MER) {
            v.naviguer((int) (e.getDistance() / KM_PAR_MILLE_NAUTIQUE));
        } else if (e.getMode() == ModeTransport.AIR) {
            v.rouler(AIR_ROULAGE_KM);
            v.voler(AIR_ALTITUDE_CROISIERE_PIEDS);
        }
        for (Colis c : e.getColis()) {
            if (c.getType() == TypeMarchandise.REFRIGERE) {
                v.activerRefrigeration(TEMPERATURE_REFRIGERATION_C);
                break;
            }
        }
        db.mettreAJourStatut(e);
        notifier(e.getClient(), "Votre expédition " + e.getId() + " est en route",
                "Votre expédition a quitté " + e.getVilleDepart() + ".",
                "TransFret : expédition " + e.getId() + " partie de " + e.getVilleDepart());
        return true;
    }

    public boolean livrerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || e.getStatut() != StatutExpedition.EN_COURS) {
            System.out.println("ERREUR : impossible de livrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.LIVREE);
        libererVehicule(e);
        db.mettreAJourStatut(e);
        notifier(e.getClient(), "Votre expédition " + e.getId() + " est livrée",
                "Votre expédition est arrivée à " + e.getVilleArrivee() + ". Merci de votre confiance.",
                "TransFret : expédition " + e.getId() + " livrée à " + e.getVilleArrivee());
        return true;
    }

    public boolean annulerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || e.getStatut() == StatutExpedition.LIVREE || e.getStatut() == StatutExpedition.ANNULEE) {
            System.out.println("ERREUR : impossible d'annuler l'expédition " + id);
            return false;
        }
        if (e.getStatut() == StatutExpedition.EN_COURS) {
            // frais si déjà partie
            e.setFraisAnnulation(Utils.arrondir(e.getPrixTTC() * TAUX_FRAIS_ANNULATION));
        }
        e.setStatut(StatutExpedition.ANNULEE);
        libererVehicule(e);
        db.mettreAJourStatut(e);
        notifier(e.getClient(), "Annulation de votre expédition " + e.getId(),
                "Votre expédition a été annulée. Frais d'annulation : " + e.getFraisAnnulation() + " €",
                "TransFret : expédition " + e.getId() + " annulée");
        return true;
    }

    private void libererVehicule(Expedition e) {
        for (Colis c : e.getColis()) {
            e.getVehicule().decharger(c);
        }
        e.getVehicule().setDisponible(true);
    }

    private void notifier(Client client, String sujet, String corps, String sms) {
        EmailService.envoyer(client.getEmail(), sujet, corps);
        if (client.getType() == TypeClient.PREMIUM) {
            new SmsService().envoyerSms(client.getTelephone(), sms);
        }
    }
}
