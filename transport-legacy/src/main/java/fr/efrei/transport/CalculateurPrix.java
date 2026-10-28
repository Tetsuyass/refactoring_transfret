package fr.efrei.transport;

import java.util.List;
import java.util.Map;

public class CalculateurPrix {

    private static final double MULTIPLICATEUR_FRAGILE = 1.15;
    private static final double MULTIPLICATEUR_DANGEREUX = 1.30;
    private static final double MULTIPLICATEUR_REFRIGERE = 1.20;
    private static final double MULTIPLICATEUR_URGENT = 1.5;
    private static final double TAUX_ASSURANCE = 0.02;
    private static final double ASSURANCE_MINIMUM = 15.0;

    private final Map<ModeTransport, TarifTransport> tarifsParMode;
    private final Map<TypeClient, PolitiqueRemise> remisesParTypeClient;

    public CalculateurPrix(Map<ModeTransport, TarifTransport> tarifsParMode, Map<TypeClient, PolitiqueRemise> remisesParTypeClient) {
        this.tarifsParMode = tarifsParMode;
        this.remisesParTypeClient = remisesParTypeClient;
    }

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

        // tarif de base selon le mode (stratégie)
        TarifTransport tarif = tarifsParMode.get(mode);
        if (tarif == null) {
            throw new IllegalArgumentException("Mode inconnu : " + mode);
        }
        double poidsTaxable = poids;
        double facteurVolume = tarif.getFacteurVolume();
        if (facteurVolume > 0 && volume * facteurVolume > poids) {
            poidsTaxable = volume * facteurVolume;
        }
        double prix = tarif.calculerTarifBase(distance, poidsTaxable);

        // suppléments
        if (fragile) prix = prix * MULTIPLICATEUR_FRAGILE;
        if (dangereux) prix = prix * MULTIPLICATEUR_DANGEREUX;
        if (refrigere) prix = prix * MULTIPLICATEUR_REFRIGERE;
        if (urgent) prix = prix * MULTIPLICATEUR_URGENT;

        // remise client (stratégie)
        PolitiqueRemise politiqueRemise = remisesParTypeClient.get(typeClient);
        if (politiqueRemise != null) {
            prix = politiqueRemise.appliquerRemise(prix);
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
