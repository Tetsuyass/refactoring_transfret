package fr.efrei.transport;

public class TarifRail implements TarifTransport {

    private static final double TARIF_PAR_KM = 0.8;
    private static final double TARIF_PAR_KG = 0.07;
    private static final double MINIMUM = 150;

    @Override
    public double calculerTarifBase(int distance, double poids) {
        double prix = distance * TARIF_PAR_KM + poids * TARIF_PAR_KG;
        if (prix < MINIMUM) prix = MINIMUM;
        return prix;
    }

    @Override
    public double getMinimumFacturation() {
        return MINIMUM;
    }

    @Override
    public double getFacteurVolume() {
        return 0;
    }
}
