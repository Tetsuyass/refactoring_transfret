package fr.efrei.transport;

public class TarifRoute implements TarifTransport {

    private static final double TARIF_PAR_KM = 1.2;
    private static final double TARIF_PAR_KG = 0.10;

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
        return 0;
    }
}
