package fr.efrei.transport;

import java.util.List;
import java.util.Map;

public class CalculateurPrix {

    private final AssembleurPrix assembleurPrix;

    public CalculateurPrix(Map<ModeTransport, TarifTransport> tarifsParMode, Map<TypeClient, PolitiqueRemise> remisesParTypeClient) {
        this.assembleurPrix = new AssembleurPrix(tarifsParMode, remisesParTypeClient);
    }

    public double calculerPrixHT(List<Colis> colis, int distance, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
        Prix prix = assembleurPrix.assembler(colis, distance, mode, urgent, assurance, typeClient);
        return Utils.arrondir(prix.calculer());
    }
}
