package fr.efrei.transport;

import java.util.List;
import java.util.Map;

public class AssembleurPrix {

    private final Map<ModeTransport, TarifTransport> tarifsParMode;
    private final Map<TypeClient, PolitiqueRemise> remisesParTypeClient;

    public AssembleurPrix(Map<ModeTransport, TarifTransport> tarifsParMode, Map<TypeClient, PolitiqueRemise> remisesParTypeClient) {
        this.tarifsParMode = tarifsParMode;
        this.remisesParTypeClient = remisesParTypeClient;
    }

    public Prix assembler(List<Colis> colis, int distance, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
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

        TarifTransport tarif = tarifsParMode.get(mode);
        if (tarif == null) {
            throw new IllegalArgumentException("Mode inconnu : " + mode);
        }

        Prix prix = new PrixBase(tarif, distance, poids, volume);

        if (fragile) prix = new SupplementFragile(prix);
        if (dangereux) prix = new SupplementDangereux(prix);
        if (refrigere) prix = new SupplementRefrigere(prix);
        if (urgent) prix = new SupplementUrgence(prix);

        PolitiqueRemise politiqueRemise = remisesParTypeClient.get(typeClient);
        if (politiqueRemise != null) {
            prix = new DecorateurRemise(prix, politiqueRemise);
        }

        if (assurance) {
            prix = new DecorateurAssurance(prix, valeur);
        }

        return prix;
    }
}
