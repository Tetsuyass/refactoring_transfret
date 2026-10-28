package fr.efrei.transport;

public class TarifAir implements TarifTransport {

    private static final double TARIF_PAR_KM = 2.5;
    private static final double TARIF_PAR_KG = 0.15;
    private static final double FACTEUR_VOLUME = 0.001;

    @Override
    public double calculerTarifBase(int distance, double poids) {
        return distance * TARIF_PAR_KM + poids * TARIF_PAR_KG;
    }

    @Override
    public double getMinimumFacturation() {
        return 0;
    }

    @Override
    public double getFacteurVolume() {
        return FACTEUR_VOLUME;
    }
}
