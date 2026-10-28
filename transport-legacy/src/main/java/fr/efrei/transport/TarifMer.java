package fr.efrei.transport;

public class TarifMer implements TarifTransport {

    private static final double TARIF_PAR_KM = 0.4;
    private static final double TARIF_PAR_KG = 0.05;
    private static final double MINIMUM = 167;

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
