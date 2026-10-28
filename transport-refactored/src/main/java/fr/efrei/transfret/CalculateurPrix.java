package fr.efrei.transfret;

import java.util.List;

public class CalculateurPrix {

    // tarifs de base : prix au km et prix au kg
    private static final double ROUTE_PAR_KM = 1.2;
    private static final double ROUTE_PAR_KG = 0.10;

    private static final double RAIL_PAR_KM = 0.8;
    private static final double RAIL_PAR_KG = 0.07;
    private static final double RAIL_PRIX_MINIMUM = 150;

    private static final double MER_PAR_KM = 0.5;
    private static final double MER_PAR_KG = 0.03;
    private static final double MER_PRIX_MINIMUM = 500;

    private static final double AIR_PAR_KM = 0.6;
    private static final double AIR_PAR_KG_TAXABLE = 4.5;
    private static final double AIR_KG_PAR_M3 = 167; // poids volumétrique

    // suppléments (coefficients multiplicateurs)
    private static final double COEFF_FRAGILE = 1.15;
    private static final double COEFF_DANGEREUX = 1.30;
    private static final double COEFF_REFRIGERE = 1.20;
    private static final double COEFF_URGENT = 1.5;

    // remises client (coefficients multiplicateurs)
    private static final double ENTREPRISE_SEUIL_GROS_MONTANT = 5000;
    private static final double COEFF_ENTREPRISE_GROS_MONTANT = 0.85;
    private static final double COEFF_ENTREPRISE = 0.90;
    private static final double COEFF_PREMIUM = 0.80;

    // assurance
    private static final double TAUX_ASSURANCE = 0.02; // de la valeur déclarée
    private static final double ASSURANCE_MINIMUM = 15;

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
        switch (mode) {
            case ROUTE -> prix = distance * ROUTE_PAR_KM + poids * ROUTE_PAR_KG;
            case RAIL -> {
                prix = distance * RAIL_PAR_KM + poids * RAIL_PAR_KG;
                if (prix < RAIL_PRIX_MINIMUM) prix = RAIL_PRIX_MINIMUM;
            }
            case MER -> {
                prix = distance * MER_PAR_KM + poids * MER_PAR_KG;
                if (prix < MER_PRIX_MINIMUM) prix = MER_PRIX_MINIMUM;
            }
            case AIR -> {
                double poidsTaxable = poids;
                if (volume * AIR_KG_PAR_M3 > poids) {
                    poidsTaxable = volume * AIR_KG_PAR_M3;
                }
                prix = distance * AIR_PAR_KM + poidsTaxable * AIR_PAR_KG_TAXABLE;
            }
            default -> throw new IllegalArgumentException("Mode inconnu : " + mode);
        }

        // suppléments
        if (fragile) prix = prix * COEFF_FRAGILE;
        if (dangereux) prix = prix * COEFF_DANGEREUX;
        if (refrigere) prix = prix * COEFF_REFRIGERE;
        if (urgent) prix = prix * COEFF_URGENT;

        // remise client
        if (typeClient == TypeClient.ENTREPRISE) {
            if (prix > ENTREPRISE_SEUIL_GROS_MONTANT) {
                prix = prix * COEFF_ENTREPRISE_GROS_MONTANT;
            } else {
                prix = prix * COEFF_ENTREPRISE;
            }
        } else if (typeClient == TypeClient.PREMIUM) {
            prix = prix * COEFF_PREMIUM;
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
