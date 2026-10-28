package fr.efrei.transport;

import java.util.List;

public class CalculateurPrix {

    private static final double TARIF_ROUTE_PAR_KM = 1.2;
    private static final double TARIF_ROUTE_PAR_KG = 0.10;
    private static final double TARIF_RAIL_PAR_KM = 0.8;
    private static final double TARIF_RAIL_PAR_KG = 0.07;
    private static final double TARIF_RAIL_MINIMUM = 150.0;
    private static final double TARIF_MER_PAR_KM = 0.5;
    private static final double TARIF_MER_PAR_KG = 0.03;
    private static final double TARIF_MER_MINIMUM = 500.0;
    private static final double TARIF_AIR_PAR_KM = 0.6;
    private static final double TARIF_AIR_PAR_KG = 4.5;
    private static final double FACTEUR_VOLUME_AIR = 167.0;
    private static final double MULTIPLICATEUR_FRAGILE = 1.15;
    private static final double MULTIPLICATEUR_DANGEREUX = 1.30;
    private static final double MULTIPLICATEUR_REFRIGERE = 1.20;
    private static final double MULTIPLICATEUR_URGENT = 1.5;
    private static final double SEUIL_REMISE_ENTREPRISE = 5000.0;
    private static final double REMISE_ENTREPRISE_HAUT = 0.85;
    private static final double REMISE_ENTREPRISE_BAS = 0.90;
    private static final double REMISE_PREMIUM = 0.80;
    private static final double TAUX_ASSURANCE = 0.02;
    private static final double ASSURANCE_MINIMUM = 15.0;

    public double calculerPrixHT(List<Colis> colis, int distance, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
        double poids = 0;
        double volume = 0;
        double valeur = 0;
        boolean fragile = false;
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            poids = poids + c.getPoids();
            volume = volume + c.getVolume();
            valeur = valeur + c.getValeurDeclaree();
            if (c.getType() == TypeMarchandise.FRAGILE) fragile = true;
            if (c.getType() == TypeMarchandise.DANGEREUX) dangereux = true;
            if (c.getType() == TypeMarchandise.REFRIGERE) refrigere = true;
        }

        // tarif de base selon le mode
        double prix;
        if (mode == ModeTransport.ROUTE) {
            prix = distance * TARIF_ROUTE_PAR_KM + poids * TARIF_ROUTE_PAR_KG;
        } else if (mode == ModeTransport.RAIL) {
            prix = distance * TARIF_RAIL_PAR_KM + poids * TARIF_RAIL_PAR_KG;
            if (prix < TARIF_RAIL_MINIMUM) prix = TARIF_RAIL_MINIMUM;
        } else if (mode == ModeTransport.MER) {
            prix = distance * TARIF_MER_PAR_KM + poids * TARIF_MER_PAR_KG;
            if (prix < TARIF_MER_MINIMUM) prix = TARIF_MER_MINIMUM;
        } else if (mode == ModeTransport.AIR) {
            double poidsTaxable = poids;
            if (volume * FACTEUR_VOLUME_AIR > poids) {
                poidsTaxable = volume * FACTEUR_VOLUME_AIR;
            }
            prix = distance * TARIF_AIR_PAR_KM + poidsTaxable * TARIF_AIR_PAR_KG;
        } else {
            throw new IllegalArgumentException("Mode inconnu : " + mode);
        }

        // suppléments
        if (fragile) prix = prix * MULTIPLICATEUR_FRAGILE;
        if (dangereux) prix = prix * MULTIPLICATEUR_DANGEREUX;
        if (refrigere) prix = prix * MULTIPLICATEUR_REFRIGERE;
        if (urgent) prix = prix * MULTIPLICATEUR_URGENT;

        // remise client
        if (typeClient == TypeClient.ENTREPRISE) {
            if (prix > SEUIL_REMISE_ENTREPRISE) {
                prix = prix * REMISE_ENTREPRISE_HAUT;
            } else {
                prix = prix * REMISE_ENTREPRISE_BAS;
            }
        } else if (typeClient == TypeClient.PREMIUM) {
            prix = prix * REMISE_PREMIUM;
        }

        // assurance
        if (assurance) {
            double montantAssurance = valeur * TAUX_ASSURANCE;
            if (montantAssurance < ASSURANCE_MINIMUM) montantAssurance = ASSURANCE_MINIMUM;
            prix = prix + montantAssurance;
        }
        return Utils.arrondir(prix);
    }
}
