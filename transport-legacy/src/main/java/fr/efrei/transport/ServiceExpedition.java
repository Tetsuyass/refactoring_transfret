package fr.efrei.transport;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ServiceExpedition {

    private static final double TAUX_TVA = 1.2;
    private static final double FRAIS_ANNULATION_EN_COURS = 0.5;
    private static final double FACTEUR_KM_MILLE = 1.852;
    private static final int ALTITUDE_VOL = 35000;
    private static final int DISTANCE_ROULEE_AVION = 2;
    private static final int TEMPERATURE_REFRIGERATION = 4;

    private final ExpeditionRepository expeditionRepository;
    private final CalculateurPrix calculateur;
    private final AffectationVehicule affectation;
    private final ValidateurExpedition validateur;
    private final EstimateurDelai estimateurDelai;
    private final Notificateur notificateur;
    private final GenerateurFacture generateurFacture;
    private int compteur;

    public ServiceExpedition(ExpeditionRepository expeditionRepository, CalculateurPrix calculateur,
                           AffectationVehicule affectation, ValidateurExpedition validateur,
                           EstimateurDelai estimateurDelai, Notificateur notificateur,
                           GenerateurFacture generateurFacture, int compteurInitial) {
        this.expeditionRepository = expeditionRepository;
        this.calculateur = calculateur;
        this.affectation = affectation;
        this.validateur = validateur;
        this.estimateurDelai = estimateurDelai;
        this.notificateur = notificateur;
        this.generateurFacture = generateurFacture;
        this.compteur = compteurInitial;
    }

    public Expedition creerExpedition(Client client, List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode, boolean urgent, boolean assurance) {
        String erreur = validateur.valider(client, colis, villeDepart, villeArrivee, mode, urgent);
        if (erreur != null) {
            System.out.println(erreur);
            return null;
        }
        int distance = Utils.getDistance(villeDepart, villeArrivee);

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
        e.setId("EXP-" + compteur + "-" + (1000 + new Random().nextInt(9000)));
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
        e.setPrixTTC(Utils.arrondir(e.getPrixHT() * TAUX_TVA));
        e.setDateCreation(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(e.getDateCreation());
        cal.add(Calendar.DAY_OF_MONTH, estimateurDelai.calculerDelai(distance, mode, urgent));
        e.setDateLivraisonPrevue(cal.getTime());

        expeditionRepository.sauvegarder(e);
        notificateur.notifierCreation(client, e.getId(), villeDepart, villeArrivee, mode, e.getPrixTTC(), Utils.formaterDate(e.getDateLivraisonPrevue()));
        return e;
    }

    // retourne le prix TTC, -1 si le trajet est inconnu
    public double calculerDevis(List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == -1) {
            return -1;
        }
        double ht = calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, typeClient);
        return Utils.arrondir(ht * TAUX_TVA);
    }

    // retourne le délai de livraison en jours
    public int calculerDelai(int distance, ModeTransport mode, boolean urgent) {
        return estimateurDelai.calculerDelai(distance, mode, urgent);
    }

    public boolean demarrerExpedition(String id) {
        Expedition e = expeditionRepository.trouverParId(id);
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
            v.naviguer((int) (e.getDistance() / FACTEUR_KM_MILLE));
        } else if (e.getMode() == ModeTransport.AIR) {
            v.rouler(DISTANCE_ROULEE_AVION);
            v.voler(ALTITUDE_VOL);
        }
        for (Colis c : e.getColis()) {
            if (c.getType() == TypeMarchandise.REFRIGERE) {
                v.activerRefrigeration(TEMPERATURE_REFRIGERATION);
                break;
            }
        }
        expeditionRepository.mettreAJourStatut(e);
        notificateur.notifierDepart(e.getClient(), e.getId(), e.getVilleDepart());
        return true;
    }

    public boolean livrerExpedition(String id) {
        Expedition e = expeditionRepository.trouverParId(id);
        if (e == null || e.getStatut() != StatutExpedition.EN_COURS) {
            System.out.println("ERREUR : impossible de livrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.LIVREE);
        libererVehicule(e);
        expeditionRepository.mettreAJourStatut(e);
        notificateur.notifierLivraison(e.getClient(), e.getId(), e.getVilleArrivee());
        return true;
    }

    public boolean annulerExpedition(String id) {
        Expedition e = expeditionRepository.trouverParId(id);
        if (e == null || e.getStatut() == StatutExpedition.LIVREE || e.getStatut() == StatutExpedition.ANNULEE) {
            System.out.println("ERREUR : impossible d'annuler l'expédition " + id);
            return false;
        }
        if (e.getStatut() == StatutExpedition.EN_COURS) {
            // 50% de frais si déjà partie
            e.setFraisAnnulation(Utils.arrondir(e.getPrixTTC() * FRAIS_ANNULATION_EN_COURS));
        }
        e.setStatut(StatutExpedition.ANNULEE);
        libererVehicule(e);
        expeditionRepository.mettreAJourStatut(e);
        notificateur.notifierAnnulation(e.getClient(), e.getId(), e.getFraisAnnulation());
        return true;
    }

    private void libererVehicule(Expedition e) {
        for (Colis c : e.getColis()) {
            e.getVehicule().decharger(c);
        }
        e.getVehicule().setDisponible(true);
    }

    public String genererFacture(Expedition expedition) {
        return generateurFacture.generer(expedition);
    }

    public int getCompteur() {
        return compteur;
    }
}
