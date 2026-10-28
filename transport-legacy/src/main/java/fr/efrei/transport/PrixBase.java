package fr.efrei.transport;

public class PrixBase implements Prix {

    private final TarifTransport tarifTransport;
    private final int distance;
    private final double poids;
    private final double volume;

    public PrixBase(TarifTransport tarifTransport, int distance, double poids, double volume) {
        this.tarifTransport = tarifTransport;
        this.distance = distance;
        this.poids = poids;
        this.volume = volume;
    }

    @Override
    public double calculer() {
        double poidsTaxable = poids;
        double facteurVolume = tarifTransport.getFacteurVolume();
        if (facteurVolume > 0 && volume * facteurVolume > poids) {
            poidsTaxable = volume * facteurVolume;
        }
        return tarifTransport.calculerTarifBase(distance, poidsTaxable);
    }
}
